package org.bustos.realityball

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.{ ProvenShape, ForeignKeyQuery }
import spray.json._
import DefaultJsonProtocol._

object RealityballRecords {
  import scala.collection.mutable.Queue

  case class Statistic(var total: Double, var rh: Double, var lh: Double)
  case class StatisticInputs(var totalNumer: Int, var totalDenom: Int, var rhNumer: Int, var rhDenom: Int, var lhNumer: Int, var lhDenom: Int)
  case class RunningHitterData(ba: Queue[StatisticInputs], obp: Queue[StatisticInputs], slugging: Queue[StatisticInputs], fantasy: Map[String, Queue[Statistic]])
  case class Team(year: String, mnemonic: String, league: String, city: String, name: String)
  case class TeamMetaData(retrosheetId: String, site: String, zipCode: String, mlbComId: String, mlbComName: String, timeZone: String)

  case class BattingAverageObservation(date: String, bAvg: Double, lhBAvg: Double, rhBAvg: Double)

  case class Player(id: String, year: String, lastName: String, firstName: String, batsWith: String, throwsWith: String, team: String, position: String)
  case class PlayerSummary(id: String, RHatBats: Int, LHatBats: Int, games: Int, mlbId: String, brefId: String, espnId: String)
  case class PitcherSummary(id: String, wins: Int, losses: Int, saves: Int, games: Int, mlbId: String, brefId: String, espnId: String)
  case class PlayerData(meta: Player, appearances: PlayerSummary)
  case class PitcherData(meta: Player, appearances: PitcherSummary)

  case class PitcherDaily(id: String, game: String, date: String, opposing: String, var win: Int, var loss: Int, var save: Int,
                          var hits: Int, var walks: Int, var hitByPitch: Int, var strikeOuts: Int, var earnedRuns: Int, var outs: Int,
                          var shutout: Boolean, var noHitter: Boolean, var pitches: Int, var balls: Int)

  case class Game(id: String, var homeTeam: String, var visitingTeam: String, var site: String, var date: String, var number: Int)
  case class GameConditions(id: String, var startTime: String, var daynight: String, var usedh: Boolean,
                            var temp: Int, var winddir: String, var windspeed: Int, var fieldcond: String, var precip: String, var sky: String)
  case class GameScoring(id: String, var umphome: String, var ump1b: String, var ump2b: String, var ump3b: String, var howscored: String,
                         var timeofgame: Int, var attendance: Int, var wp: String, var lp: String, var save: String)

  case class BallparkDaily(var id: String, var date: String, var RHhits: Int, var RHtotalBases: Int, var RHatBat: Int, var LHhits: Int, var LHtotalBases: Int, var LHatBat: Int)
  case class Ballpark(id: String, name: String, aka: String, city: String, state: String, start: String, end: String, league: String, notes: String)

  case class IdMapping(mlbId: String, mlbName: String, mlbTeam: String, brefId: String, brefName: String, espnId: String, espnName: String, retroId: String, retroName: String)

  val hitterRawLH = TableQuery[HitterRawLHStatsTable]
  val hitterRawRH = TableQuery[HitterRawRHStatsTable]
  val hitterStats = TableQuery[HitterDailyStatsTable]
  val hitterMovingStats = TableQuery[HitterStatsMovingTable]
  val hitterFantasy = TableQuery[HitterFantasyTable]
  val hitterFantasyMoving = TableQuery[HitterFantasyMovingTable]
  val pitcherFantasy = TableQuery[PitcherFantasyTable]
  val pitcherFantasyMoving = TableQuery[PitcherFantasyMovingTable]
  val hitterVolatilityStats = TableQuery[HitterStatsVolatilityTable]
  val gamesTable = TableQuery[GamesTable]
  val ballparkDailiesTable = TableQuery[BallparkDailiesTable]
  val ballparkTable = TableQuery[BallparkTable]
  val gameConditionsTable = TableQuery[GameConditionsTable]
  val gameScoringTable = TableQuery[GameScoringTable]
  val teamsTable = TableQuery[TeamsTable]
  val playersTable = TableQuery[PlayersTable]
  val pitcherDailyTable = TableQuery[PitcherDailyTable]
  val idMappingTable = TableQuery[IdMappingTable]

  val hitterFantasyStats = TableQuery[HitterFantasyTable]
  val hitterFantasyMovingStats = TableQuery[HitterFantasyMovingTable]
  val pitcherFantasyStats = TableQuery[PitcherFantasyTable]
  val pitcherFantasyMovingStats = TableQuery[PitcherFantasyMovingTable]
  val pitcherStats = TableQuery[PitcherDailyTable]
}

import RealityballRecords._
object RealityballJsonProtocol extends DefaultJsonProtocol {
  import RealityballRecords._
  implicit val playerFormat = jsonFormat8(Player)
  implicit val playerSummaryFormat = jsonFormat7(PlayerSummary)
  implicit val pitcherSummaryFormat = jsonFormat8(PitcherSummary)
  implicit val playerDataFormat = jsonFormat2(PlayerData)
  implicit val pitcherDataFormat = jsonFormat2(PitcherData)
}

class TeamsTable(tag: Tag) extends Table[Team](tag, "teams") {
  def year = column[String]("year")
  def mnemonic = column[String]("mnemonic")
  def league = column[String]("league")
  def city = column[String]("city")
  def name = column[String]("name")

  def * = (year, mnemonic, league, city, name) <> (Team.tupled, Team.unapply)
}

class GamesTable(tag: Tag) extends Table[Game](tag, "games") {
  def id = column[String]("id", O.PrimaryKey)
  def homeTeam = column[String]("homeTeam")
  def visitingTeam = column[String]("visitingTeam")
  def site = column[String]("site")
  def date = column[String]("date")
  def number = column[Int]("number")

  def * = (id, homeTeam, visitingTeam, site, date, number) <> (Game.tupled, Game.unapply)
}

class GameConditionsTable(tag: Tag) extends Table[GameConditions](tag, "gameConditions") {
  def id = column[String]("id", O.PrimaryKey)
  def startTime = column[String]("startTime")
  def daynight = column[String]("daynight")
  def usedh = column[Boolean]("usedh")
  def temp = column[Int]("temp")
  def winddir = column[String]("winddir")
  def windspeed = column[Int]("windspeed")
  def fieldcond = column[String]("fieldcond")
  def precip = column[String]("precip")
  def sky = column[String]("sky")

  def * = (id, startTime, daynight, usedh, temp, winddir, windspeed, fieldcond, precip, sky) <> (GameConditions.tupled, GameConditions.unapply)
}

class BallparkDailiesTable(tag: Tag) extends Table[BallparkDaily](tag, "ballparkDailies") {
  def id = column[String]("id")
  def date = column[String]("date")
  def RHhits = column[Int]("RHhits")
  def RHtotalBases = column[Int]("RHtotalBases")
  def RHatBat = column[Int]("RHatBat")
  def LHhits = column[Int]("LHhits")
  def LHtotalBases = column[Int]("LHtotalBases")
  def LHatBat = column[Int]("LHatBat")

  def pk = index("pk_id_date", (id, date))

  def * = (id, date, RHhits, RHtotalBases, RHatBat, LHhits, LHtotalBases, LHatBat) <> (BallparkDaily.tupled, BallparkDaily.unapply)
}

class BallparkTable(tag: Tag) extends Table[Ballpark](tag, "ballpark") {
  def id = column[String]("id", O.PrimaryKey)
  def name = column[String]("name")
  def aka = column[String]("aka")
  def city = column[String]("city")
  def state = column[String]("state")
  def start = column[String]("start")
  def end = column[String]("end")
  def league = column[String]("league")
  def notes = column[String]("notes")

  def * = (id, name, aka, city, state, start, end, league, notes) <> (Ballpark.tupled, Ballpark.unapply)
}

class GameScoringTable(tag: Tag) extends Table[GameScoring](tag, "gameScoring") {
  def id = column[String]("id", O.PrimaryKey)
  def umphome = column[String]("umphome")
  def ump1b = column[String]("ump1b")
  def ump2b = column[String]("ump2b")
  def ump3b = column[String]("ump3b")
  def howscored = column[String]("howscored")
  def timeofgame = column[Int]("timeofgame")
  def attendance = column[Int]("attendarnce")
  def wp = column[String]("wp")
  def lp = column[String]("lp")
  def save = column[String]("save")

  def * = (id, umphome, ump1b, ump2b, ump3b, howscored, timeofgame, attendance, wp, lp, save) <> (GameScoring.tupled, GameScoring.unapply)
}

class PitcherDailyTable(tag: Tag) extends Table[PitcherDaily](tag, "pitcherDaily") {
  def id = column[String]("id");
  def game = column[String]("game");
  def date = column[String]("date")
  def opposing = column[String]("opposing")

  def win = column[Int]("win")
  def loss = column[Int]("loss")
  def save = column[Int]("save")
  def hits = column[Int]("hits")
  def walks = column[Int]("walks")
  def hitByPitch = column[Int]("hitByPitch")
  def strikeOuts = column[Int]("strikeOuts")
  def earnedRuns = column[Int]("earnedRuns")
  def outs = column[Int]("outs")
  def shutout = column[Boolean]("shutout")
  def noHitter = column[Boolean]("noHitter")
  def pitches = column[Int]("pitches")
  def balls = column[Int]("balls")

  def pk = index("pk_id_date", (id, game)) // Duplicate issue with Joaquin Benoit on 20100910

  def * = (id, game, date, opposing, win, loss, save, hits, walks, hitByPitch, strikeOuts, earnedRuns, outs, shutout, noHitter, pitches, balls) <> (PitcherDaily.tupled, PitcherDaily.unapply)
}

class PlayersTable(tag: Tag) extends Table[Player](tag, "players") {
  def id = column[String]("id"); def year = column[String]("year");
  def lastName = column[String]("lastName")
  def firstName = column[String]("firstName")
  def batsWith = column[String]("batsWith")
  def throwsWith = column[String]("throwsWith")
  def team = column[String]("team")
  def position = column[String]("position")

  def pk = primaryKey("pk_id_date", (id, year))

  def * = (id, year, lastName, firstName, batsWith, throwsWith, team, position) <> (Player.tupled, Player.unapply)
}

class IdMappingTable(tag: Tag) extends Table[IdMapping](tag, "idMapping") {
  def mlbId = column[String]("mlbId"); def mlbName = column[String]("mlbName"); def mlbTeam = column[String]("mlbTeam")
  def brefId = column[String]("brefId"); def brefName = column[String]("brefName");
  def espnId = column[String]("espnId"); def espnName = column[String]("espnName");
  def retroId = column[String]("retroId"); def retroName = column[String]("retroName");

  def pk = primaryKey("pk_mlb_id", (mlbId))

  def * = (mlbId, mlbName, mlbTeam, brefId, brefName, espnId, espnName, retroId, retroName) <> (IdMapping.tupled, IdMapping.unapply)
}

class HitterRawLHStatsTable(tag: Tag) extends Table[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)](tag, "hitterRawLHstats") {
  def date = column[String]("date"); def id = column[String]("id");
  def LHatBat = column[Int]("LHatBat")
  def LHsingle = column[Int]("LHsingle")
  def LHdouble = column[Int]("LHdouble")
  def LHtriple = column[Int]("LHtriple")
  def LHhomeRun = column[Int]("LHhomeRun")
  def LHRBI = column[Int]("LHRBI")
  def LHruns = column[Int]("LHruns")
  def LHbaseOnBalls = column[Int]("LHbaseOnBalls")
  def LHhitByPitch = column[Int]("LHhitByPitch")
  def LHsacFly = column[Int]("LHsacFly")
  def LHsacHit = column[Int]("LHsacHit")

  def pk = index("pk_id_date", (id, date)) // First game of double headers are ignored for now

  def * : ProvenShape[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)] =
    (date, id, LHatBat, LHsingle, LHdouble, LHtriple, LHhomeRun, LHRBI, LHruns, LHbaseOnBalls, LHhitByPitch, LHsacFly, LHsacHit)
}

class HitterRawRHStatsTable(tag: Tag) extends Table[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)](tag, "hitterRawRHstats") {
  def date = column[String]("date"); def id = column[String]("id");
  def RHatBat = column[Int]("RHatBat")
  def RHsingle = column[Int]("RHsingle")
  def RHdouble = column[Int]("RHdouble")
  def RHtriple = column[Int]("RHtriple")
  def RHhomeRun = column[Int]("RHhomeRun")
  def RHRBI = column[Int]("RHRBI")
  def RHruns = column[Int]("RHruns")
  def RHbaseOnBalls = column[Int]("RHbaseOnBalls")
  def RHhitByPitch = column[Int]("RHhitByPitch")
  def RHsacFly = column[Int]("RHsacFly")
  def RHsacHit = column[Int]("RHsacHit")

  def pk = index("pk_id_date", (id, date)) // First game of double headers are ignored for now

  def * : ProvenShape[(String, String, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int)] =
    (date, id, RHatBat, RHsingle, RHdouble, RHtriple, RHhomeRun, RHRBI, RHruns, RHbaseOnBalls, RHhitByPitch, RHsacFly, RHsacHit)
}

class HitterDailyStatsTable(tag: Tag) extends Table[(String, String, Int, Int, Int, Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double])](tag, "hitterDailyStats") {

  def date = column[String]("date"); def id = column[String]("id"); def lineupPosition = column[Int]("lineupPosition");
  def atBats = column[Int]("ab"); def plateAppearances = column[Int]("pa")
  def RHdailyBattingAverage = column[Option[Double]]("RHdailyBattingAverage")
  def LHdailyBattingAverage = column[Option[Double]]("LHdailyBattingAverage")
  def dailyBattingAverage = column[Option[Double]]("dailyBattingAverage")
  def RHbattingAverage = column[Option[Double]]("RHbattingAverage")
  def LHbattingAverage = column[Option[Double]]("LHbattingAverage")
  def battingAverage = column[Option[Double]]("battingAverage")
  def RHonBasePercentage = column[Option[Double]]("RHonBasePercentage")
  def LHonBasePercentage = column[Option[Double]]("LHonBasePercentage")
  def onBasePercentage = column[Option[Double]]("onBasePercentage")
  def RHsluggingPercentage = column[Option[Double]]("RHsluggingPercentage")
  def LHsluggingPercentage = column[Option[Double]]("LHsluggingPercentage")
  def sluggingPercentage = column[Option[Double]]("sluggingPercentage")

  def pk = index("pk_id_date", (id, date)) // First game of double headers are ignored for now

  def * = (date, id, lineupPosition, atBats, plateAppearances,
    RHdailyBattingAverage, LHdailyBattingAverage, dailyBattingAverage,
    RHbattingAverage, LHbattingAverage, battingAverage,
    RHonBasePercentage, LHonBasePercentage, onBasePercentage,
    RHsluggingPercentage, LHsluggingPercentage, sluggingPercentage)
}

class HitterStatsMovingTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double])](tag, "hitterMovingStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def RHbattingAverageMov = column[Option[Double]]("RHbattingAverageMov")
  def LHbattingAverageMov = column[Option[Double]]("LHbattingAverageMov")
  def battingAverageMov = column[Option[Double]]("battingAverageMov")
  def RHonBasePercentageMov = column[Option[Double]]("RHonBasePercentageMov")
  def LHonBasePercentageMov = column[Option[Double]]("LHonBasePercentageMov")
  def onBasePercentageMov = column[Option[Double]]("onBasePercentageMov")
  def RHsluggingPercentageMov = column[Option[Double]]("RHsluggingPercentageMov")
  def LHsluggingPercentageMov = column[Option[Double]]("LHsluggingPercentageMov")
  def sluggingPercentageMov = column[Option[Double]]("sluggingPercentageMov")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id,
    RHbattingAverageMov, LHbattingAverageMov, battingAverageMov,
    RHonBasePercentageMov, LHonBasePercentageMov, onBasePercentageMov,
    RHsluggingPercentageMov, LHsluggingPercentageMov, sluggingPercentageMov)
}

class HitterFantasyTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double])](tag, "hitterFantasyStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def RHfanDuel = column[Option[Double]]("RHfanDuel")
  def LHfanDuel = column[Option[Double]]("LHfanDuel")
  def fanDuel = column[Option[Double]]("fanDuel")
  def RHdraftKings = column[Option[Double]]("RHdraftKings")
  def LHdraftKings = column[Option[Double]]("LHdraftKings")
  def draftKings = column[Option[Double]]("draftKings")
  def RHdraftster = column[Option[Double]]("RHdraftster")
  def LHdraftster = column[Option[Double]]("LHdraftster")
  def draftster = column[Option[Double]]("draftster")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id,
    RHfanDuel, LHfanDuel, fanDuel,
    RHdraftKings, LHdraftKings, draftKings,
    RHdraftster, LHdraftster, draftster)
}

class HitterFantasyMovingTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double])](tag, "hitterFantasyMovingStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def RHfanDuelMov = column[Option[Double]]("RHfanDuelMov")
  def LHfanDuelMov = column[Option[Double]]("LHfanDuelMov")
  def fanDuelMov = column[Option[Double]]("fanDuelMov")
  def RHdraftKingsMov = column[Option[Double]]("RHdraftKingsMov")
  def LHdraftKingsMov = column[Option[Double]]("LHdraftKingsMov")
  def draftKingsMov = column[Option[Double]]("draftKingsMov")
  def RHdraftsterMov = column[Option[Double]]("RHdraftsterMov")
  def LHdraftsterMov = column[Option[Double]]("LHdraftsterMov")
  def draftsterMov = column[Option[Double]]("draftsterMov")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id,
    RHfanDuelMov, LHfanDuelMov, fanDuelMov,
    RHdraftKingsMov, LHdraftKingsMov, draftKingsMov,
    RHdraftsterMov, LHdraftsterMov, draftsterMov)
}

class HitterStatsVolatilityTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double], Option[Double])](tag, "hitterVolatilityStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def RHbattingVolatility = column[Option[Double]]("RHbattingVolatility")
  def LHbattingVolatility = column[Option[Double]]("LHbattingVolatility")
  def battingVolatility = column[Option[Double]]("battingVolatility")
  def RHonBaseVolatility = column[Option[Double]]("RHonBaseVolatility")
  def LHonBaseVolatility = column[Option[Double]]("LHonBaseVolatility")
  def onBaseVolatility = column[Option[Double]]("onBaseVolatility")
  def RHsluggingVolatility = column[Option[Double]]("RHsluggingVolatility")
  def LHsluggingVolatility = column[Option[Double]]("LHsluggingVolatility")
  def sluggingVolatility = column[Option[Double]]("sluggingVolatility")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id,
    RHbattingVolatility, LHbattingVolatility, battingVolatility,
    RHonBaseVolatility, LHonBaseVolatility, onBaseVolatility,
    RHsluggingVolatility, LHsluggingVolatility, sluggingVolatility)
}

class PitcherFantasyTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double])](tag, "pitcherFantasyStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def fanDuel = column[Option[Double]]("fanDuel")
  def draftKings = column[Option[Double]]("draftKings")
  def draftster = column[Option[Double]]("draftster")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id, fanDuel, draftKings, draftster)
}

class PitcherFantasyMovingTable(tag: Tag) extends Table[(String, String, Option[Double], Option[Double], Option[Double])](tag, "pitcherFantasyMovingStats") {

  def date = column[String]("date"); def id = column[String]("id");
  def fanDuelMov = column[Option[Double]]("fanDuelMov")
  def draftKingsMov = column[Option[Double]]("draftKingsMov")
  def draftsterMov = column[Option[Double]]("draftsterMov")

  def pk = index("pk_id_date", (id, date))

  def * = (date, id, fanDuelMov, draftKingsMov, draftsterMov)
}