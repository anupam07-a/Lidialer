package com.lidialer.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.CallMissed
import androidx.compose.material.icons.outlined.CallReceived
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Grid3x3
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

private object LidialerTokens {
    val ink = Color(0xFFF3F4F3)
    val muted = Color(0xFF9EA7B0)
    val environment = Color(0xFF071019)
    val surface = Color(0xFF17232D).copy(alpha = .84f)
    val surfaceStrong = Color(0xFF1B2935).copy(alpha = .96f)
    val outline = Color(0xFF81909A).copy(alpha = .27f)
    val accent = Color(0xFF9FE3C0)
    val danger = Color(0xFFE78383)
    val blue = Color(0xFF87B8FF)
    val display = FontFamily.Monospace
}

enum class MaterialMode { Dynamic, Clear, Frosted, Obscured }

data class MaterialSettings(val mode: MaterialMode = MaterialMode.Dynamic, val reduceMotion: Boolean = false)

data class Contact(val id: Long, val name: String, val number: String, val initials: String, val favorite: Boolean)
enum class CallType { Incoming, Outgoing, Missed, Rejected }
data class CallRecord(val id: Long, val contactId: Long?, val number: String, val timestamp: String, val duration: String, val type: CallType, val sim: Int)

private val contacts = listOf(
    Contact(1, "Aritra Roy", "+91 98765 43210", "AR", false), Contact(2, "Sohan Das", "+91 99887 12004", "SD", false),
    Contact(3, "Priya Patel", "+91 98110 66102", "PP", true), Contact(4, "Rohit Kumar", "+91 98200 40971", "RK", false),
    Contact(5, "Tanmay Sen", "+91 98990 77421", "TS", true), Contact(6, "Sneha Kapoor", "+91 98000 21812", "SK", false),
    Contact(7, "Arjun Verma", "+91 97320 10101", "AV", false), Contact(8, "Meera Khan", "+91 97000 33771", "MK", true)
)
private val records = listOf(
    CallRecord(1, 1, contacts[0].number, "10:24 AM", "02:18", CallType.Outgoing, 1), CallRecord(2, 2, contacts[1].number, "09:42 AM", "", CallType.Missed, 2),
    CallRecord(3, 3, contacts[2].number, "08:17 AM", "04:01", CallType.Incoming, 1), CallRecord(4, 4, contacts[3].number, "07:56 AM", "", CallType.Outgoing, 2),
    CallRecord(5, 5, contacts[4].number, "06:33 AM", "01:10", CallType.Incoming, 1), CallRecord(6, 6, contacts[5].number, "Yesterday, 11:12 PM", "", CallType.Missed, 1),
    CallRecord(7, 7, contacts[6].number, "Yesterday, 08:45 PM", "03:28", CallType.Outgoing, 2), CallRecord(8, 8, contacts[7].number, "Yesterday, 05:21 PM", "", CallType.Incoming, 1)
)

@Composable
fun LidialerApp() {
    MaterialTheme(colorScheme = androidx.compose.material3.darkColorScheme(background = LidialerTokens.environment, onBackground = LidialerTokens.ink, primary = LidialerTokens.accent)) {
        var tab by rememberSaveable { mutableStateOf(1) }
        var query by rememberSaveable { mutableStateOf("") }
        var number by rememberSaveable { mutableStateOf("") }
        var detailId by rememberSaveable { mutableStateOf<Long?>(null) }
        var settings by remember { mutableStateOf(MaterialSettings()) }
        val context = LocalContext.current
        val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted && number.isNotBlank()) placeCall(context, number) }

        fun call(value: String) {
            number = value
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) placeCall(context, value) else permission.launch(Manifest.permission.CALL_PHONE)
        }

        Box(Modifier.fillMaxSize().background(environmentBrush)) {
            if (detailId != null) {
                val contact = contacts.firstOrNull { it.id == detailId }
                if (contact != null) ContactDetails(contact, onBack = { detailId = null }, onCall = { call(contact.number) })
            } else {
                Column(Modifier.fillMaxSize().imePadding()) {
                    AppTopBar(tab, query, onSearch = { query = it }, onOpenSettings = { settings = settings.copy(mode = if (settings.mode == MaterialMode.Dynamic) MaterialMode.Frosted else MaterialMode.Dynamic) })
                    AnimatedContent(targetState = tab, label = "main-area") { selected ->
                        when (selected) {
                            0 -> Keypad(number, onDigit = { number += it }, onDelete = { number = number.dropLast(1) }, onClear = { number = "" }, onCall = { call(number) }, onContact = { detailId = it })
                            1 -> Recents(query, onQuery = { query = it }, onCall = { call(it) }, onOpen = { detailId = it })
                            else -> ContactsScreen(query, onQuery = { query = it }, onOpen = { detailId = it }, onCall = { call(it) })
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    BottomNav(tab, onSelected = { tab = it })
                    Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                }
            }
        }
    }
}

private val environmentBrush = Brush.radialGradient(listOf(Color(0xFF123A55), Color(0xFF0A1B29), LidialerTokens.environment), radius = 1200f)

private fun placeCall(context: Context, value: String) { context.startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:${value.replace(" ", "")}"))) }

@Composable
private fun AppTopBar(tab: Int, query: String, onSearch: (String) -> Unit, onOpenSettings: () -> Unit) {
    var searching by rememberSaveable { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp)) {
        Row(Modifier.fillMaxWidth().height(62.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("LIDIALER", color = LidialerTokens.ink, fontSize = 27.sp, fontWeight = FontWeight.Bold, letterSpacing = (-.8).sp)
                Text(when (tab) { 0 -> "Keypad"; 1 -> "Recents"; else -> "Contacts" }.uppercase(), color = LidialerTokens.muted, fontSize = 10.sp, letterSpacing = 2.1.sp)
            }
            Box(Modifier.size(11.dp).clip(CircleShape).background(LidialerTokens.accent))
            IconButton(onClick = { searching = !searching }) { Icon(Icons.Outlined.Search, "Search", tint = LidialerTokens.ink) }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Outlined.MoreVert, "More", tint = LidialerTokens.ink) }
                DropdownMenu(menu, { menu = false }) {
                    DropdownMenuItem({ Text("Appearance") }, onClick = { menu = false; onOpenSettings() }, leadingIcon = { Icon(Icons.Outlined.Settings, null) })
                    DropdownMenuItem({ Text("Favorites") }, onClick = { menu = false }, leadingIcon = { Icon(Icons.Outlined.FavoriteBorder, null) })
                }
            }
        }
        AnimatedVisibility(searching) {
            SearchField(query, onSearch)
        }
    }
}

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit) {
    androidx.compose.material3.OutlinedTextField(value = query, onValueChange = onChange, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), placeholder = { Text("Search contacts and calls", color = LidialerTokens.muted) }, singleLine = true, shape = RoundedCornerShape(22.dp), leadingIcon = { Icon(Icons.Outlined.Search, null, tint = LidialerTokens.muted) }, colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(unfocusedContainerColor = LidialerTokens.surface, focusedContainerColor = LidialerTokens.surfaceStrong, unfocusedBorderColor = LidialerTokens.outline, focusedBorderColor = LidialerTokens.accent))
}

@Composable
private fun Recents(query: String, onQuery: (String) -> Unit, onCall: (String) -> Unit, onOpen: (Long) -> Unit) {
    var showSearch by rememberSaveable { mutableStateOf(query.isNotEmpty()) }
    val filtered = records.filter { record -> val c = contacts.firstOrNull { it.id == record.contactId }; query.isBlank() || c?.name?.contains(query, true) == true || record.number.contains(query) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) { Text("Recent calls", color = LidialerTokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f)); Text("${filtered.size} calls", color = LidialerTokens.muted, fontSize = 12.sp) }
        if (showSearch) SearchField(query, onQuery)
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            item { DayLabel("TODAY") }
            items(filtered.filter { !it.timestamp.startsWith("Yesterday") }, key = { it.id }) { RecentRow(it, onCall, onOpen) }
            item { DayLabel("YESTERDAY") }
            items(filtered.filter { it.timestamp.startsWith("Yesterday") }, key = { it.id }) { RecentRow(it, onCall, onOpen) }
        }
    }
}

@Composable
private fun DayLabel(label: String) { Row(Modifier.padding(start = 4.dp, top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) { Text(label, color = LidialerTokens.muted, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.width(10.dp)); Box(Modifier.width(28.dp).height(1.dp).background(LidialerTokens.outline)) } }

@Composable
private fun RecentRow(record: CallRecord, onCall: (String) -> Unit, onOpen: (Long) -> Unit) {
    val contact = contacts.firstOrNull { it.id == record.contactId }
    GlassSurface(MaterialRole.Frosted, Modifier.fillMaxWidth().clickable { contact?.let { onOpen(it.id) } }) {
        Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(contact?.initials ?: "?", contact?.id ?: 0)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(contact?.name ?: record.number, color = if (record.type == CallType.Missed) LidialerTokens.danger else LidialerTokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) { CallTypeIcon(record.type); Spacer(Modifier.width(6.dp)); Text(record.number, color = LidialerTokens.muted, fontSize = 12.sp, maxLines = 1) }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(record.timestamp.substringAfterLast(", "), color = if (record.type == CallType.Missed) LidialerTokens.danger else LidialerTokens.muted, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) { Text("SIM ${record.sim}", color = LidialerTokens.muted, fontFamily = LidialerTokens.display, fontSize = 9.sp); Spacer(Modifier.width(7.dp)); IconButton(onClick = { onCall(record.number) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Outlined.Call, "Call", tint = LidialerTokens.accent, modifier = Modifier.size(17.dp)) } }
            }
        }
    }
}

private enum class MaterialRole { Clear, Frosted, Obscured }

@Composable
private fun GlassSurface(role: MaterialRole, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val color = when (role) { MaterialRole.Clear -> Color.White.copy(alpha = .08f); MaterialRole.Frosted -> LidialerTokens.surface; MaterialRole.Obscured -> LidialerTokens.environment.copy(alpha = .96f) }
    Surface(modifier, shape = RoundedCornerShape(20.dp), color = color, border = BorderStroke(1.dp, LidialerTokens.outline), content = content)
}

@Composable
private fun Avatar(initials: String, id: Long) { Box(Modifier.size(45.dp).clip(CircleShape).background(listOf(Color(0xFF314C59), Color(0xFF4D443F), Color(0xFF3E4559), Color(0xFF3B504B))[id.toInt().mod(4)]), contentAlignment = Alignment.Center) { Text(initials, color = LidialerTokens.ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) } }

@Composable
private fun CallTypeIcon(type: CallType) { val color = when (type) { CallType.Missed, CallType.Rejected -> LidialerTokens.danger; CallType.Incoming -> LidialerTokens.blue; CallType.Outgoing -> LidialerTokens.accent }; Icon(if (type == CallType.Incoming) Icons.Outlined.CallReceived else if (type == CallType.Outgoing) Icons.Outlined.CallMade else Icons.Outlined.CallMissed, null, tint = color, modifier = Modifier.size(16.dp)) }

@Composable
private fun ContactsScreen(query: String, onQuery: (String) -> Unit, onOpen: (Long) -> Unit, onCall: (String) -> Unit) {
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    val list = contacts.filter { (!favoritesOnly || it.favorite) && (query.isBlank() || it.name.contains(query, true) || it.number.contains(query)) }.sortedBy { it.name }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("People", color = LidialerTokens.ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.weight(1f));
            FilterChip("Favorites", favoritesOnly) { favoritesOnly = !favoritesOnly }
        }
        SearchField(query, onQuery)
        LazyColumn(contentPadding = PaddingValues(horizontal = 18.dp, vertical = 2.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            items(list, key = { it.id }) { contact -> ContactRow(contact, onOpen, onCall) }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) { Surface(Modifier.clickable(onClick = onClick), shape = RoundedCornerShape(18.dp), color = if (selected) LidialerTokens.accent.copy(alpha = .18f) else Color.Transparent, border = BorderStroke(1.dp, if (selected) LidialerTokens.accent else LidialerTokens.outline)) { Text(label, color = if (selected) LidialerTokens.accent else LidialerTokens.muted, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)) } }

@Composable
private fun ContactRow(contact: Contact, onOpen: (Long) -> Unit, onCall: (String) -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).clickable { onOpen(contact.id) }.padding(horizontal = 10.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) { Avatar(contact.initials, contact.id); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(contact.name, color = LidialerTokens.ink, fontSize = 15.sp, fontWeight = FontWeight.Medium); Text(contact.number, color = LidialerTokens.muted, fontSize = 12.sp) }; if (contact.favorite) Icon(Icons.Outlined.Favorite, null, tint = LidialerTokens.accent, modifier = Modifier.size(16.dp)); IconButton(onClick = { onCall(contact.number) }) { Icon(Icons.Outlined.Call, "Call ${contact.name}", tint = LidialerTokens.accent) } } }

@Composable
private fun ContactDetails(contact: Contact, onBack: () -> Unit, onCall: () -> Unit) { Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) { Row(Modifier.height(62.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onBack) { Icon(Icons.Outlined.KeyboardArrowLeft, "Back", tint = LidialerTokens.ink) }; Text("Contact", color = LidialerTokens.ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }; Spacer(Modifier.height(26.dp)); Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { Avatar(contact.initials, contact.id); Spacer(Modifier.height(14.dp)); Text(contact.name, color = LidialerTokens.ink, fontSize = 27.sp, fontWeight = FontWeight.SemiBold); Text(contact.number, color = LidialerTokens.muted, fontSize = 14.sp); Spacer(Modifier.height(22.dp)); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { ActionButton("Call", Icons.Outlined.Call, onCall); ActionButton("Favorite", if (contact.favorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder) {} } }; Spacer(Modifier.height(30.dp)); Text("CALL HISTORY", color = LidialerTokens.muted, fontSize = 11.sp, letterSpacing = 2.sp); Spacer(Modifier.height(10.dp)); records.filter { it.contactId == contact.id }.forEach { record -> GlassSurface(MaterialRole.Frosted, Modifier.fillMaxWidth().padding(bottom = 7.dp)) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { CallTypeIcon(record.type); Spacer(Modifier.width(10.dp)); Text(record.timestamp, color = LidialerTokens.ink, modifier = Modifier.weight(1f)); Text(record.duration.ifBlank { "Missed" }, color = LidialerTokens.muted, fontSize = 12.sp) } } } } }

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Surface(Modifier.size(52.dp).clickable(onClick = onClick), shape = CircleShape, color = LidialerTokens.surfaceStrong, border = BorderStroke(1.dp, LidialerTokens.outline)) { Box(contentAlignment = Alignment.Center) { Icon(icon, label, tint = LidialerTokens.accent) } }; Spacer(Modifier.height(5.dp)); Text(label, color = LidialerTokens.muted, fontSize = 11.sp) } }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Keypad(number: String, onDigit: (String) -> Unit, onDelete: () -> Unit, onClear: () -> Unit, onCall: () -> Unit, onContact: (Long) -> Unit) {
    val matched = contacts.firstOrNull { it.number.filter(Char::isDigit).endsWith(number.filter(Char::isDigit)) && number.length > 4 }
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(10.dp)); Box(Modifier.fillMaxWidth().height(78.dp), contentAlignment = Alignment.Center) { if (number.isBlank()) Text("Enter number", color = LidialerTokens.muted, fontSize = 16.sp) else Text(number, color = LidialerTokens.ink, fontFamily = LidialerTokens.display, fontSize = (38 - (number.length / 4).coerceAtMost(12)).sp, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center) }
        AnimatedVisibility(matched != null) { matched?.let { GlassSurface(MaterialRole.Frosted, Modifier.fillMaxWidth().clickable { onContact(it.id) }) { Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) { Avatar(it.initials, it.id); Spacer(Modifier.width(10.dp)); Column { Text(it.name, color = LidialerTokens.ink, fontWeight = FontWeight.SemiBold); Text("Matched contact", color = LidialerTokens.muted, fontSize = 12.sp) } } } } }
        Spacer(Modifier.weight(1f)); val keys = listOf("1" to "", "2" to "ABC", "3" to "DEF", "4" to "GHI", "5" to "JKL", "6" to "MNO", "7" to "PQRS", "8" to "TUV", "9" to "WXYZ", "*" to "", "0" to "+", "#" to "")
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) { keys.chunked(3).forEach { row -> Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) { row.forEach { (digit, letters) -> DialKey(digit, letters, onClick = { onDigit(digit) }) } } } }
        Spacer(Modifier.height(16.dp)); Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) { Box(Modifier.size(62.dp).clip(CircleShape).background(LidialerTokens.accent).clickable(enabled = number.isNotBlank(), onClick = onCall), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Call, "Call", tint = LidialerTokens.environment, modifier = Modifier.size(27.dp)) }; IconButton(onClick = onDelete, modifier = Modifier.padding(start = 32.dp).combinedClickable(onClick = onDelete, onLongClick = onClear)) { Icon(Icons.Outlined.DeleteOutline, "Delete", tint = LidialerTokens.muted) } }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun DialKey(digit: String, letters: String, onClick: () -> Unit) { var pressed by remember { mutableStateOf(false) }; val scale by animateFloatAsState(if (pressed) .94f else 1f, animationSpec = spring(stiffness = 700f), label = "key-$digit"); Column(Modifier.size(82.dp).scale(scale).clip(CircleShape).background(Color.White.copy(alpha = .07f)).pointerInput(Unit) { detectTapGestures(onPress = { pressed = true; tryAwaitRelease(); pressed = false }, onTap = { onClick() }) }.padding(top = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(digit, color = LidialerTokens.ink, fontSize = 27.sp, fontFamily = LidialerTokens.display); Text(letters, color = LidialerTokens.muted, fontSize = 9.sp, letterSpacing = 1.7.sp) } }

@Composable
private fun BottomNav(selected: Int, onSelected: (Int) -> Unit) {
    val items = listOf("Keypad" to Icons.Outlined.Grid3x3, "Recents" to Icons.Outlined.Call, "Contacts" to Icons.Outlined.Contacts)
    var dragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableStateOf(selected.toFloat()) }
    val visualPosition by animateFloatAsState(
        targetValue = if (dragging) dragPosition else selected.toFloat(),
        animationSpec = spring(stiffness = 520f, dampingRatio = .82f),
        label = "dock-lens-position"
    )

    Surface(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(45.dp),
        color = Color.White.copy(alpha = .075f),
        border = BorderStroke(1.dp, LidialerTokens.outline)
    ) {
        BoxWithConstraints(
            Modifier.fillMaxWidth().height(88.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { dragging = true; dragPosition = selected.toFloat() },
                        onHorizontalDrag = { change, amount ->
                            change.consume()
                            val slot = size.width.toFloat() / 3f
                            dragPosition = (dragPosition + amount / slot).coerceIn(0f, 2f)
                        },
                        onDragEnd = {
                            dragging = false
                            val destination = dragPosition.roundToInt().coerceIn(0, 2)
                            dragPosition = destination.toFloat()
                            onSelected(destination)
                        },
                        onDragCancel = { dragging = false; dragPosition = selected.toFloat() }
                    )
                }
        ) {
            val slotWidth = maxWidth / 3f
            val center = slotWidth * (visualPosition + .5f)
            val distanceFromNearest = abs(visualPosition - visualPosition.roundToInt()).coerceIn(0f, .5f)
            val lensWidth = (118.dp - distanceFromNearest * 24.dp).coerceIn(82.dp, 122.dp)
            val motionBlur = (abs(dragPosition - selected) * 1.7f).coerceIn(0f, 1f)

            // One lens is reused and moved; it is never destroyed/recreated per tab.
            Box(
                Modifier.align(Alignment.CenterStart)
                    .offset { IntOffset((center - lensWidth / 2).roundToPx(), 0) }
                    .size(lensWidth, 68.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color.White.copy(alpha = .095f + motionBlur * .035f))
                    .then(Modifier)
            ) {
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.White.copy(alpha = .10f), Color.Transparent, Color.White.copy(alpha = .06f)))))
            }

            Row(Modifier.fillMaxSize().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                items.forEachIndexed { index, item ->
                    val distance = abs(visualPosition - index.toFloat()).coerceIn(0f, 1.5f)
                    val labelAlpha = (1f - distance * 1.45f).coerceIn(0f, 1f)
                    val iconScale = 1f + labelAlpha * .10f
                    val tint by animateColorAsState(if (labelAlpha > .35f) LidialerTokens.ink else LidialerTokens.muted, label = "dock-tint-$index")
                    Box(
                        Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(36.dp))
                            .clickable(role = Role.Tab, onClick = { dragPosition = index.toFloat(); onSelected(index) }),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(item.second, item.first, tint = tint, modifier = Modifier.size(23.dp).scale(iconScale))
                            Text(item.first, color = LidialerTokens.ink.copy(alpha = labelAlpha), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp).width((54f * labelAlpha).dp), maxLines = 1, overflow = TextOverflow.Clip)
                        }
                    }
                }
            }
        }
    }
}
