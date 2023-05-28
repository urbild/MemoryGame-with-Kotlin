package com.example.serbestcagrisim

import android.animation.ArgbEvaluator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.google.android.material.snackbar.Snackbar
import modeller.HafizaOyunu
import modeller.TahtaBoyut

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot:ConstraintLayout
    private lateinit var rvTahta: RecyclerView
    private lateinit var tvHamleSayisi: TextView
    private lateinit var tvCiftSayisi: TextView

    private lateinit var hafizaOyunu: HafizaOyunu
    private lateinit var adapter: HafizaTahtaAdapter
    private var tahtaBoyut: TahtaBoyut = TahtaBoyut.KOLAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        clRoot = findViewById(R.id.clRoot)
        rvTahta = findViewById(R.id.rvTahta)
        tvCiftSayisi = findViewById(R.id.tvCiftSayisi)
        tvHamleSayisi = findViewById(R.id.tvHamleSayisi)

        setupBoard()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.mi_yenile ->{
                if (hafizaOyunu.getHamleSayi() > 0 && !hafizaOyunu.kazanmak()){
                    showAlertDialog("Mevcut Oyunu Sonlandır?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
            }
            R.id.mi_yeniboyut -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun showNewSizeDialog() {
        val tahtaboyutuView = LayoutInflater.from(this).inflate(R.layout.dialog_tahtaboyutu,null)
        val radioGroupSize = tahtaboyutuView.findViewById<RadioGroup>(R.id.radioGroup)
        when (tahtaBoyut){
            TahtaBoyut.KOLAY -> radioGroupSize.check(R.id.rbKolay)
            TahtaBoyut.ORTA -> radioGroupSize.check(R.id.rbOrta)
            TahtaBoyut.ZOR -> radioGroupSize.check(R.id.rbZor)
        }
        showAlertDialog("Yeni boyut seç", tahtaboyutuView, View.OnClickListener {
            tahtaBoyut = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbKolay -> TahtaBoyut.KOLAY
                R.id.rbOrta -> TahtaBoyut.ORTA
                else -> TahtaBoyut.ZOR
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Hayır", null)
            .setPositiveButton("Evet") {_,_ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        when (tahtaBoyut){
            TahtaBoyut.KOLAY -> {
                tvHamleSayisi.text = "Kolay: 4 x 2"
                tvCiftSayisi.text = "Eşler: 0 / 4"
            }
            TahtaBoyut.ORTA -> {
                tvHamleSayisi.text = "Kolay: 6 x 3"
                tvCiftSayisi.text = "Eşler: 0 / 9"
            }
            TahtaBoyut.ZOR -> {
                tvHamleSayisi.text = "Kolay: 6 x 4"
                tvCiftSayisi.text = "Eşler: 0 / 12"
            }
        }
        tvCiftSayisi.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        hafizaOyunu = HafizaOyunu(tahtaBoyut)
        adapter = HafizaTahtaAdapter(this,tahtaBoyut,hafizaOyunu.kartlar, object: HafizaTahtaAdapter.KartTik{
            override fun kartaTiklandi(position: Int) {
                Log.i(TAG, "Karta TIK $position")
                updateGameWithFlip(position)
            }
        })
        rvTahta.adapter = adapter
        rvTahta.setHasFixedSize(true)
        rvTahta.layoutManager = GridLayoutManager(this, tahtaBoyut.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {
        //Hata Kontrol
        if(hafizaOyunu.kazanmak()) {
            Snackbar.make(clRoot, "ZATEN KAZANDIN. SAKİN OL ŞAMPİYON", Snackbar.LENGTH_LONG).show()
            return
        }
        if(hafizaOyunu.kartDonmesi(position)) {
            Snackbar.make(clRoot, "GEÇERSİZ HAMLE", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (hafizaOyunu.flipKart(position)) {
            Log.i(TAG, "Eşledin!! Bulunan çift sayısı: ${hafizaOyunu.dogruCiftler}")
            val color = ArgbEvaluator().evaluate(
                hafizaOyunu.dogruCiftler.toFloat() / tahtaBoyut.getCiftSayisi(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)
            ) as Int
            tvCiftSayisi.setTextColor(color)
            tvCiftSayisi.text = "Eşler: ${hafizaOyunu.dogruCiftler} / ${tahtaBoyut.getCiftSayisi()}"
            if (hafizaOyunu.kazanmak()) {
                Snackbar.make(clRoot, "TEBRİKLER!! BAŞARDIN!!", Snackbar.LENGTH_LONG).show()
                CommonConfetti.rainingConfetti(clRoot, intArrayOf(Color.YELLOW,Color.GREEN,Color.MAGENTA)).oneShot()
            }
        }

        tvHamleSayisi.text = "Hamleler: ${hafizaOyunu.getHamleSayi()}"
        adapter.notifyDataSetChanged()
    }
}