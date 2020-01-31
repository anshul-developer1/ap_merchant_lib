package com.aeropay_merchant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.R
import kotlinx.android.synthetic.main.recycler_card_layout.view.*

class HomeCardRecyclerView(val payerName : ArrayList<String>, val context: Context) : RecyclerView.Adapter<HomeCardRecyclerView.CardViewHolder>() {

    override fun getItemCount(): Int {
        return payerName.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.recycler_card_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder?.payerName?.text = payerName.get(position)
        holder?.userCardLayout.setOnClickListener(View.OnClickListener {

        })
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val payerName = view.userName
        var userCardLayout = view.userCardLayout
    }
}