package com.example.nutriragente.startup

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.FirebaseApp

/**
 * Inicializa o Firebase via App Startup em vez do ContentProvider padrão.
 *
 * O FirebaseInitProvider original roda na main thread durante o processo
 * de ContentProvider attach (antes mesmo do Application.onCreate), o que
 * contribui para Davey frames no cold start. Ao usar o InitializationProvider
 * do App Startup, a inicialização ainda ocorre cedo, mas consolidada em um
 * único ContentProvider — reduzindo o overhead de múltiplos providers.
 *
 * Registro no AndroidManifest.xml:
 *   - FirebaseInitProvider é removido com tools:node="remove"
 *   - InitializationProvider recebe <meta-data> apontando para esta classe
 */
class FirebaseInitializer : Initializer<FirebaseApp> {

    override fun create(context: Context): FirebaseApp {
        // initializeApp retorna null se o [DEFAULT] app já foi criado;
        // getInstance() garante que sempre retornamos uma instância válida.
        return FirebaseApp.initializeApp(context) ?: FirebaseApp.getInstance()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
