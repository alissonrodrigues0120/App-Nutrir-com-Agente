package com.example.nutriragente

import android.app.Application
import com.example.nutriragente.data.repository.LmsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Ponto de entrada da aplicação.
 *
 * A injeção de LmsRepository aqui força o Hilt a criar o singleton
 * imediatamente no onCreate() — que por sua vez dispara o carregamento
 * do JSON lms_tables.json em Dispatchers.IO.
 *
 * Resultado: quando o ACS chega na tela de avaliação (segundos depois),
 * as tabelas LMS já estão na memória e o cálculo de Z-Score é instantâneo.
 */
@HiltAndroidApp
class NutrirApplication : Application() {

    @Inject
    lateinit var lmsRepository: LmsRepository

    override fun onCreate() {
        super.onCreate()
        // A injeção acima já disparou o carregamento em background.
        // Nenhuma chamada adicional necessária.
    }
}
