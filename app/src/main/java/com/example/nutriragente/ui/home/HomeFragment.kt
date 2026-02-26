package com.example.nutriragente.ui.home

import android.media.MediaCas
import com.example.nutriragente.data.model.Crianca
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nutriragente.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore



class HomeFragment : Fragment(R.layout.fragment_home) {

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid as String
    private val db = FirebaseFirestore.getInstance(FirebaseApp.getInstance())
    private lateinit var recyclerView_todas: RecyclerView
    private lateinit var recyclerView_baixopeso: RecyclerView
    private lateinit var recyclerView_sobrepeso: RecyclerView
    private lateinit var CriancaList : MutableList<Crianca>
    private lateinit var CriancaList_baixopeso : MutableList<Crianca>
    private lateinit var CriancaList_sobrepeso : MutableList<Crianca>
    private lateinit var adapter_todos: CriancaAdapter
    private lateinit var adapter_baixopeso: CriancaAdapter
    private lateinit var adapter_sobrepeso: CriancaAdapter




    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val Addfab = view.findViewById<FloatingActionButton>(R.id.fab_add)
        swipeRefreshLayout = view as SwipeRefreshLayout

        super.onViewCreated(view, savedInstanceState)

        val window = requireActivity().window

        // Configura a status bar
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_toolbar)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Ajusta padding do conteúdo
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        recyclerView_todas = view.findViewById(R.id.recyclerCriancas)
        recyclerView_todas.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_todas.setHasFixedSize(true)

        recyclerView_baixopeso = view.findViewById(R.id.rvBaixopeso)
        recyclerView_baixopeso.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_baixopeso.setHasFixedSize(true)

        recyclerView_sobrepeso = view.findViewById(R.id.rvSobrepeso)
        recyclerView_sobrepeso.layoutManager = LinearLayoutManager(requireContext())
        recyclerView_sobrepeso.setHasFixedSize(true)

        CriancaList = mutableListOf()

        adapter_todos = CriancaAdapter(CriancaList)
        recyclerView_todas.adapter = adapter_todos

        CriancaList_baixopeso = mutableListOf()

        adapter_baixopeso = CriancaAdapter(CriancaList_baixopeso)
        recyclerView_baixopeso.adapter = adapter_baixopeso

        CriancaList_sobrepeso = mutableListOf()

        adapter_sobrepeso = CriancaAdapter(CriancaList_sobrepeso)
        recyclerView_sobrepeso.adapter = adapter_sobrepeso


        EventChangeListener()
        EventChangeListenerBaixoPeso()
        EventChangeListenerSobrepeso()

        swipeRefreshLayout.isRefreshing = false

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            EventChangeListener()
            EventChangeListenerBaixoPeso()
            EventChangeListenerSobrepeso()
            swipeRefreshLayout.isRefreshing = false
        }




        Addfab.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_new_evaluation)
         }
        
    }
    private fun EventChangeListener() {
        db.collection("users").document(userId).collection("crianca")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Trate o erro (ex: mostrar Toast)
                    return@addSnapshotListener
                }

                // Limpa a lista antes de adicionar os novos dados
                CriancaList.clear()

                // Converte o snapshot inteiro para uma Lista de Objetos
                // Isso é mais seguro e evita duplicatas
                value?.toObjects(Crianca::class.java)?.let {
                    CriancaList.addAll(it)
                }

                adapter_todos.notifyDataSetChanged()
            }
    }

    private fun EventChangeListenerBaixoPeso() {
        val categorias = listOf("Magreza", "Magreza Acentuada")
        db.collection("users").document(userId).collection("crianca")
            .whereIn("statusNutricional", categorias)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Trate o erro (ex: mostrar Toast)
                    return@addSnapshotListener
                }

                // Limpa a lista antes de adicionar os novos dados
                CriancaList_baixopeso.clear()

                // Converte o snapshot inteiro para uma Lista de Objetos
                // Isso é mais seguro e evita duplicatas
                value?.toObjects(Crianca::class.java)?.let {
                    CriancaList_baixopeso.addAll(it)
                }

                adapter_baixopeso.notifyDataSetChanged()
            }

    }

    private fun EventChangeListenerSobrepeso() {
        val categorias = listOf("Sobrepeso", "Obesidade", "Obesidade Grave")
        db.collection("users").document(userId).collection("crianca")
            .whereIn("statusNutricional", categorias)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Trate o erro (ex: mostrar Toast)
                    return@addSnapshotListener
                }

                // Limpa a lista antes de adicionar os novos dados
                CriancaList_sobrepeso.clear()

                // Converte o snapshot inteiro para uma Lista de Objetos
                // Isso é mais seguro e evita duplicatas
                value?.toObjects(Crianca::class.java)?.let {
                    CriancaList_sobrepeso.addAll(it)
                }

                adapter_sobrepeso.notifyDataSetChanged()
            }
    }
}




