package com.example.ui.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.GuestChecklistItem
import com.example.data.local.entity.LivingCleaningTask
import com.example.data.local.entity.PlantCareItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivingRoomScreen(
    viewModel: LivingRoomViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cleaningTasks by viewModel.livingCleaningTasks.collectAsStateWithLifecycle()
    val plants by viewModel.plants.collectAsStateWithLifecycle()
    val guestChecklist by viewModel.guestChecklist.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddCleaningDialog by remember { mutableStateOf(false) }
    var showAddPlantDialog by remember { mutableStateOf(false) }
    var showAddGuestDialog by remember { mutableStateOf(false) }

    val tabs = listOf("روتین‌های نظافت", "گیاهان آپارتمانی", "چک‌لیست مهمان")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("پذیرایی و نشیمن", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("نظافت دوره‌ای، گیاهان و تدارک مهمانی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("living_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    when (selectedTabIndex) {
                        0 -> showAddCleaningDialog = true
                        1 -> showAddPlantDialog = true
                        2 -> showAddGuestDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("living_fab_add")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth().testTag("living_tab_row")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> LivingCleaningTab(
                    tasks = cleaningTasks,
                    onToggle = { viewModel.toggleLivingCleaningTask(it) }
                )
                1 -> PlantsCareTab(
                    plants = plants,
                    onWater = { viewModel.waterPlant(it) },
                    onToggleLeaf = { viewModel.togglePlantLeafClean(it) },
                    onToggleFertilizer = { viewModel.togglePlantFertilizer(it) }
                )
                2 -> GuestChecklistTab(
                    items = guestChecklist,
                    onToggle = { viewModel.toggleGuestChecklist(it) }
                )
            }
        }
    }

    if (showAddCleaningDialog) {
        AddCleaningTaskDialog(
            onDismiss = { showAddCleaningDialog = false },
            onConfirm = { title, freq, mins ->
                viewModel.addLivingCleaningTask(title, freq, mins)
                showAddCleaningDialog = false
            }
        )
    }

    if (showAddPlantDialog) {
        AddPlantDialog(
            onDismiss = { showAddPlantDialog = false },
            onConfirm = { name, loc, interval, sun ->
                viewModel.addPlant(name, loc, interval, sun)
                showAddPlantDialog = false
            }
        )
    }

    if (showAddGuestDialog) {
        AddGuestItemDialog(
            onDismiss = { showAddGuestDialog = false },
            onConfirm = { title, cat ->
                viewModel.addGuestChecklist(title, cat)
                showAddGuestDialog = false
            }
        )
    }
}

@Composable
private fun LivingCleaningTab(
    tasks: List<LivingCleaningTask>,
    onToggle: (LivingCleaningTask) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("living_cleaning_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggle(task) }) {
                        Icon(
                            imageVector = if (task.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (task.isDone) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (task.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "تکرار: ${task.frequency} • زمان تقریبی: ${task.estimatedMinutes} دقیقه",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlantsCareTab(
    plants: List<PlantCareItem>,
    onWater: (PlantCareItem) -> Unit,
    onToggleLeaf: (PlantCareItem) -> Unit,
    onToggleFertilizer: (PlantCareItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("living_plants_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(plants, key = { it.id }) { plant ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.LocalFlorist, contentDescription = null, tint = Color(0xFF2E7D32))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(plant.plantName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text("موقعیت: ${plant.location} • ${plant.sunlightLevel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (plant.daysUntilWatering <= 1) "⚠️ امروز نیاز به آبیاری دارد!"
                            else "${plant.daysUntilWatering} روز تا آبیاری بعدی (هر ${plant.waterIntervalDays} روز)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = if (plant.daysUntilWatering <= 1) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                        )
                        Button(
                            onClick = { onWater(plant) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("آب دادم")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = plant.needsLeafCleaning,
                            onClick = { onToggleLeaf(plant) },
                            label = { Text("گردگیری برگ‌ها") }
                        )
                        FilterChip(
                            selected = plant.needsFertilizer,
                            onClick = { onToggleFertilizer(plant) },
                            label = { Text("کوددهی فصلی") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestChecklistTab(
    items: List<GuestChecklistItem>,
    onToggle: (GuestChecklistItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("guest_checklist_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.People, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "برنامه‌ریزی و چک‌لیست قبل از رسیدن مهمانان برای میزبانی عالی",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isDone,
                        onCheckedChange = { onToggle(item) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.taskTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "دسته‌بندی: ${item.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCleaningTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, freq: String, mins: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("روزانه") }
    var mins by remember { mutableStateOf("15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن روتین نظافت پذیرایی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان کار (مثلاً جاروبرقی)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = freq, onValueChange = { freq = it }, label = { Text("دوره تکرار (روزانه/هفتگی)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = mins, onValueChange = { mins = it }, label = { Text("زمان تقریبی (دقیقه)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, freq, mins.toIntOrNull() ?: 15) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, loc: String, interval: Int, sun: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("کنار پنجره") }
    var interval by remember { mutableStateOf("7") }
    var sunlight by remember { mutableStateOf("نور متوسط") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن گیاه جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام گیاه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("مکان در خانه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = interval, onValueChange = { interval = it }, label = { Text("فاصله آبیاری (روز)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sunlight, onValueChange = { sunlight = it }, label = { Text("میزان نور") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name, location, interval.toIntOrNull() ?: 7, sunlight) }) { Text("افزودن گیاه") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddGuestItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, cat: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("پذیرایی") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن مورد به چک‌لیست مهمان") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان کار") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("دسته‌بندی (تدارکات/نظافت)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, category) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
