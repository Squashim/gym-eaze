package com.example.gymeaze.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gymeaze.R
import com.example.gymeaze.ui.theme.BackgroundDark
import com.example.gymeaze.ui.theme.GymEazeTheme
import com.example.gymeaze.ui.theme.OnSurfaceDark
import com.example.gymeaze.ui.theme.PrimaryGreen

@Composable
fun IntroductionScreen(
    onCompleteIntroduction: () -> Unit
){
    Surface(modifier = Modifier.fillMaxSize(),
        color = BackgroundDark)
    {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = stringResource(R.string.welcome),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.introduction),
                fontSize = 16.sp,
                color = OnSurfaceDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onCompleteIntroduction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text(
                    text = stringResource(R.string.begin),
                    color = OnSurfaceDark
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TODO uzupelnienie dalszych danych uzytkownika",
                fontSize = 12.sp,
                color = OnSurfaceDark.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun IntroductionScreenPreview(){
    GymEazeTheme {
        IntroductionScreen({})
    }
}