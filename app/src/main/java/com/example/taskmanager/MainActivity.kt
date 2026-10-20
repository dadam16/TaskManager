package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Task(
    val id: Int,
    val name: String,
    val category: String,
    val priority: Int,
    val isCompleted: Boolean
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TaskManagerScreen()
            }
        }
    }
}

@Composable
fun TaskManagerScreen() {
    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var taskName: String by remember {
        mutableStateOf("")
    }

    var category: String by remember {
        mutableStateOf("")
    }

    var priorityStr: String by remember {
        mutableStateOf("")
    }

    var nextId: Int by remember {
        mutableStateOf(1)
    }

    val tasks = remember {
        mutableStateListOf<Task>()
    }

    var nameError: Boolean by remember {
        mutableStateOf(false)
    }

    var categoryError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityError: Boolean by remember {
        mutableStateOf(false)
    }

    var priorityErrorMessage: String by remember {
        mutableStateOf("")
    }

    var selectedFilter: String by remember {
        mutableStateOf("Összes")
    }

    var editingTask by remember {
        mutableStateOf<Task?>(null)
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Feladatkezelő",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Szervezd meg a napi teendőidet!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            OutlinedTextField(
                value = taskName,
                onValueChange = {
                    taskName = it
                    nameError = false
                },
                label = { Text("Feladat neve") },
                supportingText = {
                    if (nameError) {
                        Text(
                            text = "A feladat neve kötelező!",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = nameError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {
                        category = it
                        categoryError = false
                    },
                    label = { Text("Kategória") },
                    supportingText = {
                        if (categoryError) {
                            Text(
                                text = "A kategória nem lehet üres!",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = categoryError,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = priorityStr,
                    onValueChange = {
                        priorityStr = it
                        priorityError = false
                    },
                    label = { Text("Prioritás (1-3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = {
                        if (priorityError) {
                            Text(
                                text = priorityErrorMessage,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    isError = priorityError,
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Button(
                onClick = {
                    nameError = taskName.isBlank()
                    categoryError = category.isBlank()

                    val prioInt = priorityStr.toIntOrNull()
                    if (prioInt == null) {
                        priorityError = true
                        priorityErrorMessage = "A prioritásnak számnak kell lennie!"
                    } else if (prioInt !in 1..3) {
                        priorityError = true
                        priorityErrorMessage = "A prioritás csak 1 és 3 közötti érték lehet!"
                    } else {
                        priorityError = false
                        priorityErrorMessage = ""
                    }

                    if (nameError || categoryError || priorityError) {
                        Toast.makeText(
                            context,
                            "Ellenőrizd a megadott adatokat!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val newTask = Task(
                            id = nextId,
                            name = taskName.trim(),
                            category = category.trim(),
                            priority = prioInt!!,
                            isCompleted = false
                        )

                        tasks.add(newTask)
                        nextId++

                        Toast.makeText(
                            context,
                            "Feladat sikeresen hozzáadva!",
                            Toast.LENGTH_SHORT
                        ).show()

                        taskName = ""
                        category = ""
                        priorityStr = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Feladat hozzáadása")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { selectedFilter = "Összes" }) {
                    Text("Összes")
                }
                Button(onClick = { selectedFilter = "Aktív" }) {
                    Text("Aktív")
                }
                Button(onClick = { selectedFilter = "Elkészült" }) {
                    Text("Elkészült")
                }
            }

            val filteredTasks = when (selectedFilter) {
                "Aktív" -> tasks.filter { !it.isCompleted }
                "Elkészült" -> tasks.filter { it.isCompleted }
                else -> tasks
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Még nincs feladatod.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    val emptyMsg = when (selectedFilter) {
                        "Aktív" -> "Nincs aktív feladat."
                        "Elkészült" -> "Még nincs elkészült feladat."
                        else -> "Még nincs feladatod."
                    }
                    Text(
                        text = emptyMsg,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = filteredTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = {
                                val index = tasks.indexOf(task)
                                if (index != -1) {
                                    tasks[index] = task.copy(isCompleted = !task.isCompleted)
                                }
                            },
                            onEdit = {
                                editingTask = task
                            },
                            onDelete = {
                                tasks.remove(task)
                            }
                        )
                    }
                }
            }
        }
    }

    if (editingTask != null) {
        EditTaskDialog(
            task = editingTask!!,
            onDismiss = { editingTask = null },
            onSave = { updatedName, updatedCategory, updatedPriority ->
                val index = tasks.indexOf(editingTask)
                if (index != -1) {
                    tasks[index] = editingTask!!.copy(
                        name = updatedName,
                        category = updatedCategory,
                        priority = updatedPriority
                    )
                }
                editingTask = null
            }
        )
    }
}

@Composable
fun TaskAvatar(priority: Int) {
    val avatarBgColor = when (priority) {
        1 -> Color(0xFF81C784)
        2 -> Color(0xFFFFB74D)
        3 -> Color(0xFFE57373)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(avatarBgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = priority.toString(),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }

    val priorityText = when (task.priority) {
        1 -> "1 → Alacsony prioritás"
        2 -> "2 → Közepes prioritás"
        3 -> "3 → Magas prioritás"
        else -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaskAvatar(task.priority)
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = priorityText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    if (task.isCompleted) {
                        Text(
                            text = "Elkészült",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Szerkesztés"
                    )
                }
                IconButton(
                    onClick = {
                        showDeleteDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Törlés"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onToggleComplete,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (task.isCompleted) "Újra aktív" else "Elkészült")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = { Text("Feladat törlése") },
            text = { Text("Biztosan törölni szeretnéd az „${task.name}” feladatot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) { Text("Törlés") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                    }
                ) { Text("Mégse") }
            }
        )
    }
}

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (String, String, Int) -> Unit
) {
    val context = LocalContext.current

    var editName by remember { mutableStateOf(task.name) }
    var editCategory by remember { mutableStateOf(task.category) }
    var editPriorityStr by remember { mutableStateOf(task.priority.toString()) }

    var nameError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var priorityError by remember { mutableStateOf(false) }
    var priorityErrorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Feladat szerkesztése") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = {
                        editName = it
                        nameError = false
                    },
                    label = { Text("Feladat neve") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (nameError) {
                    Text(text = "A feladat neve kötelező!", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = editCategory,
                    onValueChange = {
                        editCategory = it
                        categoryError = false
                    },
                    label = { Text("Kategória") },
                    isError = categoryError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (categoryError) {
                    Text(text = "A kategória nem lehet üres!", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = editPriorityStr,
                    onValueChange = {
                        editPriorityStr = it
                        priorityError = false
                    },
                    label = { Text("Prioritás (1-3)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = priorityError,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (priorityError) {
                    Text(text = priorityErrorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    nameError = editName.isBlank()
                    categoryError = editCategory.isBlank()

                    val prioInt = editPriorityStr.toIntOrNull()
                    if (prioInt == null) {
                        priorityError = true
                        priorityErrorMessage = "A prioritásnak számnak kell lennie!"
                    } else if (prioInt !in 1..3) {
                        priorityError = true
                        priorityErrorMessage = "A prioritás csak 1 és 3 közötti érték lehet!"
                    } else {
                        priorityError = false
                        priorityErrorMessage = ""
                    }

                    if (nameError || categoryError || priorityError) {
                        Toast.makeText(
                            context,
                            "Ellenőrizd a megadott adatokat!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onSave(editName.trim(), editCategory.trim(), prioInt!!)
                    }
                }
            ) {
                Text("Mentés")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse")
            }
        }
    )
}