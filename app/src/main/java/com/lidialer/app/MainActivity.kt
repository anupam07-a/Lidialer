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
    val tint: Color
)
enum class CallType { INCOMING, OUTGOING, MISSED }

private val today = listOf(
    CallEntry("Maya Sharma", "+91 98765 43210", "10:42 AM", CallType.INCOMING, "MS", Color(0xFF46535B)),
    CallEntry("Arjun Mehta", "+91 99887 12004", "09:21 AM", CallType.OUTGOING, "AM", Color(0xFF5B4F48)),
    CallEntry("Rhea Kapoor", "+91 98110 66102", "08:54 AM", CallType.MISSED, "RK", Color(0xFF50485A))
)
private val yesterday = listOf(
    CallEntry("Nikhil Verma", "+91 98200 40971", "Yesterday, 6:18 PM", CallType.MISSED, "NV", Color(0xFF46574F)),
    CallEntry("Samira Khan", "+91 98990 77421", "Yesterday, 2:06 PM", CallType.INCOMING, "SK", Color(0xFF5A5144))
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

    Box(Modifier.fillMaxSize().background(Canvas)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 8.dp, bottom = 108.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Box(Modifier.fillMaxWidth().height(184.dp), contentAlignment = Alignment.CenterStart) {
                    Column(Modifier.graphicsLayer { scaleX = heroScale; scaleY = heroScale; alpha = heroAlpha }) {
                        Text("LIDIALER", color = Accent, fontSize = 11.sp, letterSpacing = 3.2.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Text("Phone", color = Ink, fontSize = 51.sp, lineHeight = 52.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.SansSerif)
                        Spacer(Modifier.height(13.dp))
                        Text("Your calls, at a human pace.", color = Muted, fontSize = 14.sp)
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
        Surface(shape = RoundedCornerShape(22.dp), color = Glass, tonalElevation = 0.dp) {
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
            Text(call.name, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                val typeColor = if (call.type == CallType.MISSED) Missed else Muted
                Icon(if (call.type == CallType.INCOMING) Icons.Outlined.CallReceived else if (call.type == CallType.OUTGOING) Icons.Outlined.CallMade else Icons.Outlined.CallMissed, null, tint = typeColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(call.number, color = Muted, fontSize = 12.sp)
            }
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(call.time.substringAfterLast(", "), color = Muted, fontSize = 11.sp)
            Icon(Icons.Outlined.Call, "Call ${call.name}", tint = if (call.type == CallType.MISSED) Missed else Accent, modifier = Modifier.size(18.dp))
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
