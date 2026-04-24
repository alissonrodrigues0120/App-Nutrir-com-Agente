package com.example.nutriragente.ui.home

import android.graphics.Color
import android.os.Build
import com.example.nutriragente.data.model.Crianca
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nutriragente.R
import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.util.setupEdgeToEdgeDrawer
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var db: FirebaseFirestore
    private var userId: String? = null
    private val listeners = mutableListOf<ListenerRegistration>()
    
    private lateinit var recyclerView_todas: RecyclerView
    private lateinit var recyclerView_baixopeso: RecyclerView
    private lateinit var recyclerView_sobrepeso: RecyclerView
    private lateinit var recyclerView_pesoideal: RecyclerView
    private lateinit var CriancaList : MutableList<Crianca>
    private lateinit var CriancaList_baixopeso : MutableList<Crianca>
    private lateinit var CriancaList_sobrepeso : MutableList<Crianca>
    private lateinit var CriancaList_pesoideal : MutableList<Crianca>
    private lateinit var adapter_todos: CriancaAdapter
    private lateinit var adapter_baixopeso: CriancaAdapter
    private lateinit var adapter_sobrepeso: CriancaAdapter
    private lateinit var adapter_pesoideal: CriancaAdapter

    private lateinit var qtdpesoideal: TextView
    private lateinit var qtdtotal : TextView
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

        // Opcional: deixar barras transparentes para efeito "imersivo"
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        // 3. CONTROLAR A COR DOS ÍCONES ← ESSENCIAL!
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Status bar: ícones ESCUROS (para contraste sobre fundo claro)
        windowInsetsController.isAppearanceLightStatusBars = true

        // Navigation bar: ícones ESCUROS (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            windowInsetsController.isAppearanceLightNavigationBars = true
        }
        setupEdgeToEdgeDrawer(
            contentTargetId = R.id.homeFragment,  // ← SwipeRefreshLayout
            navigationViewId = R.id.nav_view
        )

        val Addfab = view.findViewById<FloatingActionButton>(R.id.fab_add)
        swipeRefreshLayout = view.findViewById(R.id.homeFragment)
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = view.findViewById<NavigationView>(R.id.nav_view)

        qtdtotal = view.findViewById<TextView>(R.id.txtTotalCriancas)
        qtdpesoideal = view.findViewById<TextView>(R.id.txtQtdIdeal)
        qtdbaixo = view.findViewById<TextView>(R.id.txtQtdBaixoPeso)
        qtdsobrepeso = view.findViewById<TextView>(R.id.txtQtdSobrepeso)

        toolbar.setNavigationOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val headerView = navView.getHeaderView(0)
        val btnSobre = headerView.findViewById<Button>(R.id.sobreButton)
        val btnSaberMais = headerView.findViewById<Button>(R.id.saberMaisButton)
        val btnSignOut = headerView.findViewById<Button>(R.id.btn_sign_out)

        btnSobre.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_about)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        btnSaberMais.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_sabermais)
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        btnSignOut.setOnClickListener {
            auth.signOut()
            // CORREÇÃO: Usa a action da Home para o Login que já limpa a pilha
            findNavController().navigate(R.id.action_home_to_login)
        }

        setupRecyclerViews(view)
        startListeners()
        EventChangeListenerNumbers()

        swipeRefreshLayout.isRefreshing = false
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        Addfab.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_new_evaluation)
         }
    }

    private fun setupRecyclerViews(view: View) {
        recyclerView_todas = view.findViewById(R.id.recyclerCriancas)
        recyclerView_todas.layoutManager = LinearLayoutManager(requireContext())
        
        recyclerView_baixopeso = view.findViewById(R.id.rvBaixopeso)
        recyclerView_baixopeso.layoutManager = LinearLayoutManager(requireContext())
        
        recyclerView_sobrepeso = view.findViewById(R.id.rvSobrepeso)
        recyclerView_sobrepeso.layoutManager = LinearLayoutManager(requireContext())
        
        recyclerView_pesoideal = view.findViewById(R.id.rvPesoideal)
        recyclerView_pesoideal.layoutManager = LinearLayoutManager(requireContext())

        val onCriancaClick: (Crianca) -> Unit = { crianca ->
            val formType = when {
                crianca.idadeMeses < 6 -> FormType.UNDER_6M.name
                crianca.idadeMeses in 6..23 -> FormType.SIX_TO_23M.name
                else -> FormType.TWO_YEARS_PLUS.name
            }

            val bundle = Bundle().apply {
                putSerializable("CRIANCA", crianca)
                putString("USER_ID", userId)
                putString("CHILD_ID", crianca.id)
                putString("FORM_TYPE", formType)
            }
            findNavController().navigate(R.id.action_home_to_resultados, bundle)
        }

        CriancaList = mutableListOf()
        adapter_todos = CriancaAdapter(CriancaList, onCriancaClick)
        recyclerView_todas.adapter = adapter_todos

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

    private fun refreshData() {
        swipeRefreshLayout.isRefreshing = true
        startListeners()
        EventChangeListenerNumbers()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun startListeners() {
        stopListeners()
        EventChangeListener()
        EventChangeListenerBaixoPeso()
        EventChangeListenerSobrepeso()
        EventChangeListenerPesoideal()
    }

    private fun stopListeners() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    private fun EventChangeListener() {
        val currentUserId = userId ?: return
        val registration = db.collection("users").document(currentUserId).collection("crianca")
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                CriancaList.clear()
                value?.toObjects(Crianca::class.java)?.let { CriancaList.addAll(it) }
                adapter_todos.notifyDataSetChanged()
            }
        listeners.add(registration)
    }

    private fun EventChangeListenerBaixoPeso() {
        val categorias = listOf("Magreza", "Magreza Acentuada")
        val currentUserId = userId ?: return
        val registration = db.collection("users").document(currentUserId).collection("crianca")
            .whereIn("statusNutricional", categorias)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                CriancaList_baixopeso.clear()
                value?.toObjects(Crianca::class.java)?.let { CriancaList_baixopeso.addAll(it) }
                adapter_baixopeso.notifyDataSetChanged()
            }
        listeners.add(registration)
    }

    private fun EventChangeListenerSobrepeso() {
        val categorias = listOf("Sobrepeso", "Obesidade", "Obesidade Grave")
        val currentUserId = userId ?: return
        val registration = db.collection("users").document(currentUserId).collection("crianca")
            .whereIn("statusNutricional", categorias)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                CriancaList_sobrepeso.clear()
                value?.toObjects(Crianca::class.java)?.let { CriancaList_sobrepeso.addAll(it) }
                adapter_sobrepeso.notifyDataSetChanged()
            }
        listeners.add(registration)
    }

    private fun EventChangeListenerPesoideal() {
        val categorias = listOf("Peso Ideal")
        val currentUserId = userId ?: return
        val registration = db.collection("users").document(currentUserId).collection("crianca")
            .whereIn("statusNutricional", categorias)
            .addSnapshotListener { value, error ->
                if (error != null) return@addSnapshotListener
                CriancaList_pesoideal.clear()
                value?.toObjects(Crianca::class.java)?.let { CriancaList_pesoideal.addAll(it) }
                adapter_pesoideal.notifyDataSetChanged()
            }
        listeners.add(registration)
    }

    private fun EventChangeListenerNumbers(){
        val categorias_pesoideal = listOf("Peso Ideal")
        val categorias_baixopeso = listOf("Magreza", "Magreza Acentuada")
        val categorias_sobrepeso = listOf("Sobrepeso", "Obesidade", "Obesidade Grave")

        val currentUserId = userId ?: return
        val criancas = db.collection("users").document(currentUserId).collection("crianca")
        
        criancas.whereIn("statusNutricional", categorias_pesoideal).count()
            .get(AggregateSource.SERVER).addOnSuccessListener {
                qtdpesoideal.text = "${it.count} crianças"
            }

        criancas.whereIn("statusNutricional", categorias_baixopeso).count()
            .get(AggregateSource.SERVER).addOnSuccessListener {
                qtdbaixo.text = "${it.count} crianças"
            }

        criancas.whereIn("statusNutricional", categorias_sobrepeso).count()
            .get(AggregateSource.SERVER).addOnSuccessListener {
                qtdsobrepeso.text = "${it.count} crianças"
            }

        criancas.count().get(AggregateSource.SERVER).addOnSuccessListener {
            qtdtotal.text = "${it.count} crianças"
        }
    }

    override fun onStop() {
        super.onStop()
        stopListeners()
    }

    override fun onDestroyView() {
        stopListeners()
        super.onDestroyView()
    }
}
