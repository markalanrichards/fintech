package data

import com.mongodb.casbah.Imports._

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

// interface that provide CRUD operations
// move to library
trait DataRepository {

  def insert[T <: Any](obj: Map[String, T]): AnyRef

  def update[T <: Any](query: Map[String, T], obj: Map[String, T]): AnyRef

  def updateAll[T <: Any](query: Map[String, T], obj: Map[String, T]): AnyRef

  def updateOrInsert[T <: Any](query: Map[String, T], obj: Map[String, T], upsertOption: Boolean = true, multi: Boolean = false): AnyRef

  def search[T <: Any](filter: Map[String, T]): List[Map[String, T]]

  def first[T <: Any](filter: Map[String, T]): Option[Map[String, T]]

  def remove[T <: Any](filter: Map[String, T]): Int

  def findAll[T <: Any](): List[Map[String, T]]

}


class MongoConnection(mongoUri: String, dbName: String) {

  val client = MongoClient(MongoClientURI(mongoUri))
  val db = client.getDB(dbName) // which DB to use ???

  def collections = db.collectionNames()

  def collection(name: String) = db(name)

}

class InMemoryDataRepository(cache: ArrayBuffer[Map[String, AnyRef]]) extends DataRepository {
  override def insert[T](obj: Map[String, T]): AnyRef = ???

  override def update[T](query: Map[String, T], obj: Map[String, T]): AnyRef = ???

  override def findAll[T](): List[Map[String, T]] = ???

  override def updateOrInsert[T](query: Map[String, T], obj: Map[String, T], upsertOption: Boolean, multi: Boolean): AnyRef = ???

  override def remove[T](filter: Map[String, T]): Int = ???

  override def updateAll[T](query: Map[String, T], obj: Map[String, T]): AnyRef = ???

  override def search[T](filter: Map[String, T]): List[Map[String, T]] = ???

  override def first[T](filter: Map[String, T]): Option[Map[String, T]] = ???
}


// api to work with specific collection
class MongoDataRepository(val conn: MongoConnection, collectionName: String) extends DataRepository {

  val col = conn.collection(collectionName)

  def insert[T <: Any](obj: Map[String, T]) = {
    val res = col.insert(MongoDBObject(obj.toSeq: _*))
    res.getUpsertedId
  }


  def update[T <: Any](query: Map[String, T], obj: Map[String, T]): AnyRef = {
    updateOrInsert(query, obj, upsertOption = false)
  }

  def updateAll[T <: Any](query: Map[String, T], obj: Map[String, T]): AnyRef = {
    updateOrInsert(query, obj, upsertOption = false, multi = true)
  }


  def updateOrInsert[T <: Any](query: Map[String, T], obj: Map[String, T], upsertOption: Boolean = true, multi: Boolean = false): AnyRef = {
    val res = col.update(
      MongoDBObject(query.toSeq: _*),
      $set(obj.toSeq: _*),
      upsert = upsertOption
    )
    res.getUpsertedId
  }

  def size = col.size

  def findAll[T <: Any](): List[Map[String, T]] = {
    col.find().map { el =>
      el.toSeq.map(v => v._1 -> v._2.asInstanceOf[T]).toMap
    }.toList
  }
  def search[T <: Any](filter: Map[String, T]): List[Map[String, T]] = {
    val res = col.find(MongoDBObject(filter.toSeq: _*))
    val r2 = res.map { el =>
      el.toSeq.map(v => v._1 -> v._2.asInstanceOf[T]).toMap
    }.toList
    r2
  }

  def first[T <: Any](filter: Map[String, T]): Option[Map[String, T]] = {
    col.findOne(MongoDBObject(filter.toSeq: _*)).map(_.toSeq.map(v => v._1 -> v._2.asInstanceOf[T]).toMap)
  }

  def remove[T <: Any](filter: Map[String, T]): Int = {
    col.remove(MongoDBObject(filter.toSeq: _*)).getN
  }
}
