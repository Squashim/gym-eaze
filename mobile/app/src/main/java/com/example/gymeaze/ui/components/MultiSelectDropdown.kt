package com.example.gymeaze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymeaze.ui.theme.*

@Composable
fun <T> MultiSelectDropdown(
    label: String,
    selectedItems: List<T>,
    availableItems: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    itemDisplayText: (T) -> String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (selectedItems.isEmpty()) {
                "Wybierz pozycje..."
            } else {
                "${selectedItems.size} wybranych"
            },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!isLoading) showDialog = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = getInputBorderColor(),
                disabledTextColor = if (selectedItems.isEmpty())
                    getOnSurfaceSecondaryColor() else getOnSurfaceColor()
            ),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = getPrimaryColor(),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = if (showDialog)
                            Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.clickable { showDialog = !showDialog }
                    )
                }
            }
        )

        if (selectedItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.heightIn(max = 120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(selectedItems) { item ->
                    SelectedItemChip(
                        text = itemDisplayText(item),
                        onRemove = {
                            onSelectionChange(selectedItems - item)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        MultiSelectDialog(
            title = label,
            availableItems = availableItems,
            selectedItems = selectedItems,
            onSelectionChange = onSelectionChange,
            onDismiss = { showDialog = false },
            itemDisplayText = itemDisplayText
        )
    }
}

@Composable
private fun SelectedItemChip(
    text: String,
    onRemove: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = getPrimaryColor().copy(alpha = 0.1f),
        modifier = Modifier
            .border(1.dp, getPrimaryColor().copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = getPrimaryColor(),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "×",
                color = getPrimaryColor(),
                fontSize = 16.sp,
                modifier = Modifier
                    .clickable { onRemove() }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
private fun <T> MultiSelectDialog(
    title: String,
    availableItems: List<T>,
    selectedItems: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    onDismiss: () -> Unit,
    itemDisplayText: (T) -> String
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableItems) { item ->
                        val isSelected = selectedItems.contains(item)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) {
                                        onSelectionChange(selectedItems - item)
                                    } else {
                                        onSelectionChange(selectedItems + item)
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        onSelectionChange(selectedItems + item)
                                    } else {
                                        onSelectionChange(selectedItems - item)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = getPrimaryColor(),
                                    uncheckedColor = getOnSurfaceSecondaryColor()
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = itemDisplayText(item),
                                color = getOnSurfaceColor(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Zamknij", color = getPrimaryColor())
                    }
                }
            }
        }
    }
}