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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.local.entity.PersonalRoutineItem
import com.example.data.local.entity.SpaceDeclutterItem
import com.example.data.local.entity.WardrobeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BedroomScreen(
    viewModel: BedroomViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wardrobeItems by viewModel.wardrobeItems.collectAsStateWithLifecycle()
    val personalRoutines by viewModel.personalRoutines.collectAsStateWithLifecycle()
    val declutterItems by viewModel.declutterItems.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddWardrobeDialog by remember { mutableStateOf(false) }
    var showAddRoutineDialog by remember { mutableStateOf(false) }
    var showAddDeclutterDialog by remember { mutableStateOf(false) }

    val tabs = listOf("کمد و شستشو", "روتین‌های شخصی", "مرتب‌سازی فضا")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("اتاق خواب", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("مدیریت البسه، نظم کمدها و آرامش فردی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("bedroom_back_button")) {
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
                        0 -> showAddWardrobeDialog = true
                        1 -> showAddRoutineDialog = true
                        2 -> showAddDeclutterDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("bedroom_fab_add")
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
                modifier = Modifier.fillMaxWidth().testTag("bedroom_tab_row")
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
                0 -> WardrobeCareTab(
                    items = wardrobeItems,
                    onToggle = { viewModel.toggleWardrobeItem(it) }
                )
                1 -> PersonalRoutinesTab(
                    routines = personalRoutines,
                    onToggle = { viewModel.togglePersonalRoutine(it) }
                )
                2 -> SpaceDeclutterTab(
                    items = declutterItems,
                    onToggle = { viewModel.toggleDeclutterItem(it) }
                )
            }
        }
    }

    if (showAddWardrobeDialog) {
        AddWardrobeItemDialog(
            onDismiss = { showAddWardrobeDialog = false },
            onConfirm = { title, season, care, date ->
                viewModel.addWardrobeItem(title, season, care, date)
                showAddWardrobeDialog = false
            }
        )
    }

    if (showAddRoutineDialog) {
        AddRoutineDialog(
            onDismiss = { showAddRoutineDialog = false },
            onConfirm = { title, time ->
                viewModel.addPersonalRoutine(title, time)
                showAddRoutineDialog = false
            }
        )
    }

    if (showAddDeclutterDialog) {
        AddDeclutterDialog(
            onDismiss = { showAddDeclutterDialog = false },
            onConfirm = { area, priority, notes ->
                viewModel.addDeclutterItem(area, priority, notes)
                showAddDeclutterDialog = false
            }
        )
    }
}

@Composable
private fun WardrobeCareTab(
    items: List<WardrobeItem>,
    onToggle: (WardrobeItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("wardrobe_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
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
                    IconButton(onClick = { onToggle(item) }) {
                        Icon(
                            imageVector = if (item.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (item.isDone) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "فصل: ${item.season} • نحوه نگهداری: ${item.careType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "زمان‌بندی: ${item.nextScheduleDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalRoutinesTab(
    routines: List<PersonalRoutineItem>,
    onToggle: (PersonalRoutineItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("personal_routines_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(routines, key = { it.id }) { routine ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (routine.isDoneToday) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = routine.isDoneToday,
                        onCheckedChange = { onToggle(routine) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (routine.isDoneToday) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "زمان: ${routine.timeOfDay}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFE65100),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${routine.streakDays} روز پیوستگی",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpaceDeclutterTab(
    items: List<SpaceDeclutterItem>,
    onToggle: (SpaceDeclutterItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("declutter_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isOrganized) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isOrganized,
                        onCheckedChange = { onToggle(item) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.areaName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isOrganized) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "اولویت: ${item.priority}${if (item.reminderNotes.isNotBlank()) " • نکته: ${item.reminderNotes}" else ""}",
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
private fun AddWardrobeItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, season: String, care: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var season by remember { mutableStateOf("پاییزه/زمستانه") }
    var care by remember { mutableStateOf("خشکشویی") }
    var date by remember { mutableStateOf("آخر ماه") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن لباس یا ملحفه به برنامه شستشو") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان لباس/ملحفه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("فصل") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = care, onValueChange = { care = it }, label = { Text("نحوه شستشو") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("زمان برنامه‌ریزی") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, season, care, date) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddRoutineDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, time: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("صبح") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن روتین شخصی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان روتین") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("زمان (صبح / شب)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, time) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddDeclutterDialog(
    onDismiss: () -> Unit,
    onConfirm: (area: String, priority: String, notes: String) -> Unit
) {
    var area by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("متوسط") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن کشو یا فضای نیازمند نظم") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("نام فضا یا کشو") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = priority, onValueChange = { priority = it }, label = { Text("اولویت (بالا/متوسط/پایین)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("یادداشت نظم‌دهی") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (area.isNotBlank()) onConfirm(area, priority, notes) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
