package helpers

object Implicits {

  implicit class RichDouble(d: Double) {
    def r2 = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    def r2str = r2.toString()
  }


  implicit class RichString(str: String) {
    def % =  str.replace("%", "").toDouble / 100
    def y = str.replace("y", "").toInt
  }
}
