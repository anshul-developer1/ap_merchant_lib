package com.aeropay_merchant.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.R
import com.earthling.atminput.ATMEditText
import com.earthling.atminput.Currency
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.recycler_card_layout.view.*

class HomeCardRecyclerView(val payerName : ArrayList<String>, val context: Context) : RecyclerView.Adapter<HomeCardRecyclerView.CardViewHolder>() {

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null

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

    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) , View.OnClickListener {
        override fun onClick(v: View?) {
            if (v != null) {
                onItemClick?.invoke(adapterPosition, v)
            }
        }

        init {
            view.setOnClickListener(this)
        }
        val payerName = view.userName
    }
}