package controllers

import data.MongoRepo._
import helpers.Implicits
import play.api.routing.JavaScriptReverseRouter

// import data.TestRepo._
import models.{CaseHelper, CounterpartyData, TradeData}
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import Implicits._

object Application extends Controller {


  def d3 = Action {
    Ok(views.html.d3())
  }
  def index = Action {
    Ok(views.html.populate(counterpartyForm, tradeForm))
  }

  def trades = Action {
    val trades = tradesRepo.findAll[List[Map[String, String]]]()
    Ok(views.html.trades())
  }

  def tradesJson = Action {
    val counterPartiesMap: Map[String,String] = counterpartiesRepo.findAll().map(v => {
      ( v.get("counterparty").get,v.get("rating").get)
    }).toMap
    val trades = tradesRepo.findAll().map(x => {
      Map("rating" -> counterPartiesMap.get(x.get("counterparty").get)) ++ x
    })
    Ok(com.mongodb.util.JSON.serialize(trades)).as("application/json")
  }

  def counterparties = Action {
    val trades = counterpartiesRepo.findAll[List[Map[String, String]]]()
    Ok(com.mongodb.util.JSON.serialize(trades)).as("application/json")
  }

  val counterpartyForm = Form(
    mapping(
      "counterparty" -> nonEmptyText,
      "rating" -> nonEmptyText
    )(CounterpartyData.apply)(CounterpartyData.unapply)
  )


  val tradeForm = Form(
    mapping(
      "currency" -> nonEmptyText,
      "notional" -> number,
      "tenor" -> nonEmptyText,
      "maturityDate" -> nonEmptyText,
      "counterparty" -> nonEmptyText,
      "fixRate" -> nonEmptyText,
      "floatingRate" -> nonEmptyText
    )(TradeData.apply)(TradeData.unapply)
  )


  val tradePost = Action { implicit request =>
    println(request.body.toString)
    val trade = tradeForm.bindFromRequest().get
    println("Trade data received: " + trade)
    val id = counterpartiesRepo.insert(CaseHelper.ccToMap(trade))
    Redirect(routes.Application.trades())
  }


  val counterpartyPost = Action { implicit request =>
    println(request.body.toString)
    val cpty = counterpartyForm.bindFromRequest().get
    println("Cpty data received: " + cpty)
    val id = counterpartiesRepo.insert(CaseHelper.ccToMap(cpty))
    Redirect(routes.Application.counterparties())
  }


}
