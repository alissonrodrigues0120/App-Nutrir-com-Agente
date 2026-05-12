package com.example.nutriragente.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutriragente.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

/**
 * ViewModel unificado de autenticação — substitui LogCasResViewModel.
 *
 * Todas as operações Firebase são executadas no viewModelScope e expostas
 * como eventos one-shot via SharedFlow, eliminando os callbacks (Boolean, String?)
 * que antes acoplavam a lógica de negócio diretamente nos Fragments.
 *
 * Os Fragments observam [event] com repeatOnLifecycle(STARTED) e reagem
 * a Success (navegar) ou Error (exibir Toast) sem saber como a operação ocorreu.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    /** Evento emitido uma única vez após cada operação de autenticação. */
    sealed class AuthEvent {
        data object LoginSuccess    : AuthEvent()
        data object RegisterSuccess : AuthEvent()
        data object ResetSuccess    : AuthEvent()
        data class  Error(val message: String) : AuthEvent()
    }

    private val _event = MutableSharedFlow<AuthEvent>()
    val event: SharedFlow<AuthEvent> = _event.asSharedFlow()

    // ── Operações públicas ──────────────────────────────────────────────────

    fun login(email: String, password: String) = viewModelScope.launch {
        val result = wrapCallback { cb -> userRepository.login(email, password, cb) }
        _event.emit(result.fold({ AuthEvent.LoginSuccess }, { AuthEvent.Error(it.message ?: "Erro ao entrar") }))
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        val result = wrapCallback { cb -> userRepository.register(email, password, cb) }
        _event.emit(result.fold({ AuthEvent.RegisterSuccess }, { AuthEvent.Error(it.message ?: "Erro ao cadastrar") }))
    }

    fun resetPassword(email: String) = viewModelScope.launch {
        val result = wrapCallback { cb -> userRepository.resetPassword(email, cb) }
        _event.emit(result.fold({ AuthEvent.ResetSuccess }, { AuthEvent.Error(it.message ?: "Erro ao enviar e-mail") }))
    }

    fun getCurrentUserId(): String? = userRepository.getCurrentUserId()

    fun signOut() = userRepository.logout()

    // ── Helper: converte (Boolean, String?) callback → Result<Unit> ─────────

    /**
     * Converte o padrão de callback usado por [UserRepository] em uma
     * suspend function que retorna [Result].
     *
     * Isso elimina a necessidade de `kotlinx-coroutines-play-services` e
     * mantém a interoperabilidade com o código de callback já existente.
     */
    private suspend fun wrapCallback(
        block: ((Boolean, String?) -> Unit) -> Unit
    ): Result<Unit> = suspendCancellableCoroutine { cont ->
        block { success, message ->
            if (cont.isActive) {
                if (success) cont.resume(Result.success(Unit))
                else cont.resume(Result.failure(Exception(message ?: "Erro desconhecido")))
            }
        }
    }
}
