package models

/**
 * Created by Vladimir on 30.10.2015.
 */
case class CounterpartyData(counterparty: String, rating: String)

case class TradeData(currency: String, notional: Int, tenor: String, maturityDate: String, counterparty: String, fixRate: String, floatingRate: String)


object CaseHelper {
  def ccToMap(cc: AnyRef) =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f.setAccessible(true)
      a + (f.getName -> f.get(cc))
    }
}