package com.lidialer.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.CallMissed
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Grid3x3
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale

private val Ink = Color(0xFFF4F3F0)
private val Muted = Color(0xFF9B9A9A)
private val Canvas = Color(0xFF090A0C)
private val Glass = Color(0xFF17191C)
private val GlassLine = Color(0xFF2A2C30)
private val Accent = Color(0xFFB7C9A7)
private val Missed = Color(0xFFE17D7D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LidialerTheme { LidialerApp() } }
    }
}

@Composable
private fun LidialerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            background = Canvas, surface = Glass, onSurface = Ink,
            primary = Accent, onBackground = Ink
        ),
        content = content
    )
}

data class CallEntry(
    val name: String,
    val number: String,
    val time: String,
    val type: CallType,
    val initials: String,
    val tint: Color,
    val sim: Int
)
enum class CallType { INCOMING, OUTGOING, MISSED }

private val today = listOf(
    CallEntry("Aritra Roy", "+91 98765 43210", "10:24 AM", CallType.OUTGOING, "AR", Color(0xFF46535B), 1),
    CallEntry("Sohan Das", "+91 99887 12004", "09:42 AM", CallType.MISSED, "SD", Color(0xFF5B4F48), 2),
    CallEntry("Priya Patel", "+91 98110 66102", "08:17 AM", CallType.INCOMING, "PP", Color(0xFF50485A), 1),
    CallEntry("Rohit Kumar", "+91 98200 40971", "07:56 AM", CallType.OUTGOING, "RK", Color(0xFF46574F), 2),
    CallEntry("Tanmay Sen", "+91 98990 77421", "06:33 AM", CallType.INCOMING, "TS", Color(0xFF5A5144), 1)
)
private val yesterday = listOf(
    CallEntry("Sneha Kapoor", "+91 98200 40971", "11:12 PM", CallType.MISSED, "SK", Color(0xFF46574F), 1),
    CallEntry("Arjun Verma", "+91 98990 77421", "08:45 PM", CallType.OUTGOING, "AV", Color(0xFF5A5144), 2),
    CallEntry("Meera Khan", "+91 98110 66102", "05:21 PM", CallType.INCOMING, "MK", Color(0xFF50485A), 1),
    CallEntry("Dev Singh", "+91 98765 43210", "03:18 PM", CallType.OUTGOING, "DS", Color(0xFF46535B), 2)
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LidialerApp() {
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    var activeTab by remember { mutableStateOf(1) }
    var heldTab by remember { mutableStateOf<Int?>(null) }
    val callPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    val scrolled = listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 80
    val heroScale by animateFloatAsState(if (scrolled) .72f else 1f, label = "hero-scale")
    val heroAlpha by animateFloatAsState(if (scrolled) .08f else 1f, label = "hero-alpha")

    Box(
        Modifier.fillMaxSize().background(
            Brush.radialGradient(
                colors = listOf(Color(0xFF0B3976), Color(0xFF071426), Canvas),
                center = androidx.compose.ui.geometry.Offset(0f, 0f),
                radius = 1050f
            )
        )
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().height(142.dp), contentAlignment = Alignment.CenterStart) {
                    Row(
                        Modifier.fillMaxWidth().graphicsLayer { scaleX = heroScale; scaleY = heroScale; alpha = heroAlpha },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("LIDIALER", color = Ink, fontSize = 35.sp, letterSpacing = (-1.2).sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.size(27.dp).clip(CircleShape).background(Color(0xFF182A35)), contentAlignment = Alignment.Center) {
                            Box(Modifier.size(11.dp).clip(CircleShape).background(Color(0xFF31E5A4)))
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = {}) { Icon(Icons.Outlined.FilterList, "Sort", tint = Ink) }
                        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Outlined.MoreVert, "More", tint = Ink) }
                    }
                }
            }
            stickyHeader {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Glass.copy(alpha = .94f))
                        .padding(horizontal = 6.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Search, "Search", tint = Muted) }
                    Text("Search calls", color = Muted, fontSize = 14.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = {}) { Icon(Icons.Outlined.FilterList, "Filter", tint = Muted) }
                    Box {
                        IconButton(onClick = { menuOpen = true }) { Icon(Icons.Outlined.MoreVert, "More", tint = Muted) }
                        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                            listOf("Delete", "Hide blocked calls", "Total call time", "View and edit tags", "Settings").forEach { label ->
                                DropdownMenuItem(text = { Text(label) }, onClick = { menuOpen = false }, leadingIcon = if (label == "Delete") ({ Icon(Icons.Outlined.DeleteOutline, null) }) else null)
                            }
                        }
                    }
                }
            }
            item { Section("Today", today, context, callPermission) }
            item { Section("Yesterday", yesterday, context, callPermission) }
        }

        if (scrolled) {
            Text("Recents", Modifier.align(Alignment.TopStart).padding(start = 20.dp, top = 16.dp), color = Ink, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        }

        Dock(
            activeTab = activeTab,
            heldTab = heldTab,
            onPress = { activeTab = it },
            onHold = { heldTab = it },
            onRelease = { heldTab = null },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun Section(title: String, calls: List<CallEntry>, context: android.content.Context, permission: androidx.activity.result.ActivityResultLauncher<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title.uppercase(Locale.getDefault()), color = Muted, fontSize = 11.sp, letterSpacing = 1.8.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(start = 4.dp, bottom = 2.dp))
        Surface(shape = RoundedCornerShape(24.dp), color = Glass.copy(alpha = .78f), tonalElevation = 0.dp, border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2B4667).copy(alpha = .72f))) {
            Column {
                calls.forEachIndexed { index, call ->
                    CallRow(call) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${call.number.replace(" ", "")}")))
                        } else permission.launch(Manifest.permission.CALL_PHONE)
                    }
                    if (index < calls.lastIndex) Spacer(Modifier.padding(start = 76.dp).fillMaxWidth().height(1.dp).background(GlassLine))
                }
            }
        }
    }
}

@Composable
private fun CallRow(call: CallEntry, onCall: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onCall).padding(horizontal = 15.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(46.dp).clip(CircleShape).background(call.tint), contentAlignment = Alignment.Center) {
            Text(call.initials, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(call.name, color = if (call.type == CallType.MISSED) Missed else Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                val typeColor = if (call.type == CallType.MISSED) Missed else if (call.type == CallType.OUTGOING) Color(0xFF4CE5A5) else Color(0xFF79A8FF)
                Icon(if (call.type == CallType.INCOMING) Icons.Outlined.CallReceived else if (call.type == CallType.OUTGOING) Icons.Outlined.CallMade else Icons.Outlined.CallMissed, null, tint = typeColor, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(7.dp))
                Text(call.number, color = Muted, fontSize = 12.sp, maxLines = 1)
            }
        }
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFF2A3A52).copy(alpha = .55f), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF425572).copy(alpha = .55f))) {
            Text("SIM ${call.sim}", color = Muted, fontSize = 10.sp, letterSpacing = 1.5.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(call.time, color = if (call.type == CallType.MISSED) Missed else Muted, fontSize = 11.sp)
            Box(Modifier.size(25.dp).clip(CircleShape).background(Color(0xFF213451).copy(alpha = .7f)), contentAlignment = Alignment.Center) {
                Text("›", color = Muted, fontSize = 22.sp, fontWeight = FontWeight.Light)
            }
        }
    }
}

@Composable
private fun Dock(activeTab: Int, heldTab: Int?, onPress: (Int) -> Unit, onHold: (Int) -> Unit, onRelease: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier.padding(bottom = 14.dp), shape = RoundedCornerShape(30.dp), color = Color(0xFF1B1D20).copy(alpha = .97f), tonalElevation = 8.dp, shadowElevation = 16.dp) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 7.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Keypad" to Icons.Outlined.Grid3x3, "Recents" to Icons.Outlined.Call, "Contacts" to Icons.Outlined.Contacts).forEachIndexed { index, (label, icon) ->
                val selected = activeTab == index
                val lens = heldTab == index
                val scale by animateFloatAsState(if (lens) 1.14f else 1f, animationSpec = spring(stiffness = 500f), label = "dock-$index")
                Column(
                    Modifier.scale(scale).clip(RoundedCornerShape(23.dp)).background(if (selected) Accent.copy(alpha = .15f) else Color.Transparent)
                        .pointerInput(index) { detectTapGestures(onTap = { onPress(index) }, onLongPress = { onHold(index) }, onPress = { tryAwaitRelease(); onRelease() }) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(icon, label, tint = if (selected || lens) Accent else Muted, modifier = Modifier.size(20.dp))
                    Text(label, color = if (selected) Ink else Muted, fontSize = 10.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}
