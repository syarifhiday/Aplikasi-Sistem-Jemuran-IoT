package com.example.sistemjemuran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter (private val historyList: ArrayList<HistoryModel>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_of_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentHistory = historyList[position]
        holder.dataHistory.text = currentHistory.data
        holder.waktuHistoy.text = currentHistory.time
        holder.tempHistory.text = "Suhu: " + currentHistory.temp + "°C"
        holder.cahayaHistory.text = "Cahaya: " + currentHistory.cahaya
        holder.basahHistory.text = "Basah: " + currentHistory.basah
        if (currentHistory.data?.contains("hujan") == true){
            holder.gambarCuaca.setImageResource(R.drawable.day_rain)
        }else{
            holder.gambarCuaca.setImageResource(R.drawable.day_clear)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val dataHistory : TextView = itemView.findViewById(R.id.dataHistory)
        val waktuHistoy : TextView = itemView.findViewById(R.id.waktuHistory)
        val gambarCuaca : ImageView = itemView.findViewById(R.id.gambarCuaca)
        val tempHistory : TextView = itemView.findViewById(R.id.tempHistory)
        val cahayaHistory : TextView = itemView.findViewById(R.id.cahayaHistory)
        val basahHistory : TextView = itemView.findViewById(R.id.basahHistory)
    }

}