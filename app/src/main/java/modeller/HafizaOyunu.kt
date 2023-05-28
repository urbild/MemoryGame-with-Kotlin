package modeller

import resimler.DEFAULT_ICONS

class HafizaOyunu (private val tahtaBoyut: TahtaBoyut) {

    val kartlar: List<HafizaKart>
    var dogruCiftler = 0

    private var flipKartSayi = 0
    private var indexTekSecilenKart: Int? = null

    init{
        val resimSecim : List<Int> = DEFAULT_ICONS.shuffled().take(tahtaBoyut.getCiftSayisi())
        val randomResim = (resimSecim + resimSecim).shuffled()
        kartlar = randomResim.map { HafizaKart(it) }
    }

    fun flipKart(position: Int): Boolean {
        flipKartSayi++
        val kart: HafizaKart = kartlar[position]
        var eslemeBul = false
        if(indexTekSecilenKart==null){
            basaAlKartlari()
            indexTekSecilenKart = position
        } else {
            eslemeBul= esMi(indexTekSecilenKart!!, position)
            indexTekSecilenKart = null
        }
        kart.upYuz = !kart.upYuz
        return eslemeBul
    }

    private fun esMi(position1: Int, position2: Int): Boolean {
        if(kartlar[position1].id != kartlar[position2].id){
            return false
        }
        kartlar[position1].eslesme = true
        kartlar[position2].eslesme = true
        dogruCiftler++
        return true
    }

    private fun basaAlKartlari() {
        for (kart in kartlar) {
            if(!kart.eslesme) {
                kart.upYuz = false
            }
        }
    }

    fun kazanmak(): Boolean {
        return dogruCiftler == tahtaBoyut.getCiftSayisi()

    }

    fun kartDonmesi(position: Int): Boolean {
        return kartlar[position].upYuz

    }

    fun getHamleSayi(): Int {
        return flipKartSayi / 2

    }

}