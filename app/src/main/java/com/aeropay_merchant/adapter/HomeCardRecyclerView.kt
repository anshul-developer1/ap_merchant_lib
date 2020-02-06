package com.aeropay_merchant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aeropay_merchant.Model.CreateSyncPayload
import com.aeropay_merchant.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_card_layout.view.*

class HomeCardRecyclerView(var payerName: MutableList<CreateSyncPayload>, val context: Context) : RecyclerView.Adapter<HomeCardRecyclerView.CardViewHolder>() {

    var onItemClick: ((pos: Int, view: View) -> Unit)? = null

    override fun getItemCount(): Int {
        return payerName.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.recycler_card_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder?.payerName?.text = payerName[position].userName.toString()
        Glide.with(context).load(payerName[position].profileImage.toString()).into(holder?.userImage)
    }

    fun setValues(payerDetails: MutableList<CreateSyncPayload>){
        this.payerName = payerDetails
        notifyDataSetChanged()
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
        val userImage = view.userImage
    }
}