package com.example.gymeaze.ui.screens.profile.sections.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getCardBackgroundColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor

@Composable
fun TrainingGoalInfoDialog(
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
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = getCardBackgroundColor(),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.training_goal),
                        fontSize = 20.sp,
                        color = getOnSurfaceColor(),
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = stringResource(R.string.public_goal_desc),
                        tint = getOnSurfaceSecondaryColor(),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.training_goal_privacy_description),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = getOnSurfaceColor(),
                        textAlign = TextAlign.Start
                    )

                    Text(
                        text = stringResource(R.string.training_goal_findability_note),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = getOnSurfaceColor(),
                        textAlign = TextAlign.Start
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getOnSurfaceColor()
                        ),
                        border = BorderStroke(
                            1.dp,
                            getInputBorderColor()
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.close),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}