package com.example.nutriragente.util

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.example.nutriragente.R
import com.google.android.material.navigation.NavigationView

/**
 * Aplica o modo tela cheia (Edge-to-Edge) e garante que o conteúdo
 * não fique embaixo das barras do sistema (Status Bar e Navigation Bar).
 */
fun Fragment.setupEdgeToEdge(mainView: View) {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, false)
    // 3. ADAPTAÇÃO DINÂMICA: Ajusta o preenchimento (padding) automaticamente
    ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        
        // Adiciona espaço no topo (StatusBar) e na base (NavigationBar)
        v.updatePadding(
            top = systemBars.top,
            bottom = systemBars.bottom,
            left = systemBars.left,
            right = systemBars.right
        )
        insets
    }
}

fun Fragment.setupEdgeToEdgeDrawer(
    contentTargetId: Int,
    navigationViewId: Int
) {
    val window = requireActivity().window
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // 1. Padding no conteúdo principal
    view?.findViewById<View>(contentTargetId)?.let { content ->
        ViewCompat.setOnApplyWindowInsetsListener(content) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                bottom = systemBars.bottom,
                left = systemBars.left,
                right = systemBars.right
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    // 2. NavigationView: aplicar padding no header E ajustar o fundo
    view?.findViewById<NavigationView>(navigationViewId)?.let { navView ->
        // Aplica insets no NavigationView inteiro
        ViewCompat.setOnApplyWindowInsetsListener(navView) { _, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // Aplica padding apenas no topo (status bar)
            // A navigation bar fica por baixo (fundo azul estende)
            navView.setPadding(
                navView.paddingLeft,
                statusBars.top,
                navView.paddingRight,
                0
            )

            WindowInsetsCompat.CONSUMED
        }

        // Ajusta o header especificamente
        navView.post {
            val header = navView.getHeaderView(0)
            header?.let { headerView ->
                ViewCompat.setOnApplyWindowInsetsListener(headerView) { h, insets ->
                    val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                    h.updatePadding(
                        top = statusBars.top + h.resources.getDimensionPixelSize(R.dimen.header_extra_padding),
                        bottom = h.paddingBottom
                    )
                    WindowInsetsCompat.CONSUMED
                }
            }
        }
    }
}
