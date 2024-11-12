package com.hongul.filq.ui.customize

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hongul.filq.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizingScreen() {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                title = { Text("") },
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "내 명함 추가",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            Text(
                text = "여러분의 개성을 담은 명함,\n 세상에 단 하나뿐인 디자인!",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp),
                style = androidx.compose.ui.text.TextStyle(color = Color.Gray, fontSize = 14.sp)
            )

            Button(
                onClick = { /* 명함 만들기 버튼 클릭 로직 */ },
                modifier = Modifier
                    .size(width = 319.dp, height = 160.dp)
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(15.dp)), // 테두리 추가
                shape = RoundedCornerShape(10.dp), // 버튼 모서리 둥글게
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.fillMaxSize(), // Column이 Button 크기만큼 차지
                    horizontalAlignment = Alignment.CenterHorizontally, // 중앙 정렬
                    verticalArrangement = Arrangement.Center // 세로 중앙 정렬
                    ) {
                    Image(
                        painter = painterResource(id = R.drawable.add_business_card),
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("명함 만들기"
                    ,color = Color.Black )
                }
            }

            Button(
                onClick = { /* 명함 사진 불러오기 버튼 클릭 로직 */ },
                modifier = Modifier
                    .size(width = 319.dp, height = 160.dp)
                    .padding(vertical = 8.dp)
                    .border(1.dp, Color.Black, RoundedCornerShape(15.dp)), // 테두리 추가
                shape = RoundedCornerShape(10.dp), // 버튼 모서리 둥글게
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                // Column이 Button을 가득 채우도록 설정
                Column(
                    modifier = Modifier.fillMaxSize(), // Column이 Button 크기만큼 차지
                    horizontalAlignment = Alignment.CenterHorizontally, // 중앙 정렬
                    verticalArrangement = Arrangement.Center // 세로 중앙 정렬
                ) {
                    // 이미지
                    Image(
                        painter = painterResource(id = R.drawable.load_business_card), // 이미지 파일명 변경
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )

                    // 이미지와 텍스트 사이에 10dp 간격 추가
                    Spacer(modifier = Modifier.height(10.dp))

                    // 텍스트
                    Text(
                        "명함 사진 불러오기",
                        color = Color.Black // 글자색을 검정색으로 설정
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_caution),
                    contentDescription = null,
                )
                Text("명함 사진 불러오기란?", textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { })
            }
        }
    }
}