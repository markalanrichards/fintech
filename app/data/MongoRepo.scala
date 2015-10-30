package data

import com.mongodb.casbah.Imports._
import scala.collection.JavaConverters._


object MongoRepo {

  def main(a: Array[String]) {
    val dataRepo = new MongoDataRepository(new MongoConnection("mongodb://heroku_tvczrsc9:128m3n0bkg1qtuta2uv4p6ga7h@ds045464.mongolab.com:45464/heroku_tvczrsc9", "heroku_tvczrsc9"), "test_collection_1")
    dataRepo.remove(Map())
    (0 to 10000).foreach { i =>
      dataRepo.insert(Map(s"DATA_KEY$i" -> s"DATA_VALUE$i", "TIME" -> System.currentTimeMillis()))
    }

    println(dataRepo.size)
  }

}
