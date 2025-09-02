package com.example.gymeaze.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.data.models.Hashtag
import com.example.gymeaze.ui.theme.*

@Composable
fun HashtagInputField(
    selectedHashtags: List<Hashtag>,
    onHashtagsChange: (List<Hashtag>) -> Unit,
    availableHashtags: List<Hashtag>,
    modifier: Modifier = Modifier,
    maxHashtags: Int = 50
) {
    var inputText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = modifier) {
        Text(
            text = "Hashtagi",
            color = getOnSurfaceColor(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isLetterOrDigit() || it == ',' }.lowercase()
                inputText = filtered
            },
            placeholder = {
                Text(
                    text = "Dodaj hashtagi, np. biceps, triceps - użyj Entera lub przecinka",
                    color = getOnSurfaceSecondaryColor()
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = getPrimaryColor(),
                unfocusedBorderColor = getInputBorderColor(),
                focusedTextColor = getOnSurfaceColor(),
                unfocusedTextColor = getOnSurfaceColor(),
                cursorColor = getPrimaryColor()
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (inputText.isNotBlank()) {
                        addHashtag(inputText.trim(), selectedHashtags, onHashtagsChange, maxHashtags)
                        inputText = ""
                    }
                    keyboardController?.hide()
                }
            )
        )

        LaunchedEffect(inputText) {
            if (inputText.contains(",")) {
                val parts = inputText.split(",")
                val newHashtag = parts[0].trim()
                if (newHashtag.isNotBlank()) {
                    addHashtag(newHashtag, selectedHashtags, onHashtagsChange, maxHashtags)
                }
                inputText = parts.drop(1).joinToString(",").trimStart()
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hashtag może zawierać tylko małe litery i cyfry. Możesz dodać do $maxHashtags hashtagów.",
            color = getOnSurfaceSecondaryColor(),
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        if (selectedHashtags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedHashtags) { hashtag ->
                    HashtagChip(
                        text = hashtag.name,
                        onRemove = {
                            onHashtagsChange(selectedHashtags - hashtag)
                        }
                    )
                }
            }
        }

        if (selectedHashtags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${selectedHashtags.size}/$maxHashtags hashtagów",
                color = if (selectedHashtags.size >= maxHashtags)
                    MaterialTheme.colorScheme.error else getOnSurfaceSecondaryColor(),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun HashtagChip(
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
                text = "#$text",
                color = getPrimaryColor(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "×",
                color = getPrimaryColor(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onRemove() }
                    .padding(2.dp)
            )
        }
    }
}

private fun addHashtag(
    input: String,
    currentHashtags: List<Hashtag>,
    onHashtagsChange: (List<Hashtag>) -> Unit,
    maxHashtags: Int
) {
    if (currentHashtags.size >= maxHashtags) return

    val cleanInput = input.lowercase().trim()
    if (cleanInput.isEmpty()) return

    val exists = currentHashtags.any { it.name.equals(cleanInput, ignoreCase = true) }
    if (exists) return

    if (!cleanInput.all { it.isLetterOrDigit() }) return

    if (cleanInput.length < 2 || cleanInput.length > 30) return

    val newHashtag = Hashtag(
        _id = "", //generowane przez backend
        name = cleanInput,
        description = null,
        category = null,
        usageCount = 0
    )

    onHashtagsChange(currentHashtags + newHashtag)
}