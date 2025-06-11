package com.hongul.fliq.ui.cardgen.page

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hongul.fliq.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCardProgressPage(
    onBack: () -> Unit = {},
    onNext: () -> Unit
) {
    val progress = 0.7f
    var title by remember { mutableStateOf("STEP 1. 이름 추출 중..") }

    Log.d("Basic", "Recomposition")

    LaunchedEffect(Unit) {
        delay(2000)
        title = "STEP 2. 휴대폰 번호 추출 중.."
        delay(2000)
        title = "STEP 3. 이메일 추출 중.."
        delay(2000)
        onNext()
    }

    Scaffold(
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF9CD2A1)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = title,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(modifier = Modifier.size(100.dp))
        }
    }
}
