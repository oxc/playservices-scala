package de.esotechnik.playservicesscala

import android.content.Intent
import android.view.View
import com.google.android.gms.common.api.{GoogleApiClient, Status}
import com.google.android.gms.games.Games.GamesOptions
import com.google.android.gms.{games => gms}
import de.esotechnik.playservicesscala.macros.loadApi

import scala.concurrent.Future

package object games {

  val Games = new ApiWrapper {
    def getAppId()(implicit apiClient : GoogleApiClient) : String = gms.Games.getAppId(apiClient)
    def getCurrentAccountName()(implicit apiClient : GoogleApiClient) : String = gms.Games.getCurrentAccountName(apiClient)
    def getSdkVariant()(implicit apiClient : GoogleApiClient) : Int = gms.Games.getSdkVariant(apiClient)
    def getSettingsIntent()(implicit apiClient : GoogleApiClient) : Intent = gms.Games.getSettingsIntent(apiClient)
    def setGravityForPopups(gravity : Int)(implicit apiClient : GoogleApiClient) : Unit = gms.Games.setGravityForPopups(apiClient, gravity)
    def setViewForPopups(gamesContentView : View)(implicit apiClient : GoogleApiClient) : Unit = gms.Games.setViewForPopups(apiClient, gamesContentView)
    def signOut()(implicit apiClient : GoogleApiClient) : Future[Status] = gms.Games.signOut(apiClient)
  }

  @loadApi(gms.Games.Achievements) object Achievements {}
  @loadApi(gms.Games.Events) object Events {}
  @loadApi(gms.Games.GamesMetadata) object GamesMetadata {}
  @loadApi(gms.Games.Invitations) object Invitations {}
  @loadApi(gms.Games.Leaderboards) object Leaderboards {}
  @loadApi(gms.Games.Notifications) object Notifications {}
  @loadApi(gms.Games.Players) object Players {}
  @loadApi(gms.Games.Quests) object Quests {}
  @loadApi(gms.Games.RealTimeMultiplayer) object RealTimeMultiplayer {}
  @loadApi(gms.Games.Requests) object Requests {}
  @loadApi(gms.Games.Snapshots) object Snapshots {}
  @loadApi(gms.Games.TurnBasedMultiplayer) object TurnBasedMultiplayer {}

  trait PlayServicesGames { self : PlayServices =>
    gamesOptions match {
      case Some(options) => self.addApi(gms.Games.API, options)
      case None => self.addApi(gms.Games.API)
    }

    protected val gamesOptions : Option[GamesOptions] = None

    protected val games = Games
    protected val achievements = Achievements
    protected val events = Events
    protected val gamesMetadata = GamesMetadata
    protected val invitations = Invitations
    protected val leaderboards = Leaderboards
    protected val notifications = Notifications
    protected val players = Players
    protected val quests = Quests
    protected val realTimeMultiplayer = RealTimeMultiplayer
    protected val requests = Requests
    protected val snapshots = Snapshots
    protected val turnBasedMultiplayer = TurnBasedMultiplayer
  }

}
