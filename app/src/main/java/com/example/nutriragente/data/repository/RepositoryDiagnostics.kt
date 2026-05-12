package com.example.nutriragente.data.repository

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

object RepositoryDiagnostics {
    fun logError(
        repository: String,
        operation: String,
        throwable: Throwable,
        context: Map<String, String> = emptyMap()
    ) {
        val contextString = context.entries.joinToString(",") { "${it.key}=${it.value}" }
        Log.e("RepoError", "repo=$repository op=$operation context={$contextString}", throwable)

        FirebaseCrashlytics.getInstance().apply {
            setCustomKey("repository", repository)
            setCustomKey("operation", operation)
            setCustomKey("context", contextString)
            recordException(throwable)
        }
    }
}
