package com.example.calculatorapp.fragments.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatorapp.R
import com.example.calculatorapp.data.model.Calculation
import kotlinx.android.synthetic.main.custom_row.view.*

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var calculationsList = emptyList<Calculation>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int {
        return calculationsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = calculationsList[position]
        holder.itemView.textViewExpression.text = currentItem.expression
        holder.itemView.textViewResult.text = currentItem.result
    }

    fun setData(calculations: List<Calculation>){
        this.calculationsList = calculations
        notifyDataSetChanged()
    }
}