package controllers

import _root_.data.{MongoConnection, MongoDataRepository}
import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

import play.api.db._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(null))
  }

  def db = Action {
    val dataRepo = new MongoDataRepository(new MongoConnection("mongodb://heroku_tvczrsc9:128m3n0bkg1qtuta2uv4p6ga7h@ds045464.mongolab.com:45464/heroku_tvczrsc9", "heroku_tvczrsc9"), "trades")
    val out = dataRepo.findAll()
    Ok(out)
  }
}
