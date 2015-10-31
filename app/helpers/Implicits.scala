package helpers

object Implicits {

  implicit class RichDouble(d: Double) {
    def r2str = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toString()
  }


  implicit class RichString(str: String) {
    def % =  str.replace("%", "").toDouble / 100
    def y = str.replace("y", "").toInt
  }
}
