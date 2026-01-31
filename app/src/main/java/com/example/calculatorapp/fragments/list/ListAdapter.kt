package com.example.calculatorapp.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatorapp.data.model.Calculation
import com.example.calculatorapp.databinding.CustomRowBinding

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var calculationsList = emptyList<Calculation>()

    class MyViewHolder(private val binding: CustomRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(calculation: Calculation) {
            binding.textViewExpression.text = calculation.expression
            binding.textViewResult.text = calculation.result
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return calculationsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = calculationsList[position]
        holder.bind(currentItem)
    }

    fun setData(calculations: List<Calculation>){
        this.calculationsList = calculations
        notifyDataSetChanged()
    }
}