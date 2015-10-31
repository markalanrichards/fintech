package data

import models.{CapitalRateData, CounterpartyData, CaseHelper, TradeData}

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
  val ratingsRepo = new MongoDataRepository(connection,"ratings")
  val parties = List("Liverpool Bank", "Bristol Bank","Southampton Bank", "Edinburgh Bank", "Norfolk Bank", "Birmingham Bank","Cardiff Bank")

  var ratings = Map(
    "AAA"->"0.07",
    "AA" -> "0.08",
    "A+" -> "0.10",
    "A" -> "0.12",
    "A-" -> "0.20",
    "BBB+" -> "0.35",
    "BBB" -> "0.60",
    "BBB-" -> "1.00",
    "BB+" -> "2.50",
    "BB" -> "4.25",
    "BB-"->"6.5"
  )

  def main(a: Array[String]) {


    val dataRepo = tradesRepo

    //println(counterpartiesRepo.size)

    def randomRating= {
      ratings.toList(Random.nextInt(ratings.size))._1
    }

    def randomCC = {
      parties(Random.nextInt(parties.length))
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
    counterpartiesRepo.remove(Map())
    ratingsRepo.remove(Map())
    parties.foreach(party => {
      val counterParty = CounterpartyData(party,randomRating)
      counterpartiesRepo.insert(CaseHelper.ccToMap(counterParty))
    })
    ratings.foreach(entry => {
      ratingsRepo.insert(CaseHelper.ccToMap(CapitalRateData(entry._1,entry._2)))
    })
    (0 to 50).foreach { i =>
      val randomCounterParty: String = randomCC

      val t = TradeData(randomCurr, randomNotional, (1 + randomNum(10)).toString, "2015-11-10", randomCounterParty, (4 + randomPercentage).toString, randomPercentage.toString)
      dataRepo.insert(CaseHelper.ccToMap(t))
      println(dataRepo.size)
    }

  }

}
