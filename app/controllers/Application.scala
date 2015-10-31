package controllers

import data.MongoRepo._
import play.api.routing.JavaScriptReverseRouter

// import data.TestRepo._
import models.{CaseHelper, CounterpartyData, TradeData}
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

object Application extends Controller {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Application.scenarioAnalysis,
        routes.javascript.Application.stressAnalysis
      )
    ).as("text/javascript")
  }


  def index = Action {
    Ok(views.html.populate(counterpartyForm, tradeForm))
  }

  implicit class RichString(str: String) {
    def % =  str.replace("%", "").toDouble / 100
  }

  def scenarioAnalysis(newBps: String) = Action {
    // sys.error("error scenario analysis!")
    val libor = newBps.toInt
    val trades = tradesRepo.findAll().map(t => CaseHelper.createCaseClass[TradeData](t))
    val newValue = trades.map(t => {
      t.notional.toInt * (t.floatingRate.%.toDouble + libor - t.fixRate.%)
    } ).sum
    Ok(BigDecimal(newValue).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble.toString)
  }

  def stressAnalysis(newBps: String) = Action {
    Ok(newBps)
  }

  def trades = Action {
    val trades = tradesRepo.findAll[List[Map[String, String]]]()
    Ok(views.html.trades())
  }

  def tradesJson = Action {
    val trades = tradesRepo.findAll().map(x => {
      val first1 = counterpartiesRepo.search(Map("counterparty" -> x.get("counterparty")))
      val stringToOption = first1.head.get("rating")

      val cp : String = stringToOption.get.asInstanceOf[String]
      Map("rating" -> Option(cp)) ++ x
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
