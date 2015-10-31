package controllers

import controllers.Application._
import data.MongoRepo._
import helpers.Implicits
import models.{CaseHelper, TradeData}
import play.api.mvc.{Action, Controller}
import play.api.routing.JavaScriptReverseRouter
import Implicits._

object Scenarios extends Controller {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      JavaScriptReverseRouter("jsRoutes")(
        routes.javascript.Scenarios.scenarioAnalysis,
        routes.javascript.Scenarios.stressAnalysis
      )
    ).as("text/javascript")
  }

  def stressAnalysis(newBps: String) = Action {
    Ok(newBps)
  }

  private def liborTradeAnalysis(t: TradeData, libor: Double) = {
    t.notional * (t.fixRate.% - t.floatingRate.%.toDouble + libor)
  }

  val libors = List(2.5, 3.0, 3.5, 4.0, 4.5)

  def scenarioAnalysisBucked(newBps: String) = Action {
    val libor = newBps.toDouble / 100
    val trades = tradesRepo.findAll().map(t => CaseHelper.createCaseClass[TradeData](t))


    val librs = libors.map { liblib =>
        val values = (0 to 10).map { x =>
          val newValue = trades.filter(_.tenor.y == x).map(t => {
            liborTradeAnalysis(t, libor)
          }).sum
         (x -> newValue.r2str)
        }
      Map("key" -> s"Libor$liblib") -> Map("values" -> values)
      }
    Ok(com.mongodb.util.JSON.serialize(Map("names" -> librs))).as("application/json")
  }

  def scenarioAnalysis(newBps: String) = Action {
    // sys.error("error scenario analysis!")
    val libor = newBps.toDouble / 100
    val trades = tradesRepo.findAll().map(t => CaseHelper.createCaseClass[TradeData](t))
    val newValue = trades.map(t => {
      liborTradeAnalysis(t, libor)
    } ).sum
    Ok(newValue.r2str)
  }
  
  



}
