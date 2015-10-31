package data

import com.mongodb.casbah.Imports._
import scala.collection.JavaConverters._
import scala.collection.concurrent.TrieMap
import scala.collection.mutable.ArrayBuffer


object TestRepo {
  val tradesRepo = new InMemoryDataRepository(ArrayBuffer())
  val counterpartiesRepo = new InMemoryDataRepository(ArrayBuffer())
}

object MongoRepo {

  val connection = new MongoConnection("mongodb://heroku_tvczrsc9:128m3n0bkg1qtuta2uv4p6ga7h@ds045464.mongolab.com:45464/heroku_tvczrsc9", "heroku_tvczrsc9")
  val tradesRepo = new MongoDataRepository(connection, "trades")
  val counterpartiesRepo = new MongoDataRepository(connection, "counterparties")

  def main(a: Array[String]) {
    val dataRepo = tradesRepo

    println(counterpartiesRepo.size)
//    dataRepo.remove(Map())
//    (0 to 10).foreach { i =>
//      dataRepo.insert(Map("Notional" -> "100", "Counterparty" -> "City Bank", "CounterpartyRate" -> "AAA+", "Tenor" -> "1y"))
//    }

    println(dataRepo.size)
  }

}
