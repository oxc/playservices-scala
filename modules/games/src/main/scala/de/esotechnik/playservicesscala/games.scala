/* Copyright 2015 Bernhard Frauendienst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.esotechnik.playservicesscala

import android.content.Intent
import android.view.View
import com.google.android.gms.common.api.{GoogleApiClient, Status, Api}
import com.google.android.gms.{games => gms}
import de.esotechnik.playservicesscala.macros.loadApi

import scala.concurrent.Future

package object games {

  object Games extends ApiWrapper[Api[gms.Games.GamesOptions]] {
    val requiredApi = gms.Games.API

    def getAppId()(implicit apiClient : GoogleApiClient) : String = gms.Games.getAppId(apiClient)
    def getCurrentAccountName()(implicit apiClient : GoogleApiClient) : String = gms.Games.getCurrentAccountName(apiClient)
    def getSdkVariant()(implicit apiClient : GoogleApiClient) : Int = gms.Games.getSdkVariant(apiClient)
    def getSettingsIntent()(implicit apiClient : GoogleApiClient) : Intent = gms.Games.getSettingsIntent(apiClient)
    def setGravityForPopups(gravity : Int)(implicit apiClient : GoogleApiClient) : Unit = gms.Games.setGravityForPopups(apiClient, gravity)
    def setViewForPopups(gamesContentView : View)(implicit apiClient : GoogleApiClient) : Unit = gms.Games.setViewForPopups(apiClient, gamesContentView)
    def signOut()(implicit apiClient : GoogleApiClient) : Future[Status] = gms.Games.signOut(apiClient)
  }

  @loadApi(gms.Games.Achievements, gms.Games.API) object Achievements {}
  @loadApi(gms.Games.Events, gms.Games.API) object Events {}
  @loadApi(gms.Games.GamesMetadata, gms.Games.API) object GamesMetadata {}
  @loadApi(gms.Games.Invitations, gms.Games.API) object Invitations {}
  @loadApi(gms.Games.Leaderboards, gms.Games.API) object Leaderboards {}
  @loadApi(gms.Games.Notifications, gms.Games.API) object Notifications {}
  @loadApi(gms.Games.Players, gms.Games.API) object Players {}
  @loadApi(gms.Games.Quests, gms.Games.API) object Quests {}
  @loadApi(gms.Games.RealTimeMultiplayer, gms.Games.API) object RealTimeMultiplayer {}
  @loadApi(gms.Games.Requests, gms.Games.API) object Requests {}
  @loadApi(gms.Games.Snapshots, gms.Games.API) object Snapshots {}
  @loadApi(gms.Games.TurnBasedMultiplayer, gms.Games.API) object TurnBasedMultiplayer {}

}
