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
import de.esotechnik.playservicesscala.macros.{provideApi, requireApi}

import scala.concurrent.Future

package object games {

  @requireApi(gms.Games.API) object Games extends ApiProvider {

    def getAppId()(implicit apiClient: GoogleApiClient): String = gms.Games.getAppId(apiClient)
    def getCurrentAccountName()(implicit apiClient: GoogleApiClient): String = gms.Games.getCurrentAccountName(apiClient)
    def getSdkVariant()(implicit apiClient: GoogleApiClient): Int = gms.Games.getSdkVariant(apiClient)
    def getSettingsIntent()(implicit apiClient: GoogleApiClient): Intent = gms.Games.getSettingsIntent(apiClient)
    def setGravityForPopups(gravity: Int)(implicit apiClient: GoogleApiClient): Unit = gms.Games.setGravityForPopups(apiClient, gravity)
    def setViewForPopups(gamesContentView: View)(implicit apiClient: GoogleApiClient): Unit = gms.Games.setViewForPopups(apiClient, gamesContentView)
    def signOut()(implicit apiClient: GoogleApiClient): Future[Status] = gms.Games.signOut(apiClient)

    @provideApi(gms.Games.Achievements) object Achievements {}
    @provideApi(gms.Games.Events) object Events {}
    @provideApi(gms.Games.GamesMetadata) object GamesMetadata {}
    @provideApi(gms.Games.Invitations) object Invitations {}
    @provideApi(gms.Games.Leaderboards) object Leaderboards {}
    @provideApi(gms.Games.Notifications) object Notifications {}
    @provideApi(gms.Games.Players) object Players {}
    @provideApi(gms.Games.Quests) object Quests {}
    @provideApi(gms.Games.RealTimeMultiplayer) object RealTimeMultiplayer {}
    @provideApi(gms.Games.Requests) object Requests {}
    @provideApi(gms.Games.Snapshots) object Snapshots {}
    @provideApi(gms.Games.TurnBasedMultiplayer) object TurnBasedMultiplayer {}

  }
}
  
