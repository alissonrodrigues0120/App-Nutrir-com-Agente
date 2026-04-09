package com.example.nutriragente.ui.avaliacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.data.model.Forms
import com.example.nutriragente.data.repository.FormRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FormViewModel(
    private val userId: String,
    private val childId: String,
    private val formType: FormType,
    private val birthDate: LocalDate
) : ViewModel() {

    private val repository = FormRepository(userId, childId, formType.name)

    private val _uiState = MutableStateFlow<FormUiState>(FormUiState.Loading)
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()

    private val _currentAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val currentAnswers: StateFlow<Map<String, String>> = _currentAnswers.asStateFlow()

    init {
        loadForm()
    }

    private fun loadForm() {
        viewModelScope.launch {
            repository.getFormByType(formType.name)
                .catch { e ->
                    _uiState.value = FormUiState.Error(e.message ?: "Erro ao carregar")
                }
                .collect { form ->
                    if (form != null) {
                        _currentAnswers.value = form.responses
                        _uiState.value = FormUiState.Ready(form.responses, form.isCompleted)
                    } else {
                        _uiState.value = FormUiState.Ready(emptyMap(), false)
                    }
                }
        }
    }

    fun updateAnswer(key: String, value: String) {
        _currentAnswers.update { it + (key to value) }
    }

    fun saveForm() {
        viewModelScope.launch {
            val form = Forms(
                userId = userId,
                childId = childId,
                formType = formType.name,
                childBirthDate = birthDate.toString(),
                responses = _currentAnswers.value,
                isCompleted = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            repository.saveForm(form)
                .onSuccess {
                    _uiState.value = FormUiState.Saved
                }
                .onFailure { exception ->
                    _uiState.value = FormUiState.Error(exception.message ?: "Erro ao salvar")
                }
        }
    }
}

sealed class FormUiState {
    object Loading : FormUiState()
    data class Ready(val responses: Map<String, String>, val isCompleted: Boolean) : FormUiState()
    object Saved : FormUiState()
    data class Error(val message: String) : FormUiState()
}


