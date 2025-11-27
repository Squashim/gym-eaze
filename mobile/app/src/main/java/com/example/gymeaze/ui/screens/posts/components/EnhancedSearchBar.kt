package com.example.gymeaze.ui.screens.posts.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun EnhancedSearchBar(
    selectedHashtags: List<String>,
    hashtagInput: String,
    onHashtagInputChange: (String) -> Unit,
    onAddHashtag: (String) -> Unit,
    onRemoveHashtag: (String) -> Unit,
    onSearch: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = getSurfaceColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = null,
                    tint = getOnSurfaceSecondaryColor(),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = hashtagInput,
                    onValueChange = onHashtagInputChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.hashtag_and_press_plus),
                            color = getOnSurfaceSecondaryColor(),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 13.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getInputBorderColor(),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor(),
                        cursorColor = getPrimaryColor()
                    ),
                    shape = RoundedCornerShape(6.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val cleanHashtag = hashtagInput.trim().removePrefix("#")
                            if (cleanHashtag.isNotBlank()) {
                                if (selectedHashtags.isEmpty()) {
                                    onAddHashtag(cleanHashtag)
                                    onSearch()
                                } else {
                                    onAddHashtag(cleanHashtag)
                                }
                            }
                        }
                    ),
                    singleLine = true,
                    trailingIcon = {
                        if (hashtagInput.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    val cleanHashtag = hashtagInput.trim().removePrefix("#")
                                    if (cleanHashtag.isNotBlank()) {
                                        if (selectedHashtags.isEmpty()) {
                                            onAddHashtag(cleanHashtag)
                                            onSearch()
                                        } else {
                                            onAddHashtag(cleanHashtag)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_hashtag),
                                    tint = getPrimaryColor()
                                )
                            }
                        }
                    },
                )
            }
            if (selectedHashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(selectedHashtags) { hashtag ->
                        HashtagChip(
                            text = hashtag,
                            onRemove = { onRemoveHashtag(hashtag) }
                        )
                    }
                }
            }
            if (selectedHashtags.isEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.add_hashtag_to_search),
                    color = getOnSurfaceSecondaryColor(),
                    fontSize = 12.sp
                )
            }
        }
    }
}