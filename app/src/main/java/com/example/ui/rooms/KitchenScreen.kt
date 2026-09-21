package com.example.ui.rooms

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
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
import com.example.data.local.entity.ApplianceCleanRoutine
import com.example.data.local.entity.MealPlanItem
import com.example.data.local.entity.PantryItem
import com.example.data.local.entity.SmartShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenScreen(
    viewModel: KitchenViewModel = viewModel(),
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pantryItems by viewModel.pantryItems.collectAsStateWithLifecycle()
    val shoppingItems by viewModel.shoppingItems.collectAsStateWithLifecycle()
    val mealPlans by viewModel.mealPlans.collectAsStateWithLifecycle()
    val applianceRoutines by viewModel.applianceRoutines.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddPantryDialog by remember { mutableStateOf(false) }
    var showAddShoppingDialog by remember { mutableStateOf(false) }
    var showAddMealDialog by remember { mutableStateOf(false) }

    val tabs = listOf("موجودی مواد غذایی", "لیست خرید هوشمند", "برنامه غذایی هفتگی", "نظافت وسایل")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("آشپزخانه", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("مدیریت مواد، خرید و نظافت", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("kitchen_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (selectedTabIndex in 0..2) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTabIndex) {
                            0 -> showAddPantryDialog = true
                            1 -> showAddShoppingDialog = true
                            2 -> showAddMealDialog = true
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("kitchen_fab_add")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "افزودن مورد جدید")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth().testTag("kitchen_tab_row")
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
                0 -> PantryListTab(
                    items = pantryItems,
                    onDelete = { viewModel.deletePantryItem(it) }
                )
                1 -> SmartShoppingTab(
                    items = shoppingItems,
                    onToggle = { viewModel.toggleShoppingItem(it) },
                    onDelete = { viewModel.deleteShoppingItem(it) }
                )
                2 -> MealPlanTab(
                    meals = mealPlans,
                    onDelete = { viewModel.deleteMealPlan(it) }
                )
                3 -> ApplianceRoutineTab(
                    routines = applianceRoutines,
                    onToggle = { viewModel.toggleApplianceClean(it) }
                )
            }
        }
    }

    if (showAddPantryDialog) {
        AddPantryItemDialog(
            onDismiss = { showAddPantryDialog = false },
            onConfirm = { name, cat, qty, unit, exp, lowStock ->
                viewModel.addPantryItem(name, cat, qty, unit, exp, lowStock)
                showAddPantryDialog = false
            }
        )
    }

    if (showAddShoppingDialog) {
        AddShoppingItemDialog(
            onDismiss = { showAddShoppingDialog = false },
            onConfirm = { name, qty, cat ->
                viewModel.addShoppingItem(name, qty, cat)
                showAddShoppingDialog = false
            }
        )
    }

    if (showAddMealDialog) {
        AddMealPlanDialog(
            onDismiss = { showAddMealDialog = false },
            onConfirm = { day, meal, recipe, time, note ->
                viewModel.addMealPlan(day, meal, recipe, time, note)
                showAddMealDialog = false
            }
        )
    }
}

@Composable
private fun PantryListTab(
    items: List<PantryItem>,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("pantry_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        val expiringCount = items.count { it.expiryDaysLeft <= 3 }
        if (expiringCount > 0) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "هشدار فساد: $expiringCount ماده غذایی کمتر از ۳ روز تا انقضا دارند!",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        items(items, key = { it.id }) { item ->
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
                            .clip(CircleShape)
                            .background(
                                if (item.expiryDaysLeft <= 3) MaterialTheme.colorScheme.errorContainer
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Kitchen,
                            contentDescription = null,
                            tint = if (item.expiryDaysLeft <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "${item.quantity} ${item.unit} • ${item.category} (${item.storageLocation})",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (item.expiryDaysLeft <= 0) "منقضی شده!" else "${item.expiryDaysLeft} روز مانده به انقضا",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (item.expiryDaysLeft <= 3) MaterialTheme.colorScheme.error else Color(0xFF388E3C)
                        )
                    }
                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartShoppingTab(
    items: List<SmartShoppingItem>,
    onToggle: (SmartShoppingItem) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("shopping_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "مواد غذایی که رو به اتمام هستند به طور خودکار به این لیست اضافه می‌شوند.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (item.isBought) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggle(item) }) {
                        Icon(
                            imageVector = if (item.isBought) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (item.isBought) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (item.isBought) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${item.quantity} • ${item.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.isAutoGenerated) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Text(
                                        text = "هوشمند (کمبود موجودی)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                    IconButton(onClick = { onDelete(item.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun MealPlanTab(
    meals: List<MealPlanItem>,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("meal_plan_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(meals, key = { it.id }) { meal ->
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
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${meal.dayOfWeek} (${meal.mealType})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "زمان: ${meal.prepTimeMinutes} دقیقه", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(meal.recipeTitle, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        if (meal.notes.isNotBlank()) {
                            Text(meal.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = { onDelete(meal.id) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplianceRoutineTab(
    routines: List<ApplianceCleanRoutine>,
    onToggle: (ApplianceCleanRoutine) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("appliance_routine_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(routines, key = { it.id }) { routine ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { onToggle(routine) }) {
                        Icon(
                            imageVector = if (routine.isCompleted) Icons.Default.CheckCircle else Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = if (routine.isCompleted) Color(0xFF388E3C) else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(routine.applianceName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = "دوره نظافت: هر ${routine.frequencyDays} روز • آخرین بار: ${routine.lastCleanedDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (routine.daysRemaining <= 3) "زمان سرویس فرا رسیده! (${routine.daysRemaining} روز مانده)"
                            else "${routine.daysRemaining} روز تا نظافت بعدی",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (routine.daysRemaining <= 3) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                    Button(
                        onClick = { onToggle(routine) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (routine.isCompleted) "انجام شد" else "ثبت نظافت")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddPantryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, quantity: Double, unit: String, expiryDays: Int, isLowStock: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("یخچال") }
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("عدد") }
    var expiryDaysText by remember { mutableStateOf("7") }
    var isLowStock by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن ماده غذایی جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام ماده غذایی") },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_input_name")
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("مقدار") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("واحد") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("دسته‌بندی (یخچال/کابینت)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = expiryDaysText,
                        onValueChange = { expiryDaysText = it },
                        label = { Text("روز تا انقضا") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                ) {
                    Text("موجودی رو به اتمام (افزودن خودکار به لیست خرید):", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    Switch(checked = isLowStock, onCheckedChange = { isLowStock = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val qty = quantityText.toDoubleOrNull() ?: 1.0
                        val exp = expiryDaysText.toIntOrNull() ?: 7
                        onConfirm(name, category, qty, unit, exp, isLowStock)
                    }
                }
            ) {
                Text("افزودن")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun AddShoppingItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, quantity: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("۱ عدد") }
    var category by remember { mutableStateOf("خواروبار") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن به لیست خرید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام کالا") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("مقدار / تعداد") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("دسته‌بندی") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onConfirm(name, quantity, category)
                }
            ) { Text("افزودن") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun AddMealPlanDialog(
    onDismiss: () -> Unit,
    onConfirm: (day: String, meal: String, recipe: String, prepTime: Int, note: String) -> Unit
) {
    var day by remember { mutableStateOf("شنبه") }
    var meal by remember { mutableStateOf("ناهار") }
    var recipe by remember { mutableStateOf("") }
    var prepTime by remember { mutableStateOf("45") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("افزودن وعده غذایی") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = recipe,
                    onValueChange = { recipe = it },
                    label = { Text("نام غذا یا دستور پخت") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { day = it },
                        label = { Text("روز هفته") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = meal,
                        onValueChange = { meal = it },
                        label = { Text("وعده (ناهار/شام)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = prepTime,
                    onValueChange = { prepTime = it },
                    label = { Text("زمان پخت (دقیقه)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("یادداشت و نکات") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (recipe.isNotBlank()) onConfirm(day, meal, recipe, prepTime.toIntOrNull() ?: 30, note)
                }
            ) { Text("ثبت وعده") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}
