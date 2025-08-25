package com.example.gymeaze.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.getErrorColor
import com.example.gymeaze.ui.theme.getInputBorderColor
import com.example.gymeaze.ui.theme.getOnPrimaryColor
import com.example.gymeaze.ui.theme.getOnSurfaceColor
import com.example.gymeaze.ui.theme.getOnSurfaceSecondaryColor
import com.example.gymeaze.ui.theme.getPrimaryColor
import com.example.gymeaze.ui.theme.getSurfaceColor

@Composable
fun AccountLinkingDialog(
    email: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = getSurfaceColor())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.connect_accounts),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = getOnSurfaceColor(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.account_linking_message, email),
                    fontSize = 14.sp,
                    color = getOnSurfaceSecondaryColor(),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {Text(stringResource(R.string.password))},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = getOnSurfaceSecondaryColor()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = getPrimaryColor(),
                        unfocusedBorderColor = getInputBorderColor(),
                        focusedTextColor = getOnSurfaceColor(),
                        unfocusedTextColor = getOnSurfaceColor(),
                        cursorColor = getPrimaryColor()
                    ),
                    shape = RoundedCornerShape(8.dp),
                    isError = errorMessage != null
                )

                if (errorMessage != null ){
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = getErrorColor(),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = getOnSurfaceColor()
                        )
                    }

                    Button(
                        onClick = {onConfirm(password)},
                        modifier = Modifier.weight(1f),
                        enabled = password.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getPrimaryColor()
                        )
                    ) {
                        if(isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = getOnPrimaryColor()
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.connect_accounts_button),
                                color = getOnPrimaryColor()
                            )
                        }
                    }
                }
            }
        }
    }
}

