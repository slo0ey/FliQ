package com.hongul.filq.ui.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import com.hongul.filq.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = LocalDate.now()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val schedules = remember { mutableStateMapOf<LocalDate, MutableList<Schedule>>() }
    var selectedSchedule by remember { mutableStateOf<Schedule?>(null) }

    // 기본 일정 색상
    var defaultColor by remember { mutableStateOf(Color.Red.copy(alpha = 0.6f)) }

    // 명함에 저장된 연락처 목록 (기본 참여자)
    val contacts = remember {
        mutableStateListOf(
            Contact(id = 1, name = "홍추핑구", email = "pinggu@example.com"),
            Contact(id = 2, name = "홍추핑", email = "ping@example.com")
        )
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("캘린더", fontSize = 18.sp) },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 캘린더 헤더
                CalendarHeader(currentMonth) { isNext ->
                    currentMonth = if (isNext) {
                        currentMonth.plusMonths(1)
                    } else {
                        currentMonth.minusMonths(1)
                    }
                }
                // 캘린더 본문
                CalendarBody(
                    currentMonth = currentMonth,
                    today = today,
                    selectedDate = selectedDate,
                    schedules = schedules
                ) { date ->
                    selectedDate = date
                }
                // 캘린더와 일정 목록 사이의 얇은 선
                Divider(color = Color.Gray, thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp))
                // 일정 목록
                ScheduleList(
                    selectedDate = selectedDate,
                    schedules = schedules
                ) { schedule ->
                    selectedSchedule = schedule
                }
                // 일정 추가
                if (selectedDate != null) {
                    ScheduleInput(
                        selectedDate,
                        defaultColor = defaultColor, // 기본 색상을 전달
                        schedules = schedules, // schedules 전달
                        onAddSchedule = { /* 다른 추가 작업 필요 시 구현 */ }
                    )
                }
            }

            // 일정 수정 화면
            if (selectedSchedule != null) {
                ScheduleEditScreen(
                    schedule = selectedSchedule!!,
                    contacts = contacts,
                    defaultColor = defaultColor,
                    onClose = { selectedSchedule = null },
                    onDelete = {
                        schedules[selectedDate]?.let { scheduleList ->
                            if (scheduleList.remove(selectedSchedule)) {
                                if (scheduleList.isEmpty()) {
                                    schedules.remove(selectedDate)
                                }
                            }
                        }
                        selectedSchedule = null
                    },
                    onColorChange = { newColor -> defaultColor = newColor }
                )
            }
        }
    }
}





// 명함 데이터 클래스
data class Contact(val id: Int, val name: String, val email: String)

// 일정 데이터 클래스
data class Schedule(
    val id: String,
    var title: String,
    var color: Color,
    val participants: MutableList<Contact>
)

@Composable
fun CalendarHeader(currentMonth: YearMonth, onMonthChange: (Boolean) -> Unit) {
    val monthDisplayName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val year = currentMonth.year

    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(false) }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "이전 달",
                tint = Color.Gray
            )
        }

        Text(
            text = "$monthDisplayName $year",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B5E20)
        )

        IconButton(onClick = { onMonthChange(true) }) {
            Icon(Icons.Filled.ArrowForward,
                contentDescription = "다음 달",
                tint = Color.Gray
            )
        }
    }
}

// 캘린더 본문 수정
@Composable
fun CalendarBody(
    currentMonth: YearMonth,
    today: LocalDate,
    selectedDate: LocalDate?,
    schedules: Map<LocalDate, List<Schedule>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value % 7
    val lastDayOfMonth = (firstDayOfMonth + daysInMonth - 1) % 7

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 요일 헤더
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            val daysOfWeek = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
            daysOfWeek.forEach { day ->
                Text(text = day, fontSize = 12.sp, color = Color.Gray)
            }
        }

        // 날짜 표시
        val days = (1..daysInMonth).toList()
        val emptyDaysBefore = List(firstDayOfMonth) { "" }
        val emptyDaysAfter = List(6 - lastDayOfMonth) { "" }
        val dayChunks = (emptyDaysBefore + days.map { it.toString() } + emptyDaysAfter).chunked(7)

        dayChunks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                week.forEach { day ->
                    val date = day.toIntOrNull()?.let { currentMonth.atDay(it) }
                    val isToday = date == today
                    val isSelected = date == selectedDate

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = if (isSelected && selectedDate != null) Color(0xFF1B5E20) else Color.Transparent,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { date?.let(onDateSelected) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 16.sp,
                                fontWeight = when {
                                    isToday -> FontWeight.Bold
                                    else -> FontWeight.Normal
                                },
                                color = when {
                                    isToday -> Color(0xFF1B5E20)
                                    else -> Color.Black
                                }
                            )
                        }
                        // 날짜 아래 일정 색상 점
                        if (date != null && schedules.containsKey(date)) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                schedules[date]!!.take(3).forEach { schedule ->
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(schedule.color, shape = CircleShape)
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// 일정 목록
@Composable
fun ScheduleList(
    selectedDate: LocalDate?,
    schedules: Map<LocalDate, List<Schedule>>,
    onScheduleClick: (Schedule) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (selectedDate != null) {
            Text(
                text = "${selectedDate.monthValue}월 ${selectedDate.dayOfMonth}일",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            val daySchedules = schedules[selectedDate]
            if (daySchedules.isNullOrEmpty()) {
                Text(
                    text = "일정이 없습니다",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                daySchedules.forEach { schedule ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onScheduleClick(schedule) }
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 일정 이름 왼쪽에 기본 색상 원
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(schedule.color, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = schedule.title,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        if (schedule.participants.isNotEmpty()) {
                            Text(
                                text = "참여자: ${schedule.participants.joinToString(", ") { "${it.name} (${it.email})" }}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInput(
    selectedDate: LocalDate?,
    defaultColor: Color, // 기본 색상 추가
    schedules: MutableMap<LocalDate, MutableList<Schedule>>, // schedules 전달
    onAddSchedule: (Schedule) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
                Text(text = "${selectedDate?.monthValue}월 ${selectedDate?.dayOfMonth}일에 일정 추가")
            },
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(24.dp)),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (inputText.isNotBlank() && selectedDate != null) {
                    // 새 일정 생성
                    val newSchedule = Schedule(
                        id = UUID.randomUUID().toString(),
                        title = inputText,
                        color = defaultColor, // 기본 색상
                        participants = mutableListOf() // 빈 참여자 리스트
                    )
                    // schedules에 추가
                    schedules.getOrPut(selectedDate) { mutableListOf() }.add(newSchedule)
                    onAddSchedule(newSchedule) // 콜백 호출
                    inputText = "" // 입력 초기화
                }
            },
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFF1B5E20), shape = CircleShape) // 녹색 버튼
        ) {
            Icon(Icons.Filled.Add, contentDescription = "일정 추가", tint = Color.White)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleEditScreen(
    schedule: Schedule,
    contacts: List<Contact>,
    defaultColor: Color, // 기본 색상 추가
    onClose: () -> Unit,
    onDelete: () -> Unit,
    onColorChange: (Color) -> Unit // 색상 변경 콜백 추가
) {
    var title by remember { mutableStateOf(schedule.title) }
    var color by remember { mutableStateOf(schedule.color) }
    // 참여자 초기 상태를 빈 리스트로 설정
    val selectedParticipants = remember { mutableStateListOf<Contact>().apply { addAll(schedule.participants) } }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) } // 색상 선택 팝업 상태 추가

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("일정 수정", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.trash), // trash.xml
                            contentDescription = "삭제"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            // 일정 이름과 색상
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, shape = CircleShape)
                        .clickable { showColorPickerDialog = true } // 색상 선택 팝업 열기
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("일정 이름") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 참여자 선택
            Text(text = "참여자", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            contacts.forEach { contact ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedParticipants.contains(contact)) {
                                selectedParticipants.remove(contact)
                            } else {
                                selectedParticipants.add(contact)
                            }
                        }
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = selectedParticipants.contains(contact),
                        onCheckedChange = {
                            if (it) {
                                selectedParticipants.add(contact)
                            } else {
                                selectedParticipants.remove(contact)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF1B5E20), // 녹색 체크박스
                            uncheckedColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${contact.name} (${contact.email})")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    schedule.title = title
                    schedule.color = color
                    schedule.participants.clear()
                    schedule.participants.addAll(selectedParticipants)
                    onClose()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)) // 녹색 저장 버튼
            ) {
                Text("저장", color = Color.White)
            }
        }
    }

    // 삭제 확인 팝업
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("일정을 삭제하시겠습니까?") },
            text = { Text("확인을 누르면 일정이 완전히 삭제됩니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Red, // 빨간색 확인 버튼
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("취소")
                }
            },
            containerColor = Color.White
        )
    }

    // 색상 선택 팝업
    if (showColorPickerDialog) {
        ColorPickerDialog(
            onColorSelected = { selectedColor ->
                color = selectedColor
                showColorPickerDialog = false
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }
}




@Composable
fun ColorPickerDialog(
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    // 맨 왼쪽 위 색상을 기본값으로 설정
    val defaultBasicColor = Color.Red.copy(alpha = 0.6f)
    var currentColor by remember { mutableStateOf(defaultBasicColor) }

    val basicColors = listOf(
        defaultBasicColor,
        Color(0xFFFF7F50).copy(alpha = 0.6f), // Coral
        Color(0xFFFFA500).copy(alpha = 0.6f), // Orange
        Color.Yellow.copy(alpha = 0.6f),
        Color(0xFFADFF2F).copy(alpha = 0.6f), // Green Yellow
        Color.Green.copy(alpha = 0.6f),
        Color.Cyan.copy(alpha = 0.6f),
        Color.Blue.copy(alpha = 0.6f),
        Color(0xFF8A2BE2).copy(alpha = 0.6f), // Blue Violet
        Color.Magenta.copy(alpha = 0.6f),
        Color(0xFFFF69B4).copy(alpha = 0.6f), // Hot Pink
        Color(0xFFA52A2A).copy(alpha = 0.6f), // Brown
        Color.Gray.copy(alpha = 0.6f),
        Color.DarkGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.6f),
        Color(0xFF556B2F).copy(alpha = 0.6f), // Dark Olive Green
        Color(0xFF708090).copy(alpha = 0.6f), // Slate Gray
        Color(0xFFB0C4DE).copy(alpha = 0.6f), // Light Steel Blue
        Color(0xFFD2B48C).copy(alpha = 0.6f), // Tan
        Color.Black.copy(alpha = 0.6f)
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("색상 선택", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 기본 색상 버튼
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    basicColors.chunked(5).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .background(color, shape = CircleShape)
                                        .clickable { currentColor = color }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 선택된 색상 미리보기
                Text("선택된 색상", fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20))
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(currentColor, shape = CircleShape)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(currentColor) // 선택된 색상을 전달
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1B5E20), // 버튼 배경색
                    contentColor = Color.White // 텍스트 색상
                ),
                shape = RoundedCornerShape(20), // 버튼을 둥글게 만듦
                modifier = Modifier
                    .height(40.dp) // 버튼 높이
                    .padding(horizontal = 5.dp) // 버튼 여백 줄임
            ) {
                Text(
                    "확인",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold // 강조 텍스트
                )
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // 버튼 배경 투명
                    contentColor = Color(0xFF1B5E20) // 텍스트 색상 녹색
                ),
                shape = RoundedCornerShape(20), // 둥근 모양 유지
                elevation = ButtonDefaults.buttonElevation(0.dp), // 버튼 그림자 제거
                modifier = Modifier
                    .height(40.dp) // 버튼 높이
                    .padding(horizontal = 5.dp) // 버튼 여백 줄임
            ) {
                Text(
                    "취소",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal // 일반 텍스트
                )
            }
        },
        containerColor = Color.White // 배경 색상을 흰색으로 설정
    )
}




@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen()
}

@Preview(showBackground = true, name = "Schedule Edit Preview")
@Composable
fun ScheduleEditScreenPreview() {
    ScheduleEditScreen(
        schedule = Schedule(
            id = "1",
            title = "팀 미팅",
            color = Color.Blue.copy(alpha = 0.6f),
            participants = mutableListOf(
                Contact(id = 1, name = "홍추핑구", email = "pinggu@example.com"),
                Contact(id = 2, name = "홍추핑", email = "ping@example.com")
            )
        ),
        contacts = listOf(
            Contact(id = 1, name = "홍추핑구", email = "pinggu@example.com"),
            Contact(id = 2, name = "홍추핑", email = "ping@example.com"),
            Contact(id = 3, name = "홍길동", email = "gil@example.com")
        ),
        defaultColor = Color.Red.copy(alpha = 0.6f),
        onClose = {},
        onDelete = {},
        onColorChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    ColorPickerDialog(onColorSelected = {}, onDismiss = {})
}

