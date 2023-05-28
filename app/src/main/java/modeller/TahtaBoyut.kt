package modeller

enum class TahtaBoyut(val kartSayi: Int){
    KOLAY(8),
    ORTA(18),
    ZOR(24);

    fun getWidth(): Int {
        return when (this){
            KOLAY -> 2
            ORTA -> 3
            ZOR -> 4
        }
    }

    fun getHeight(): Int {
        return kartSayi / getWidth()
    }

    fun getCiftSayisi(): Int {
        return kartSayi / 2
    }
}