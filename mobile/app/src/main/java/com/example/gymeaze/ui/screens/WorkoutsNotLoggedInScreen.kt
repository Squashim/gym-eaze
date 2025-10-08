package com.example.gymeaze.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.ui.components.BackgroundWithDotsAndCircles
import com.example.gymeaze.ui.theme.*

@Composable
fun WorkoutsNotLoggedInScreen(
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(getBackgroundColor())
    ) {
        BackgroundWithDotsAndCircles(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        getPrimaryColor().copy(alpha = 0.1f),
                        RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = getPrimaryColor()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Plany treningowe",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = getOnSurfaceColor(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Twórz spersonalizowane plany treningowe, śledź postęp i osiągaj swoje cele fitness!",
                fontSize = 16.sp,
                color = getOnSurfaceColor().copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FeatureItem(
                    icon = Icons.Default.Create,
                    title = "Kreator planów",
                    description = "Twórz własne plany treningowe dostosowane do Twoich celów"
                )

                FeatureItem(
                    icon = Icons.Default.List,
                    title = "Organizacja ćwiczeń",
                    description = "Planuj dni treningowe i odpoczynku w dogodny sposób"
                )

                FeatureItem(
                    icon = Icons.Default.Share,
                    title = "Udostępnianie",
                    description = "Dziel się swoimi planami z innymi użytkownikami"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getPrimaryColor().copy(alpha = 0.1f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    getPrimaryColor().copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = getPrimaryColor()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Zaloguj się, aby korzystać z planów treningowych",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = getOnSurfaceColor(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Przejdź do zakładki 'Profil' i zaloguj się lub załóż nowe konto",
                        fontSize = 14.sp,
                        color = getOnSurfaceSecondaryColor(),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = getPrimaryColor()
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Znajdziesz zakładkę 'Profil' na dole ekranu",
                    fontSize = 12.sp,
                    color = getOnSurfaceSecondaryColor(),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    getPrimaryColor().copy(alpha = 0.1f),
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = getPrimaryColor()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = getOnSurfaceColor()
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = getOnSurfaceSecondaryColor(),
                lineHeight = 18.sp
            )
        }
    }
}