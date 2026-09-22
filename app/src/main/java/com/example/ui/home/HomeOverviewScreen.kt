package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.HousekeepingTricksProvider
import com.example.domain.model.RoomType
import com.example.ui.components.AnimatedIsometricHouse
import com.example.ui.components.HousekeepingTrickDetailDialog
import com.example.ui.components.RoomCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeOverviewScreen(
    viewModel: HomeViewModel,
    onNavigateToRoom: (RoomType) -> Unit,
    onNavigateToPersonalCare: () -> Unit = {},
    onNavigateToDailyTimeline: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats by viewModel.overviewStats.collectAsStateWithLifecycle()
    val roomImageUris by viewModel.roomImageUris.collectAsStateWithLifecycle()
    val trickIndices by viewModel.trickIndices.collectAsStateWithLifecycle()
    val selectedTrickForDetails by viewModel.selectedTrickForDetails.collectAsStateWithLifecycle()
    var selectedHighlightRoom by remember { mutableStateOf<RoomType?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        // 1. Calendar/Timeline Icon Button -> Daily Timeline
                        IconButton(
                            onClick = onNavigateToDailyTimeline,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("nav_btn_daily_timeline")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "تایملاین روزانه",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // 3. Home Icon -> Active Indicator for Current Screen
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("nav_btn_home_active"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "صفحه اصلی",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // 2. Profile/Person Icon Button -> Personal Care Space
                        IconButton(
                            onClick = onNavigateToPersonalCare,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .testTag("nav_btn_personal_care")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "فضای شخصی مدیر خانه",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.testTag("home_top_app_bar")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("home_overview_scroll_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Quick Status / Daily Summary Card
            item {
                HomeDailyBriefCard(stats = stats)
            }

            // 2. Interactive Isometric House Model (Exploded view)
            item {
                AnimatedIsometricHouse(
                    stats = stats,
                    selectedRoom = selectedHighlightRoom,
                    onRoomClick = { room ->
                        selectedHighlightRoom = room
                        onNavigateToRoom(room)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Directly Room Submenus List without any intermediate text headers
            items(RoomType.values()) { room ->
                val statusText = when (room) {
                    RoomType.KITCHEN -> if (stats.kitchenExpiringCount > 0) "${stats.kitchenExpiringCount} ماده رو به اتمام یا انقضا" else "موجودی و وعده‌ها منظم است"
                    RoomType.LIVING_ROOM -> if (stats.plantsNeedingWaterCount > 0) "${stats.plantsNeedingWaterCount} گیاه نیازمند آبیاری" else "محیط آرام و تمیز"
                    RoomType.BEDROOM -> if (stats.bedroomPendingRoutinesCount > 0) "${stats.bedroomPendingRoutinesCount} روتین روزانه مانده" else "کمد و روتین مرتب"
                    RoomType.KIDS_ROOM -> if (stats.kidsPendingChores > 0) "${stats.kidsPendingChores} وظیفه امروز | ${stats.kidsCompletedPoints} امتیاز کسب شده" else "امتیاز کامل امروز کسب شد ⭐"
                    RoomType.YARD -> if (stats.yardMaintenancePendingCount > 0) "${stats.yardMaintenancePendingCount} مورد نیاز به سرویس فصلی" else "باغچه و ابزارها آماده"
                }

                val alertCount = when (room) {
                    RoomType.KITCHEN -> stats.kitchenExpiringCount
                    RoomType.LIVING_ROOM -> stats.plantsNeedingWaterCount
                    RoomType.BEDROOM -> stats.bedroomPendingRoutinesCount
                    RoomType.KIDS_ROOM -> stats.kidsPendingChores
                    RoomType.YARD -> stats.yardMaintenancePendingCount
                }

                val currentTrick = HousekeepingTricksProvider.getDailyTrickForRoom(
                    room = room,
                    daySeedOffset = trickIndices[room] ?: 0
                )

                RoomCard(
                    room = room,
                    statusText = statusText,
                    alertCount = alertCount,
                    customImageUri = roomImageUris[room],
                    currentTrick = currentTrick,
                    onImageSelected = { uri ->
                        viewModel.setRoomImageUri(room, uri.toString())
                    },
                    onNextTrick = {
                        viewModel.nextTrickForRoom(room)
                    },
                    onOpenTrickDetails = { trick ->
                        viewModel.openTrickDetails(trick)
                    },
                    onClick = { onNavigateToRoom(room) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        // Trick Full Details Dialog
        selectedTrickForDetails?.let { trick ->
            HousekeepingTrickDetailDialog(
                trick = trick,
                onDismiss = { viewModel.dismissTrickDetails() }
            )
        }
    }
}

@Composable
private fun HomeDailyBriefCard(
    stats: HomeOverviewStats,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_daily_brief_card")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "خلاصه وضعیت امروز خانه",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                val summaryText = buildString {
                    if (stats.kitchenExpiringCount > 0) append("${stats.kitchenExpiringCount} انقضای نزدیک • ")
                    if (stats.plantsNeedingWaterCount > 0) append("${stats.plantsNeedingWaterCount} آبیاری گیاه • ")
                    if (stats.kidsPendingChores > 0) append("${stats.kidsPendingChores} وظیفه کودک • ")
                    if (isEmpty()) append("تمامی امور خانه روی روال و منظم است ✨")
                }
                Text(
                    text = summaryText.trimEnd(' ', '•'),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
