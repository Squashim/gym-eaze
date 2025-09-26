package com.example.gymeaze.ui.screens.posts.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.ui.theme.*

@Composable
fun LoginToLikeDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = getCardBackgroundColor(),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Funkcja dostępna po zalogowaniu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )

                Text(
                    text = "Zaloguj się, aby móc korzystać z tych funkcji",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getPrimaryColor(),
                            contentColor = getOnPrimaryColor()
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "OK",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnPrimaryColor()
                        )
                    }
                }
            }
        }
    }
}