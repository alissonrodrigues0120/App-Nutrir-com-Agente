package com.example.nutriragente.ui.home

import com.example.nutriragente.data.database.entities.Crianca
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nutriragente.R
import com.example.nutriragente.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

    private val vm: HomeViewModel by viewModels()
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


        ViewCompat.setOnApplyWindowInsetsListener(Addfab) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply the insets as a margin to the view. This solution sets
            // only the bottom, left, and right dimensions, but you can apply whichever
            // insets are appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            WindowInsetsCompat.CONSUMED
        }

        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        binding.recyclerCriancas.adapter = CriancaAdapter()

        swipeRefreshLayout.setOnRefreshListener {
            vm.reloadData()
        }

        GraphHistory.initialize(requireContext())

        // --- TEXTOS RESUMO ---
        val txtTotal = view.findViewById<TextView>(R.id.txtTotalCriancas)
        val txtIdeal = view.findViewById<TextView>(R.id.txtQtdIdeal)
        val txtBaixo = view.findViewById<TextView>(R.id.txtQtdBaixoPeso)
        val txtSobre = view.findViewById<TextView>(R.id.txtQtdSobrepeso)

        // --- RECYCLER TODAS ---
        val recyclerTodas = view.findViewById<RecyclerView>(R.id.recyclerCriancas)
        val adapterTodas = CriancaAdapter()
        recyclerTodas.adapter = adapterTodas

        // --- RECYCLER BAIXO PESO ---
        val recyclerBaixoPeso = view.findViewById<RecyclerView>(R.id.rvBaixopeso)
        val adapterBaixoPeso = CriancaAdapter()
        recyclerBaixoPeso.adapter = adapterBaixoPeso

        // --- RECYCLER SOBREPESO ---
        val recyclerSobrepeso = view.findViewById<RecyclerView>(R.id.rvSobrepeso)
        val adapterSobrepeso = CriancaAdapter()
        recyclerSobrepeso.adapter = adapterSobrepeso




        vm.criancas.observe(viewLifecycleOwner) { list : List <Crianca>  ->

            swipeRefreshLayout.isRefreshing = false

            adapterTodas.submitList(list)

        // Filtra com tipos explícitos
            val listaParaImc = list.filter { crianca : Crianca ->
                crianca.idadeMeses >= 30
            }

            val pesoAdequado = listaParaImc.filter { crianca : Crianca ->
                crianca.statusNutricional == "Peso Adequado"
            }
            val baixoPeso = listaParaImc.filter { crianca : Crianca ->
                crianca.statusNutricional == "Magreza" ||
                crianca.statusNutricional == "Magreza Acentuada"
            }
            val sobrepeso = listaParaImc.filter { crianca : Crianca ->
                crianca.statusNutricional == "Sobrepeso" ||
                crianca.statusNutricional.startsWith("Obesidade")
            }

            // Atualiza UI
            txtTotal.text = "${list.size} crianças"
            txtIdeal.text = pesoAdequado.size.toString()
            txtBaixo.text = baixoPeso.size.toString()
            txtSobre.text = sobrepeso.size.toString()

            adapterBaixoPeso.submitList(baixoPeso)
            adapterSobrepeso.submitList(sobrepeso)

            
        }



        Addfab.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_new_evaluation)
         }
        
    }
}
