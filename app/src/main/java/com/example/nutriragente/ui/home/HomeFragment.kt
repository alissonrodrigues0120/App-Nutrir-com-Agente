package com.example.nutriragente.ui.home

import com.example.nutriragente.data.database.entities.Crianca
import androidx.lifecycle.observe
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.MainActivity
import com.example.nutriragente.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HomeFragment : Fragment() {

    private val vm: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        vm.criancas.observe(viewLifecycleOwner) { lista : List <Crianca>  ->



            adapterTodas.submitList(lista)

        // Filtra com tipos explícitos
            val listaParaImc = lista.filter { crianca : Crianca ->
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
            txtTotal.text = "${lista.size} crianças"
            txtIdeal.text = pesoAdequado.size.toString()
            txtBaixo.text = baixoPeso.size.toString()
            txtSobre.text = sobrepeso.size.toString()

            adapterBaixoPeso.submitList(baixoPeso)
            adapterSobrepeso.submitList(sobrepeso)

            
        }

        val Addfab = view.findViewById<FloatingActionButton>(R.id.fab_add)

        Addfab.setOnClickListener{
            findNavController().navigate(R.id.action_home_to_new_evaluation)
         }

        
    }
}
