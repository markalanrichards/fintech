package data

import models.{CaseHelper, TradeData}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random


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

    //println(counterpartiesRepo.size)


    def randomCC = {
      Math.abs(new Random().nextInt()) % 5 match {
        case 0 => "City Bank UK"
        case 1 => "Barclays"
        case 2 => "Bank Of America"
        case 3 => "UBS"
        case 4 => "Lloyds Bank"
      }
    }

    def randomCurr = {
      Math.abs(new Random().nextInt()) % 5 match {
        case 0 => "USD"
        case 1 => "GBP"
        case 2 => "EUR"
        case 3 => "CAD"
        case 4 => "NOK"
      }
    }

    def randomNotional = {
      (if (new Random().nextBoolean()) 1 else -1) *
        ( Math.abs(new Random().nextInt(10)) % 5 match {
        case 0 | 1 => 1
        case 2 => 3
        case 3 => 5
        case 4 => 10
      })
    }

    def randomPercentage = {
      Math.abs(new Random().nextInt(10)) % 5 match {
        case 0 | 1 => 0.5
        case 2 => 1.0
        case 3 => 1.5
        case 4 => 0.25
      }
    }


    def randomNum(top: Int) = Math.abs(new Random().nextInt(top))


    dataRepo.remove(Map())
    (0 to 50).foreach { i =>
      val t = TradeData(randomCurr, randomNotional, (1 + randomNum(10)).toString + "y", "2015-11-10", randomCC, (4 + randomPercentage) + "%", randomPercentage + "%")
      dataRepo.insert(CaseHelper.ccToMap(t))
      //    }

      println(dataRepo.size)
    }
  }

}
