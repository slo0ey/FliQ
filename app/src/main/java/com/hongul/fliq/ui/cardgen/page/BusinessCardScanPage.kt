package com.hongul.fliq.ui.cardgen.page

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.coroutineContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun BusinessCardScanPage(onBack: () -> Unit = {}, onNext: () -> Unit) {
    val progress = 0.6f
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // 사진 촬영을 위한 ImageCapture 참조 저장
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    // 사진 촬영 상태 관리
    var isCapturing by remember { mutableStateOf(false) }

    // 카메라 권한 요청
    val cameraPermission = rememberPermissionState(
        Manifest.permission.CAMERA
    )

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    Log.d("Basic", "Recomposition")
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
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

            Text(
                text = "명함 사진을\n스캔해주세요.",
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Spacer(modifier = Modifier.height(16.dp))

            }

            Spacer(modifier = Modifier.height(0.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CameraPreviewView(lifecycleOwner, onImageCaptureReady = { imageCaptureInstance ->
                    imageCapture = imageCaptureInstance
                })
                Spacer(modifier = Modifier.height(0.dp))

            }

            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        // 사진을 촬영하고 완료되면 onNext 호출
                        if (!isCapturing && imageCapture != null) {
                            isCapturing = true

                            val outputDirectory = File(context.getExternalFilesDir(null), "Camera")
                            if (!outputDirectory.exists()) {
                                outputDirectory.mkdirs()
                            }
                            val photoFile = File(outputDirectory, "business_card_${System.currentTimeMillis()}.jpg")
                            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            imageCapture?.takePicture(
                                outputFileOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        Toast.makeText(context, "명함 사진을 분석중입니다...", Toast.LENGTH_SHORT).show()
                                        isCapturing = false

                                        // 2초 후에 onNext 호출
                                        android.os.Handler().postDelayed({
                                            onNext()
                                        }, 4000)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("CameraPreview", "Image capture failed", exception)
                                        Toast.makeText(context, "사진 촬영에 실패하였습니다", Toast.LENGTH_SHORT).show()
                                        isCapturing = false
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF125422))
                ) {
                    Text(text = "다음", color = Color.White, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun CameraPreviewView(lifecycleOwner: LifecycleOwner, onImageCaptureReady: (ImageCapture) -> Unit = {}) {
    AndroidView(
        factory = { context ->
            val previewView = PreviewView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            // ImageCapture 인스턴스 생성
            val imageCapture = ImageCapture.Builder().build()

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture  // ImageCapture 추가
                    )

                    // 상위 컴포넌트에 ImageCapture 인스턴스 전달
                    onImageCaptureReady(imageCapture)

                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
    )
}
