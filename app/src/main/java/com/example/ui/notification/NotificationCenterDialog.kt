package com.example.ui.notification

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.notification.DailyReminderWorker
import com.example.notification.NotificationManagerHelper
import com.example.notification.TimelineAlarmScheduler

/**
 * Modern Compose Permission Manager:
 * Checks and requests POST_NOTIFICATIONS for Android 13+ (API 33+)
 * using ActivityResultContracts.RequestPermission().
 */
@Composable
fun RequestNotificationPermissionEffect(
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onPermissionResult(true)
            }
        } else {
            onPermissionResult(true)
        }
    }
}

/**
 * Interactive Notification Center Dialog:
 * Allows user to grant permission and directly test each required notification scenario:
 * 1. Urgent Food Expiry Alert
 * 2. 15-Minute Timeline Reminder (Hydrogel synthesis)
 * 3. Exact Start Reminder (Thesis Meeting)
 * 4. Exact Start Reminder (Python Coding)
 * 5. WorkManager Daily Background Check
 */
@Composable
fun NotificationCenterDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            Toast.makeText(context, "مجوز نوتیفیکیشن فعال شد", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "مجوز ارسال نوتیفیکیشن رد شد", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dialog_notification_center"),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE9FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = Color(0xFF6D28D9),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "مدیریت و تست نوتیفیکیشن‌ها",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                ),
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "سیستم هشدارهای محلی و زمان‌بندی دقیق",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF6B7280)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Permission Status Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (hasPermission) Color(0xFFECFDF5) else Color(0xFFFFFBEB)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (hasPermission) Color(0xFFA7F3D0) else Color(0xFFFDE68A)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (hasPermission) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (hasPermission) Color(0xFF059669) else Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (hasPermission) "مجوز POST_NOTIFICATIONS فعال است" else "مجوز نوتیفیکیشن صادر نشده",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    ),
                                    color = if (hasPermission) Color(0xFF065F46) else Color(0xFF92400E)
                                )
                                Text(
                                    text = if (hasPermission) "کانال‌های ضروری و روتین آماده ارسال هستند" else "برای دریافت هشدارها نیاز به فعال‌سازی است",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                                    color = Color(0xFF6B7280)
                                )
                            }
                        }

                        if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                modifier = Modifier.testTag("btn_request_permission_dialog")
                            ) {
                                Text("درخواست", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ارسال سناریوهای تستی (Mock Notifications):",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    ),
                    color = Color(0xFF374151)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 1: Food Expiry Alert (Urgent Channel)
                NotificationTestItem(
                    title = "⚠️ هشدار انقضای مواد غذایی (کانال ضروری)",
                    subtitle = "شیر و مرغ در یخچال فردا منقضی می‌شوند.",
                    badge = "High Importance",
                    badgeColor = Color(0xFFDC2626),
                    icon = Icons.Default.Kitchen,
                    onTrigger = {
                        NotificationManagerHelper.sendMockFoodExpiryAlert(context)
                        Toast.makeText(context, "نوتیفیکیشن هشدار انقضا ارسال شد", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 2: Project 15-min reminder
                NotificationTestItem(
                    title = "⏰ یادآور ۱۵ دقیقه قبل (پروژه)",
                    subtitle = "۱۵ دقیقه تا شروع: سنتز هیدروژل در آزمایشگاه.",
                    badge = "Routine Channel",
                    badgeColor = Color(0xFF2563EB),
                    icon = Icons.Default.Science,
                    onTrigger = {
                        NotificationManagerHelper.sendMockProject15MinReminder(context)
                        Toast.makeText(context, "نوتیفیکیشن یادآور پروژه ارسال شد", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 3: Academic / Thesis Meeting
                NotificationTestItem(
                    title = "⏰ زمان شروع جلسه پایان‌نامه",
                    subtitle = "زمان شروع: جلسه مشاوره پایان‌نامه با میرسلیمی.",
                    badge = "Routine Channel",
                    badgeColor = Color(0xFF7C3AED),
                    icon = Icons.Default.School,
                    onTrigger = {
                        NotificationManagerHelper.sendMockThesisMeetingReminder(context)
                        Toast.makeText(context, "نوتیفیکیشن جلسه پایان‌نامه ارسال شد", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 4: Python Coding
                NotificationTestItem(
                    title = "💻 زمان شروع کدنویسی پایتون",
                    subtitle = "زمان شروع: بررسی کدهای پایتون پردازش تصویر.",
                    badge = "Routine Channel",
                    badgeColor = Color(0xFF059669),
                    icon = Icons.Default.Code,
                    onTrigger = {
                        NotificationManagerHelper.sendMockPythonCodingReminder(context)
                        Toast.makeText(context, "نوتیفیکیشن کدنویسی ارسال شد", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 5: WorkManager background test
                NotificationTestItem(
                    title = "📋 اجرای تست WorkManager در پس‌زمینه",
                    subtitle = "بررسی خودکار موارد نیازمند توجه صبحگاهی و ارسال نوتیفیکیشن تجمیعی.",
                    badge = "Background Worker",
                    badgeColor = Color(0xFFEA580C),
                    icon = Icons.Default.Sync,
                    onTrigger = {
                        DailyReminderWorker.triggerTestWork(context)
                        Toast.makeText(context, "درخواست WorkManager در پس‌زمینه ثبت شد", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scenario 6: Exact Alarm 5 seconds test
                NotificationTestItem(
                    title = "⏱️ تست زمان‌بندی دقیق AlarmManager (۵ ثانیه دیگر)",
                    subtitle = "تنظیم آلارم با AlarmManager.setExactAndAllowWhileIdle",
                    badge = "Exact Alarm",
                    badgeColor = Color(0xFF0284C7),
                    icon = Icons.Default.Alarm,
                    onTrigger = {
                        val scheduler = TimelineAlarmScheduler(context)
                        scheduler.scheduleAlarm(
                            requestCode = 999,
                            triggerTimeMillis = System.currentTimeMillis() + 5000,
                            title = "⏰ آلارم دقیق زمان‌بندی‌شده",
                            message = "این نوتیفیکیشن دقیقاً ۵ ثانیه قبل از طریق AlarmManager تنظیم شده بود!"
                        )
                        Toast.makeText(context, "آلارم برای ۵ ثانیه بعد تنظیم شد", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("بستن")
            }
        }
    )
}

@Composable
fun NotificationTestItem(
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    icon: ImageVector,
    onTrigger: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(badgeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        color = Color(0xFF1F2937),
                        modifier = Modifier.weight(1f)
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.5.sp),
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                    color = Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onTrigger,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = badgeColor),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "ارسال",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
