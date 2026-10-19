package kz.labka.tapsyrma.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.labka.tapsyrma.data.Task
import kz.labka.tapsyrma.ui.AppViewModelProvider
import kz.labka.tapsyrma.ui.common.PriorityBadge
import kz.labka.tapsyrma.ui.common.format
import kz.labka.tapsyrma.ui.common.relativeLabel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TaskDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: TaskDetailViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TaskDetailContent(
        state = state,
        onBack = onBack,
        onEdit = onEdit,
        onToggleDone = viewModel::toggleDone,
        onDelete = { viewModel.delete(onDeleted = onBack) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailContent(
    state: TaskDetailUiState,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit,
    today: LocalDate = LocalDate.now(),
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val task = state.task

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Тапсырма туралы") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Артқа")
                    }
                },
                actions = {
                    if (task != null) {
                        IconButton(onClick = { onEdit(task.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Өзгерту")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Жою")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                task == null -> Text(
                    text = "Тапсырма табылмады. Ол жойылған болуы мүмкін.",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(32.dp),
                )
                else -> TaskDetails(task, today, onToggleDone)
            }
        }
    }

    if (showDeleteDialog && task != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Тапсырманы жою") },
            text = { Text("«${task.title}» тапсырмасы біржола жойылады. Жалғастырасыз ба?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) { Text("Жою", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Болдырмау") }
            },
        )
    }
}

@Composable
private fun TaskDetails(task: Task, today: LocalDate, onToggleDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow("Күйі") {
                    val (label, color) = when {
                        task.isDone -> "Орындалды" to Color(0xFF388E3C)
                        task.isOverdue(today) -> "Мерзімі өтті" to MaterialTheme.colorScheme.error
                        else -> "Орындалуда" to MaterialTheme.colorScheme.primary
                    }
                    Text(label, color = color, fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider()
                InfoRow("Маңыздылығы") { PriorityBadge(task.priority) }
                HorizontalDivider()
                InfoRow("Мерзімі") {
                    Text(task.dueDate?.relativeLabel(today) ?: "Көрсетілмеген")
                }
                HorizontalDivider()
                InfoRow("Құрылған күні") {
                    val created = Instant.ofEpochMilli(task.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
                    Text(created.format())
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Сипаттама", style = MaterialTheme.typography.titleSmall)
            Text(
                text = task.description.ifBlank { "Сипаттама жазылмаған." },
                style = MaterialTheme.typography.bodyLarge,
                color = if (task.description.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
        }
        if (task.isDone) {
            OutlinedButton(onClick = onToggleDone, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Қайта ашу")
            }
        } else {
            Button(onClick = onToggleDone, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Орындалды деп белгілеу")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        value()
    }
}
