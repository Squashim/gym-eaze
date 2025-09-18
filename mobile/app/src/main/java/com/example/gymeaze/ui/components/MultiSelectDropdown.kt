package com.example.gymeaze.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.*

@Composable
fun <T> MultiSelectDropdown(
    label: String,
    selectedItems: List<T>,
    availableItems: List<T>,
    onSelectionChange: (List<T>) -> Unit,
    itemDisplayText: (T) -> String,
    modifier: Modifier = Modifier,
    placeholder: String,
    isLoading: Boolean = false,
    itemContent: (@Composable (T) -> Unit)? = null
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
                placeholder
            } else {
                stringResource(R.string.items_selected_count, selectedItems.size)
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
            LazyRow(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            itemDisplayText = itemDisplayText,
            itemContent = itemContent
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
        color = getSecondaryColor(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = getOnSecondaryColor(),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "×",
                color = getOnSecondaryColor(),
                fontSize = 18.sp,
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
    itemDisplayText: (T) -> String,
    maxSelected: Int = 8,
    itemContent: (@Composable (T) -> Unit)? = null
) {
    var searchText by remember { mutableStateOf("") }
    val filteredItems = availableItems.filter {
        itemDisplayText(it).contains(searchText, ignoreCase = true)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = getOnSurfaceColor(),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = getOnSurfaceColor()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text(stringResource(R.string.search_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = getOnSurfaceSecondaryColor(),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getInputBorderColor(),
                        cursorColor = getPrimaryColor(),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor()
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                if(filteredItems.isEmpty()){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = getOnSurfaceSecondaryColor(),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.no_results_found),
                                color = getOnSurfaceSecondaryColor(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 255.dp)
                    ) {
                        items(filteredItems) { item ->
                            val isSelected = selectedItems.contains(item)
                            val canSelectMore = selectedItems.size < maxSelected || isSelected

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = canSelectMore) {
                                        if (isSelected) {
                                            onSelectionChange(selectedItems - item)
                                        } else if(canSelectMore){
                                            onSelectionChange(selectedItems + item)
                                        }
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if(canSelectMore) onSelectionChange(selectedItems + item)
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

                                if (itemContent != null) {
                                    itemContent(item)
                                } else {
                                    Text(
                                        text = itemDisplayText(item),
                                        color = if (canSelectMore) getOnSurfaceColor() else getOnSurfaceSecondaryColor(),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}