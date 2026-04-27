package com.example.nutriragente.ui.home

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nutriragente.R
import com.example.nutriragente.data.model.Crianca
import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.util.setupEdgeToEdgeDrawer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    // Auth permanece injetado para obter userId (sign out, navegação)
    @Inject lateinit var auth: FirebaseAuth

    // ViewModel injetado pelo Hilt — gerencia todo o estado e os listeners Firestore
    private val viewModel: HomeViewModel by viewModels()

    private var userId: String? = null

    // private lateinit var recyclerView_todas: RecyclerView  // lista completa comentada
    private lateinit var recyclerView_baixopeso: RecyclerView
    private lateinit var recyclerView_sobrepeso: RecyclerView
    private lateinit var recyclerView_pesoideal: RecyclerView
    // private lateinit var CriancaList: MutableList<Crianca>  // lista completa comentada
    private lateinit var CriancaList_baixopeso: MutableList<Crianca>
    private lateinit var CriancaList_sobrepeso: MutableList<Crianca>
    private lateinit var CriancaList_pesoideal: MutableList<Crianca>
    // private lateinit var adapter_todos: CriancaAdapter  // lista completa comentada
    private lateinit var adapter_baixopeso: CriancaAdapter
    private lateinit var adapter_sobrepeso: CriancaAdapter
    private lateinit var adapter_pesoideal: CriancaAdapter

    private lateinit var qtdpesoideal: TextView
    private lateinit var qtdtotal: TextView
    private lateinit var qtdbaixo: TextView
    private lateinit var qtdsobrepeso: TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window
        userId = auth.currentUser?.uid
        if (userId == null) {
            findNavController().navigate(R.id.action_home_to_login)
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowInsetsController.isAppearanceLightNavigationBars = true
        }

        setupEdgeToEdgeDrawer(
            contentTargetId = R.id.homeFragment,
            navigationViewId = R.id.nav_view
        )

        val addfab = view.findViewById<FloatingActionButton>(R.id.fab_add)
        swipeRefreshLayout = view.findViewById(R.id.homeFragment)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = view.findViewById<NavigationView>(R.id.nav_view)

        qtdtotal    = view.findViewById(R.id.txtTotalCriancas)
        qtdpesoideal = view.findViewById(R.id.txtQtdIdeal)
        qtdbaixo    = view.findViewById(R.id.txtQtdBaixoPeso)
        qtdsobrepeso = view.findViewById(R.id.txtQtdSobrepeso)

        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }

        val headerView = navView.getHeaderView(0)
        headerView.findViewById<Button>(R.id.sobreButton).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_about)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        headerView.findViewById<Button>(R.id.saberMaisButton).setOnClickListener {
            findNavController().navigate(R.id.action_home_to_sabermais)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        headerView.findViewById<Button>(R.id.btn_sign_out).setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_home_to_login)
        }

        setupRecyclerViews(view)

        // Observar o ViewModel — repeatOnLifecycle cancela as coroutines quando
        // o fragment sai de STARTED e as reinicia quando volta. Sem onStop manual.
        observeViewModel()

        // O SwipeRefresh é visual apenas: o ViewModel já escuta mudanças em tempo real
        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        addfab.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_new_evaluation)
        }
    }

    /**
     * Conecta a UI aos StateFlows do ViewModel.
     *
     * repeatOnLifecycle(STARTED): as coroutines são canceladas automaticamente
     * quando o fragment vai para background (ex: outra tela) e reiniciadas ao
     * voltar — sem necessidade de onStop/onDestroyView manuais.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // lista completa comentada
                // launch {
                //     viewModel.todasCriancas.collect { lista ->
                //         CriancaList.clear()
                //         CriancaList.addAll(lista)
                //         adapter_todos.notifyDataSetChanged()
                //     }
                // }
                launch {
                    viewModel.baixoPeso.collect { lista ->
                        CriancaList_baixopeso.clear()
                        CriancaList_baixopeso.addAll(lista)
                        adapter_baixopeso.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.sobrepeso.collect { lista ->
                        CriancaList_sobrepeso.clear()
                        CriancaList_sobrepeso.addAll(lista)
                        adapter_sobrepeso.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.pesoIdeal.collect { lista ->
                        CriancaList_pesoideal.clear()
                        CriancaList_pesoideal.addAll(lista)
                        adapter_pesoideal.notifyDataSetChanged()
                    }
                }
                launch {
                    viewModel.contadores.collect { c ->
                        qtdtotal.text     = "${c.total} crianças"
                        qtdpesoideal.text = "${c.pesoIdeal} crianças"
                        qtdbaixo.text     = "${c.baixoPeso} crianças"
                        qtdsobrepeso.text = "${c.sobrepeso} crianças"
                    }
                }
            }
        }
    }

    private fun setupRecyclerViews(view: View) {
        // lista completa comentada
        // recyclerView_todas = view.findViewById(R.id.recyclerCriancas)
        // recyclerView_todas.layoutManager = LinearLayoutManager(requireContext())

        recyclerView_baixopeso = view.findViewById(R.id.rvBaixopeso)
        recyclerView_baixopeso.layoutManager = LinearLayoutManager(requireContext())

        recyclerView_sobrepeso = view.findViewById(R.id.rvSobrepeso)
        recyclerView_sobrepeso.layoutManager = LinearLayoutManager(requireContext())

        recyclerView_pesoideal = view.findViewById(R.id.rvPesoideal)
        recyclerView_pesoideal.layoutManager = LinearLayoutManager(requireContext())

        val onCriancaClick: (Crianca) -> Unit = { crianca ->
            val formType = when {
                crianca.idadeMeses < 6    -> FormType.UNDER_6M.name
                crianca.idadeMeses in 6..23 -> FormType.SIX_TO_23M.name
                else                      -> FormType.TWO_YEARS_PLUS.name
            }
            val bundle = Bundle().apply {
                putSerializable("CRIANCA", crianca)
                putString("USER_ID", userId)
                putString("CHILD_ID", crianca.id)
                putString("FORM_TYPE", formType)
            }
            findNavController().navigate(R.id.action_home_to_resultados, bundle)
        }

        // lista completa comentada
        // CriancaList = mutableListOf()
        // adapter_todos = CriancaAdapter(CriancaList, onCriancaClick)
        // recyclerView_todas.adapter = adapter_todos

        CriancaList_baixopeso = mutableListOf()
        adapter_baixopeso = CriancaAdapter(CriancaList_baixopeso, onCriancaClick)
        recyclerView_baixopeso.adapter = adapter_baixopeso

        CriancaList_sobrepeso = mutableListOf()
        adapter_sobrepeso = CriancaAdapter(CriancaList_sobrepeso, onCriancaClick)
        recyclerView_sobrepeso.adapter = adapter_sobrepeso

        CriancaList_pesoideal = mutableListOf()
        adapter_pesoideal = CriancaAdapter(CriancaList_pesoideal, onCriancaClick)
        recyclerView_pesoideal.adapter = adapter_pesoideal
    }

    // onStop e onDestroyView com cleanup manual de listeners foram REMOVIDOS.
    // repeatOnLifecycle(STARTED) cuida do ciclo de vida automaticamente.
}
