package com.example.calculatorapp.fragments.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.calculatorapp.data.viewmodel.CalculationViewModel
import com.example.calculatorapp.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private lateinit var mCalculationViewModel: CalculationViewModel
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        // RecyclerView
        val adapter = ListAdapter()
        val recyclerView = binding.recyclerViewCalcs
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // UserViewModel
        mCalculationViewModel = ViewModelProvider(this)[CalculationViewModel::class.java]
        mCalculationViewModel.readAllData.observe(viewLifecycleOwner, Observer { calculation ->
            adapter.setData(calculation)
         })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}