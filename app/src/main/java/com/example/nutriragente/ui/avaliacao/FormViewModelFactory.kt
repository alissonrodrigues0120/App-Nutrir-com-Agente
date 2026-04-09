package com.example.nutriragente.ui.avaliacao

import com.example.nutriragente.data.model.FormType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.time.LocalDate

class FormViewModelFactory(
    private val userId: String,
    private val childId: String,
    private val formType: FormType,
    private val birthDate: LocalDate
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(
                userId = userId,
                childId = childId,
                formType = formType,
                birthDate = birthDate
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
