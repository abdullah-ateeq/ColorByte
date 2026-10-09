package com.example.colorbyte.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.colorbyte.data.model.SavedColor
import com.example.colorbyte.ui.util.HapticHelper
import com.example.colorbyte.ui.util.HapticType
import com.example.colorbyte.ui.viewmodel.ColorByteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: ColorByteViewModel,
    snackbarHostState: SnackbarHostState,
    onNavigateToConverter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedColors by viewModel.filteredSavedColors.collectAsState()
    val allColors by viewModel.savedColors.collectAsState()
    val searchQuery by viewModel.historySearchQuery.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var showClearAllDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<SavedColor?>(null) }

    fun copyItemValues(item: SavedColor) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = "HEX: ${item.hex}\nRGB: ${item.red}, ${item.green}, ${item.blue}"
        val clip = ClipData.newPlainText(item.displayName, text)
        clipboard.setPrimaryClip(clip)
        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
        coroutineScope.launch {
            snackbarHostState.showSnackbar("Copied ${item.displayName}")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header & Clear All
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HISTORY",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${allColors.size} saved / max ${settings.historyLimit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (allColors.isNotEmpty()) {
                TextButton(
                    onClick = { showClearAllDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear all",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CLEAR ALL")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        if (allColors.isNotEmpty()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::setHistorySearchQuery,
                placeholder = { Text("Search by name, notes, HEX or RGB...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setHistorySearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Empty state
        if (allColors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "NO SAVED COLORS",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Save a color to see it here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onNavigateToConverter,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("GO TO CONVERTER")
                    }
                }
            }
        } else if (savedColors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No colors match \"$searchQuery\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Saved Colors List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = savedColors,
                    key = { it.id }
                ) { item ->
                    HistoryItemCard(
                        item = item,
                        onOpen = {
                            viewModel.loadColor(item.rgb, item.name, item.notes)
                            onNavigateToConverter()
                        },
                        onCopy = { copyItemValues(item) },
                        onToggleFavorite = {
                            HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                            viewModel.toggleFavorite(item.id)
                        },
                        onDuplicate = {
                            HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.LIGHT)
                            viewModel.duplicateColor(item.id)
                        },
                        onDelete = {
                            if (settings.confirmDelete) {
                                itemToDelete = item
                            } else {
                                HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.MEDIUM)
                                viewModel.deleteColor(item.id)
                            }
                        }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = if (item.isFavorite) "Remove this favorite color?" else "Delete saved color?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${item.displayName}\" (${item.hex})?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.MEDIUM)
                        viewModel.deleteColor(item.id)
                        itemToDelete = null
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { itemToDelete = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Clear all confirmation dialog
    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = {
                Text(text = "Delete all saved colors?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "This will permanently remove all colors in your history.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.performHaptic(context, settings.hapticFeedback, HapticType.MEDIUM)
                        viewModel.clearHistory()
                        showClearAllDialog = false
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showClearAllDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: SavedColor,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val swatchColor = Color(item.red, item.green, item.blue)
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault()) }
    val formattedDate = remember(item.createdAt) { dateFormat.format(Date(item.createdAt)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .semantics {
                contentDescription = "Saved color ${item.displayName} HEX ${item.hex} RGB ${item.red}, ${item.green}, ${item.blue}"
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color Swatch rendered from actual RGB/HEX
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(swatchColor)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (item.isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite color",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.hex,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "RGB: ${item.red}, ${item.green}, ${item.blue}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (item.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            // Quick Actions / Overflow Menu
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (item.isFavorite) "Unmark favorite" else "Mark favorite",
                        tint = if (item.isFavorite) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open in Converter") },
                            onClick = {
                                menuExpanded = false
                                onOpen()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Values") },
                            onClick = {
                                menuExpanded = false
                                onCopy()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate") },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}
