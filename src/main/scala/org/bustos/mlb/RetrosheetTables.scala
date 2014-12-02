package org.bustos.mlb

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

class TeamsTable(tag: Tag)
  extends Table[(String, String, String, String)](tag, "teams") {

  def mnemonic: Column[String] = column[String]("mnemonic")
  def league: Column[String] = column[String]("league")
  def city: Column[String] = column[String]("city")
  def name: Column[String] = column[String]("name")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, String, String)] = (mnemonic, league, city, name)
}

class PlayersTable(tag: Tag)
  extends Table[(String, String, String, String, String, String, String)](tag, "players") {

  def mnemonic: Column[String] = column[String]("mnemonic")
  def lastName: Column[String] = column[String]("lastName")
  def firstName: Column[String] = column[String]("firstName")
  def batsWith: Column[String] = column[String]("batsWith")
  def throwsWith: Column[String] = column[String]("throwsWith")
  def team: Column[String] = column[String]("team")
  def position: Column[String] = column[String]("position")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, String, String, String, String, String)] = (mnemonic, lastName, firstName, batsWith, throwsWith, team, position)
}

class HitterRawLHStatsTable(tag: Tag)
  extends Table[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)](tag, "hitterRawLHstats") {

  def date: Column[String] = column[String]("date")
  def playerID: Column[String] = column[String]("playerID")
  def LHatBat: Column[Int] = column[Int]("LHatBat")
  def LHsingle: Column[Int] = column[Int]("LHsingle")
  def LHdouble: Column[Int] = column[Int]("LHdouble")
  def LHtriple: Column[Int] = column[Int]("LHtriple")
  def LHhomeRun: Column[Int] = column[Int]("LHhomeRun")
  def LHRBI: Column[Int] = column[Int]("LHRBI")
  def LHbaseOnBalls: Column[Int] = column[Int]("LHbaseOnBalls")
  def LHhitByPitch: Column[Int] = column[Int]("LHhitByPitch")
  def LHsacFly: Column[Int] = column[Int]("LHsacFly")
  def LHsacHit: Column[Int] = column[Int]("LHsacHit")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)] =
    (date, playerID, LHatBat, LHsingle, LHdouble, LHtriple, LHhomeRun, LHRBI, LHbaseOnBalls, LHhitByPitch, LHsacFly, LHsacHit)
}

class HitterRawRHStatsTable(tag: Tag)
  extends Table[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)](tag, "hitterRawRHstats") {

  def date: Column[String] = column[String]("date")
  def playerID: Column[String] = column[String]("playerID")
  def RHatBat: Column[Int] = column[Int]("RHatBat")
  def RHsingle: Column[Int] = column[Int]("RHsingle")
  def RHdouble: Column[Int] = column[Int]("RHdouble")
  def RHtriple: Column[Int] = column[Int]("RHtriple")
  def RHhomeRun: Column[Int] = column[Int]("RHhomeRun")
  def RHRBI: Column[Int] = column[Int]("RHRBI")
  def RHbaseOnBalls: Column[Int] = column[Int]("RHbaseOnBalls")
  def RHhitByPitch: Column[Int] = column[Int]("RHhitByPitch")
  def RHsacFly: Column[Int] = column[Int]("RHsacFly")
  def RHsacHit: Column[Int] = column[Int]("RHsacHit")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)] =
    (date, playerID, RHatBat, RHsingle, RHdouble, RHtriple, RHhomeRun, RHRBI, RHbaseOnBalls, RHhitByPitch, RHsacFly, RHsacHit)
}

class HitterStatsTable(tag: Tag)
  extends Table[(String, String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double)](tag, "hitterDailyStats") {

  def date: Column[String] = column[String]("date")
  def playerID: Column[String] = column[String]("playerID")
  def RHbattingAverage: Column[Double] = column[Double]("RHbattingAverage")
  def LHbattingAverage: Column[Double] = column[Double]("LHbattingAverage")
  def battingAverage: Column[Double] = column[Double]("battingAverage")
  def RHonBasePercentage: Column[Double] = column[Double]("RHonBasePercentage")
  def LHonBasePercentage: Column[Double] = column[Double]("LHonBasePercentage")
  def onBasePercentage: Column[Double] = column[Double]("onBasePercentage")
  def RHsluggingPercentage: Column[Double] = column[Double]("RHsluggingPercentage")
  def LHsluggingPercentage: Column[Double] = column[Double]("LHsluggingPercentage")
  def sluggingPercentage: Column[Double] = column[Double]("sluggingPercentage")
  def RHfantasyScore: Column[Double] = column[Double]("RHfantasyScore")
  def LHfantasyScore: Column[Double] = column[Double]("LHfantasyScore")
  def fantasyScore: Column[Double] = column[Double]("fantasyScore")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double)] =
    (date, playerID, 
      RHbattingAverage, LHbattingAverage, battingAverage,  
      RHonBasePercentage, LHonBasePercentage, onBasePercentage, RHsluggingPercentage, LHsluggingPercentage,
      sluggingPercentage, RHfantasyScore, LHfantasyScore, fantasyScore)
}

