package com.example.serbestcagrisim

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import modeller.HafizaKart
import modeller.TahtaBoyut
import kotlin.math.min

class HafizaTahtaAdapter(
    private val context: Context,
    private val tahtaBoyut: TahtaBoyut,
    private val kartlar: List<HafizaKart>,
    private val kartTik: KartTik
) :
    RecyclerView.Adapter<HafizaTahtaAdapter.ViewHolder>() {

    companion object{
      private const val MARGIN_SIZE = 10
      private const val TAG = "HafizaTahtaAdapter"
    }

    interface KartTik {
        fun kartaTiklandi(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth : Int = parent.width / tahtaBoyut.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight : Int = parent.height / tahtaBoyut.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength : Int = min(cardWidth, cardHeight)
        val view: View = LayoutInflater.from(context).inflate(R.layout.hafiza_kart, parent, false)
        val layoutParams: ViewGroup.MarginLayoutParams = view.findViewById<CardView>(R.id.cvHafizaKart).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun getItemCount() = tahtaBoyut.kartSayi


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val butonResim = itemView.findViewById<ImageButton>(R.id.butonResim)
        fun bind(position: Int) {
            val hafizaKart: HafizaKart  = kartlar[position]
            butonResim.setImageResource(if (hafizaKart.upYuz) hafizaKart.id else R.drawable.agac)

            butonResim.alpha = if (hafizaKart.eslesme) .4f else 1.0f
            val colorStateList: ColorStateList? = if(hafizaKart.eslesme) ContextCompat.getColorStateList(context, R.color.teal_700) else null
            ViewCompat.setBackgroundTintList(butonResim, colorStateList)


            butonResim.setOnClickListener{
                Log.i(TAG, "$position konumuna tıklandı")
                kartTik.kartaTiklandi(position)
            }

        }
    }
}
