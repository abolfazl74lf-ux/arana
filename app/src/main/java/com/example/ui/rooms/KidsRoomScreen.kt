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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.ChoreItem
import com.example.data.local.entity.KidsShoppingItem
import com.example.data.local.entity.StudyTask

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsRoomScreen(
    viewModel: KidsRoomViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chores by viewModel.chores.collectAsStateWithLifecycle()
    val studyTasks by viewModel.studyTasks.collectAsStateWithLifecycle()
    val kidsShopping by viewModel.kidsShopping.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddChoreDialog by remember { mutableStateOf(false) }
    var showAddStudyDialog by remember { mutableStateOf(false) }
    var showAddShoppingDialog by remember { mutableStateOf(false) }

    val totalPoints = chores.filter { it.isCompleted }.sumOf { it.rewardPoints }
    val tabs = listOf("چارت وظایف و امتیاز", "ردیاب تحصیلی", "لیست خرید اختصاصی")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("اتاق بچه‌ها", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("چارت مسئولیت‌پذیری، تکالیف و نیازها", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("kids_back_button")) {
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
                        0 -> showAddChoreDialog = true
                        1 -> showAddStudyDialog = true
                        2 -> showAddShoppingDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("kids_fab_add")
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
                modifier = Modifier.fillMaxWidth().testTag("kids_tab_row")
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
                0 -> ChoreChartTab(
                    chores = chores,
                    totalPoints = totalPoints,
                    onToggle = { viewModel.toggleChore(it) }
                )
                1 -> StudyTrackerTab(
                    tasks = studyTasks,
                    onToggle = { viewModel.toggleStudyTask(it) }
                )
                2 -> KidsShoppingTab(
                    items = kidsShopping,
                    onToggle = { viewModel.toggleKidsShopping(it) }
                )
            }
        }
    }

    if (showAddChoreDialog) {
        AddChoreDialog(
            onDismiss = { showAddChoreDialog = false },
            onConfirm = { title, child, points, emoji ->
                viewModel.addChore(title, child, points, emoji)
                showAddChoreDialog = false
            }
        )
    }

    if (showAddStudyDialog) {
        AddStudyTaskDialog(
            onDismiss = { showAddStudyDialog = false },
            onConfirm = { subject, desc, due ->
                viewModel.addStudyTask(subject, desc, due)
                showAddStudyDialog = false
            }
        )
    }

    if (showAddShoppingDialog) {
        AddKidsShoppingDialog(
            onDismiss = { showAddShoppingDialog = false },
            onConfirm = { item, cat, child ->
                viewModel.addKidsShopping(item, cat, child)
                showAddShoppingDialog = false
            }
        )
    }
}

@Composable
private fun ChoreChartTab(
    chores: List<ChoreItem>,
    totalPoints: Int,
    onToggle: (ChoreItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("chores_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB300)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("مجموع امتیازهای کودک", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFFE65100))
                        Text(
                            text = "$totalPoints ستاره طلایی کسب شده!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFFF57F17)
                        )
                    }
                }
            }
        }

        items(chores, key = { it.id }) { chore ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (chore.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggle(chore) }) {
                        Icon(
                            imageVector = if (chore.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (chore.isCompleted) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = chore.emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = chore.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (chore.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "مسئول: ${chore.childName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF3E0))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("+${chore.rewardPoints} امتیاز", color = Color(0xFFE65100), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyTrackerTab(
    tasks: List<StudyTask>,
    onToggle: (StudyTask) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("study_tasks_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (task.isFinished) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = task.isFinished, onCheckedChange = { onToggle(task) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    task.subject,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("مهلت: ${task.dueDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.taskDescription,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (task.isFinished) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KidsShoppingTab(
    items: List<KidsShoppingItem>,
    onToggle: (KidsShoppingItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("kids_shopping_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isPurchased) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = item.isPurchased, onCheckedChange = { onToggle(item) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isPurchased) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "دسته‌بندی: ${item.category} • برای: ${item.childName}",
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
private fun AddChoreDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, child: String, points: Int, emoji: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var child by remember { mutableStateOf("آرتین") }
    var points by remember { mutableStateOf("15") }
    var emoji by remember { mutableStateOf("⭐") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن وظیفه به چارت کودک") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان کار (جمع کردن اسباب‌بازی‌ها)") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = child, onValueChange = { child = it }, label = { Text("نام کودک") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = points, onValueChange = { points = it }, label = { Text("امتیاز جایزه") }, modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("ایموجی") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, child, points.toIntOrNull() ?: 10, emoji) }) { Text("ثبت") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddStudyTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (subject: String, desc: String, due: String) -> Unit
) {
    var subject by remember { mutableStateOf("ریاضی") }
    var desc by remember { mutableStateOf("") }
    var due by remember { mutableStateOf("فردا") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن تکلیف تحصیلی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("درس / مبحث") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("شرح تکلیف یا پروژه") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = due, onValueChange = { due = it }, label = { Text("مهلت تحویل") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (desc.isNotBlank()) onConfirm(subject, desc, due) }) { Text("ثبت") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
private fun AddKidsShoppingDialog(
    onDismiss: () -> Unit,
    onConfirm: (item: String, cat: String, child: String) -> Unit
) {
    var item by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("لوازم‌التحریر") }
    var child by remember { mutableStateOf("آرتین") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن به لیست خرید کودک") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = item, onValueChange = { item = it }, label = { Text("نام کالا") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("دسته‌بندی (لوازم‌التحریر/پوشاک/بهداشتی)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = child, onValueChange = { child = it }, label = { Text("برای کودک") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (item.isNotBlank()) onConfirm(item, cat, child) }) { Text("افزودن") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
