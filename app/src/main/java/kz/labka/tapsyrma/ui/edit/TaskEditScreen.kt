package kz.labka.tapsyrma.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kz.labka.tapsyrma.data.Priority
import kz.labka.tapsyrma.domain.TaskValidator
import kz.labka.tapsyrma.ui.AppViewModelProvider
import kz.labka.tapsyrma.ui.common.format
import java.time.LocalDate

private const val MILLIS_IN_DAY = 86_400_000L

@Composable
fun TaskEditScreen(
    onBack: () -> Unit,
    viewModel: TaskEditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val state = viewModel.uiState
    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }
    TaskEditContent(
        state = state,
        onBack = onBack,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onDueDateChange = viewModel::onDueDateChange,
        onSave = viewModel::save,
        onMessageShown = viewModel::onMessageShown,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditContent(
    state: TaskEditUiState,
    onBack: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (LocalDate?) -> Unit,
    onSave: () -> Unit,
    onMessageShown: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            onMessageShown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNew) "Жаңа тапсырма" else "Тапсырманы өзгерту") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Артқа")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            TaskForm(
                state = state,
                modifier = Modifier.padding(padding),
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onPriorityChange = onPriorityChange,
                onDueDateChange = onDueDateChange,
                onPickDate = { showDatePicker = true },
                onSave = onSave,
            )
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = (state.dueDate ?: LocalDate.now()).toEpochDay() * MILLIS_IN_DAY,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // DatePicker таңдалған күнді UTC бойынша миллисекундпен қайтарады.
                    pickerState.selectedDateMillis?.let { onDueDateChange(LocalDate.ofEpochDay(it / MILLIS_IN_DAY)) }
                    showDatePicker = false
                }) { Text("Таңдау") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Болдырмау") }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun TaskForm(
    state: TaskEditUiState,
    modifier: Modifier,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onDueDateChange: (LocalDate?) -> Unit,
    onPickDate: () -> Unit,
    onSave: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Атауы *") },
            singleLine = true,
            isError = state.errors.titleError != null,
            supportingText = {
                Text(state.errors.titleError ?: "${state.title.trim().length} / ${TaskValidator.TITLE_MAX}")
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )
        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Сипаттама") },
            minLines = 3,
            maxLines = 6,
            isError = state.errors.descriptionError != null,
            supportingText = {
                Text(
                    state.errors.descriptionError
                        ?: "${state.description.trim().length} / ${TaskValidator.DESCRIPTION_MAX}",
                )
            },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )

        Text("Маңыздылығы", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Priority.entries.forEach { priority ->
                FilterChip(
                    selected = state.priority == priority,
                    onClick = { onPriorityChange(priority) },
                    label = { Text(priority.label) },
                )
            }
        }

        Text("Орындау мерзімі", style = MaterialTheme.typography.titleSmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onPickDate) {
                Icon(Icons.Filled.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(state.dueDate?.format() ?: "Күнді таңдау")
            }
            if (state.dueDate != null) {
                IconButton(onClick = { onDueDateChange(null) }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Мерзімді алып тастау")
                }
            }
        }
        state.errors.dueDateError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
            Text("Сақтау")
        }
    }
}
