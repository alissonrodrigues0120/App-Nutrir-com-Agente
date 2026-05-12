package com.example.nutriragente.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Configura o Firestore com cache persistente ilimitado.
     *
     * Por que aqui e não no LoginFragment:
     * - As FirestoreSettings DEVEM ser definidas antes de qualquer operação
     *   Firestore. Se configuradas apenas no Login, um ACS que abra o app
     *   já autenticado (tela de biometria, por exemplo) vai operar sem cache
     *   até a tela de login ser visitada — podendo perder dados offline.
     * - O Hilt inicializa este módulo junto com a Application (SingletonComponent),
     *   garantindo que o cache esteja ativo desde o primeiro frame.
     *
     * CACHE_SIZE_UNLIMITED: ideal para ACS em campo com conectividade
     * intermitente — o app nunca descarta dados locais por falta de espaço.
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build()
                )
                .build()
        }
    }
}
