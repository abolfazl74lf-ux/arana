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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Yard
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
import com.example.data.local.entity.GardenCalendarItem
import com.example.data.local.entity.MaintenanceItem
import com.example.data.local.entity.ToolItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YardScreen(
    viewModel: YardViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gardenTasks by viewModel.gardenTasks.collectAsStateWithLifecycle()
    val maintenanceItems by viewModel.maintenanceItems.collectAsStateWithLifecycle()
    val tools by viewModel.tools.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddGardenDialog by remember { mutableStateOf(false) }
    var showAddMaintenanceDialog by remember { mutableStateOf(false) }
    var showAddToolDialog by remember { mutableStateOf(false) }

    val tabs = listOf("تقویم باغبانی", "تعمیرات نگهداری", "مدیریت ابزارها")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("حیاط و محوطه بیرون", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("باغبانی فصلی، تعمیرات دوره‌ای و مدیریت فنی", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("yard_back_button")) {
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
                        0 -> showAddGardenDialog = true
                        1 -> showAddMaintenanceDialog = true
                        2 -> showAddToolDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("yard_fab_add")
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
                modifier = Modifier.fillMaxWidth().testTag("yard_tab_row")
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
                0 -> GardenCalendarTab(
                    tasks = gardenTasks,
                    onToggle = { viewModel.toggleGardenTask(it) }
                )
                1 -> MaintenanceTab(
                    items = maintenanceItems,
                    onToggle = { viewModel.toggleMaintenanceItem(it) }
                )
                2 -> ToolsManagementTab(
                    tools = tools,
                    onToggle = { viewModel.toggleToolAvailability(it) }
                )
            }
        }
    }

    if (showAddGardenDialog) {
        AddGardenTaskDialog(
            onDismiss = { showAddGardenDialog = false },
            onConfirm = { title, area, season, date ->
                viewModel.addGardenTask(title, area, season, date)
                showAddGardenDialog = false
            }
        )
    }

    if (showAddMaintenanceDialog) {
        AddMaintenanceDialog(
            onDismiss = { showAddMaintenanceDialog = false },
            onConfirm = { title, freq, status ->
                viewModel.addMaintenanceItem(title, freq, status)
                showAddMaintenanceDialog = false
            }
        )
    }

    if (showAddToolDialog) {
        AddToolDialog(
            onDismiss = { showAddToolDialog = false },
            onConfirm = { name, loc, cat ->
                viewModel.addTool(name, loc, cat)
                showAddToolDialog = false
            }
        )
    }
}

@Composable
private fun GardenCalendarTab(
    tasks: List<GardenCalendarItem>,
    onToggle: (GardenCalendarItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("garden_tasks_list"),
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
                            text = task.taskTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (task.isDone) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "بخش: ${task.targetAreaOrPlant} • فصل: ${task.seasonTag}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "زمان‌بندی: ${task.scheduledDate}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF00796B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceTab(
    items: List<MaintenanceItem>,
    onToggle: (MaintenanceItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("maintenance_list"),
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
                    Checkbox(checked = item.isDone, onCheckedChange = { onToggle(item) })
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
                            text = "دوره بازرسی: ${item.frequencyLabel} • وضعیت: ${item.statusText}",
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
private fun ToolsManagementTab(
    tools: List<ToolItem>,
    onToggle: (ToolItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("tools_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tools, key = { it.id }) { tool ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE0F2F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Handyman, contentDescription = null, tint = Color(0xFF00796B))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tool.toolName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "جایگاه: ${tool.storageLocation} • دسته: ${tool.category}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (tool.isAvailable) "موجود در جایگاه" else "در حال استفاده",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (tool.isAvailable) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                        onClick = { onToggle(tool) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (tool.isAvailable) "امانت گرفتن" else "بازگرداندن")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddGardenTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, area: String, season: String, date: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("باغچه جلو") }
    var season by remember { mutableStateOf("بهار") }
    var date by remember { mutableStateOf("اوایل اردیبهشت") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن فعالیت به تقویم باغبانی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان کار (هرس، سمپاشی، کاشت)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("بخش حیاط یا گیاه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = season, onValueChange = { season = it }, label = { Text("فصل") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("زمان‌بندی") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, area, season, date) }) { Text("ثبت") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddMaintenanceDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, freq: String, status: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var freq by remember { mutableStateOf("فصلی") }
    var status by remember { mutableStateOf("نیاز به بررسی") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن مورد به چک‌لیست تعمیرات") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان تعمیرات (ناودان، لامپ، رنگ)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = freq, onValueChange = { freq = it }, label = { Text("دوره بررسی") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("وضعیت فعلی") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, freq, status) }) { Text("ثبت") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddToolDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, loc: String, cat: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var loc by remember { mutableStateOf("قفسه انبار حیاط") }
    var cat by remember { mutableStateOf("باغبانی") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت ابزار جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام ابزار") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("محل نگهداری و قفسه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("دسته‌بندی (فنی/باغبانی/ایمنی)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name, loc, cat) }) { Text("ثبت ابزار") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
