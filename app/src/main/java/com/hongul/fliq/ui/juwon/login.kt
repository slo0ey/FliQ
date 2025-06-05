package com.hongul.fliq.ui.juwon

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hongul.fliq.R
import com.hongul.fliq.api.Api
import com.hongul.fliq.api.user.dto.CreateUserBody
import com.hongul.fliq.model.user.User
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val PlayfairFont = FontFamily(
    Font(R.font.playfair_display_black, FontWeight.Normal),
    Font(R.font.playfair_bold, FontWeight.Bold)
)
val PretendardFont = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_semibold, FontWeight.Bold),
)

@Composable
fun FliQLoginScreen(onLogin: (User) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FliQ 로고
        Text(
            text = "FliQ",
            style = TextStyle(
                fontFamily = PlayfairFont,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(25.dp))

        // 설명 문구
        Text(
            text = "터치 한 번으로 빠르게 명함 전달",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Black,
                fontFamily = PretendardFont,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(40.dp))

        if (isLoading) {
            // 로딩 중일 때 표시할 컴포넌트
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        } else {
            // 카카오톡 로그인 버튼
            KakaoLoginButton(context, coroutineScope) {
                isLoading = true
                onLogin(it)
            }
        }
    }
}

@Composable
fun KakaoLoginButton(context: Context, scope: CoroutineScope, onLogin: (User) -> Unit) {
    Button(
        onClick = {
            UserApiClient.instance.loginWithKakaoTalk(context) {
                token, error ->
                if (error != null) {
                    // 로그인 실패 처리
                    println("로그인 실패: $error")
                } else if (token != null) {
                    // 로그인 성공 처리
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            println("사용자 정보 요청 실패: $error")
                        } else if (user != null) {
                            Log.d("KakaoLogin", "사용자 정보: $user")
                            // 사용자 정보 가져오기 성공
                            scope.launch {
                                val id = user.id!!
                                val name = user.kakaoAccount?.name ?: "Unknown"
                                val email = user.kakaoAccount?.email ?: ""
                                val profileImageURL = user.kakaoAccount?.profile?.profileImageUrl ?: ""

                                var fliqUser = Api.user.getUser(id).body()?.toUser()
                                if (fliqUser == null)
                                    Api.user.createUser(
                                        createUserBody = CreateUserBody(
                                            id = id,
                                            name = name,
                                            email = email,
                                            profileImageURL = profileImageURL,
                                        )
                                    )
                                onLogin(fliqUser ?: User(
                                    id = id,
                                    name = name,
                                    email = email,
                                    profileImageURL = profileImageURL
                                ))
                            }
                        }
                    }
                }
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE812)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(50.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kakao_logo),
                    contentDescription = "Kakao Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                text = "카카오톡 계정으로 로그인",
                color = Color.Black,
                fontSize = 15.sp,
                fontFamily = PretendardFont,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFliQLoginScreen() {
    FliQLoginScreen(onLogin = { _ ->})
}