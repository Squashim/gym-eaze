package com.example.gymeaze.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.*
import com.example.gymeaze.viewmodel.ReportItemType
import com.example.gymeaze.viewmodel.ReportState
import com.example.gymeaze.viewmodel.ReportType
import com.example.gymeaze.viewmodel.ReportViewModel

@Composable
fun ReportDialog(
    itemId: String,
    itemType: ReportItemType,
    onDismiss: () -> Unit,
    viewModel: ReportViewModel
) {
    var selectedReportType by remember { mutableStateOf<ReportType?>(null) }
    var reasonText by remember { mutableStateOf("") }
    val reportState by viewModel.reportState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(reportState) {
        when (val state = reportState) {
            is ReportState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onDismiss()
                viewModel.resetState()
            }
            is ReportState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = getCardBackgroundColor(),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.report_title_format, itemType.displayName.lowercase()),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor()
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.report_select_reason),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor()
                    )

                    ReportType.values().forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReportType = type }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedReportType == type,
                                onClick = { selectedReportType = type }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = type.displayName,
                                fontSize = 14.sp,
                                color = getOnSurfaceColor()
                            )
                        }
                    }
                }

                if (selectedReportType == ReportType.OTHER) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.report_reason_label),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = getOnSurfaceColor()
                        )

                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = {
                                if (it.length <= 250) reasonText = it
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.report_reason_hint),
                                    color = getOnSurfaceSecondaryColor()
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            singleLine = false,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = getPrimaryColor(),
                                unfocusedBorderColor = getInputBorderColor(),
                                focusedTextColor = getOnSurfaceColor(),
                                unfocusedTextColor = getOnSurfaceColor()
                            ),
                            supportingText = {
                                Text(
                                    text = "${reasonText.length}/250",
                                    fontSize = 12.sp,
                                    color = getOnSurfaceSecondaryColor(),
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End
                                )
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = reportState !is ReportState.Loading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getOnSurfaceColor()
                        ),
                        border = BorderStroke(1.dp, getInputBorderColor()),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.action_cancel),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            selectedReportType?.let { type ->
                                val reason = if (type == ReportType.OTHER) {
                                    reasonText.trim().takeIf { it.isNotBlank() }
                                } else null

                                if (type == ReportType.OTHER && reason.isNullOrBlank()) {
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.report_error_reason_required),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@let
                                }

                                viewModel.reportItem(
                                    itemId = itemId,
                                    itemType = itemType,
                                    reportType = type,
                                    reason = reason
                                )
                            }
                        },
                        enabled = selectedReportType != null && reportState !is ReportState.Loading,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (selectedReportType != null)
                                getPrimaryColor() else getOnSurfaceSecondaryColor()
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (selectedReportType != null)
                                getPrimaryColor() else getInputBorderColor()
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (reportState is ReportState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = getPrimaryColor()
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.action_report),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}