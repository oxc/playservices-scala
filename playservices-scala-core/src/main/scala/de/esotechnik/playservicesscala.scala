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

package de.esotechnik

import android.app.Activity
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.Api.ApiOptions.{HasOptions, NotRequiredOptions}
import com.google.android.gms.common.api.GoogleApiClient.{Builder => ClientBuilder, ConnectionCallbacks, OnConnectionFailedListener}
import com.google.android.gms.common.api.{Api => PlayApi, GoogleApiClient}

import scala.collection.mutable

package object playservicesscala {

  trait PlayServices extends Activity with ConnectionCallbacks with OnConnectionFailedListener {
    this: Activity =>

    private val TAG = "PlayServicesScala"

    protected val apis = new DeclaredApis()

    lazy implicit protected val googleApiClient : GoogleApiClient = buildGoogleApiClient()

    private def buildGoogleApiClient(): GoogleApiClient = {
      val builder = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)

      require(apis.apis.nonEmpty, "No APIs added to PlayServices. Add some to `apis` before onStart() is called.")
      apis.apis.foreach { _.addApi(builder) }

      Log.d(TAG, "Building GoogleApiClient...")

      return builder.build()
    }

    override def onStart(): Unit = {
      super.onStart()
      Log.d(TAG, "Connecting GoogleApiClient...")
      googleApiClient.connect()
    }

    override def onStop() : Unit = {
      super.onStop()
      Log.d(TAG, "Disconnecting GoogleApiClient...")
      googleApiClient.disconnect()
    }


    val connectionFailedResolutionResultCode: Int = 1001

    override def onConnectionFailed(connectionResult : ConnectionResult) {
      if (connectionResult.hasResolution) {
        connectionResult.startResolutionForResult(this, connectionFailedResolutionResultCode)
      }
    }
  }

  class DeclaredApis {
    private[playservicesscala] val apis : mutable.HashSet[ApiDependency] = mutable.HashSet()

    def ++=(xs : TraversableOnce[ApiDependency]) = apis ++= xs

    def +=(apiDependency: ApiDependency) = apis += apiDependency

    def ?=(apiDependency: ApiDependency) = apis += apiDependency.ifAvailable
  }

  sealed trait ApiDependency {
    private[playservicesscala] def addApi(builder : ClientBuilder) : Unit

    private[playservicesscala] def addApiIfAvailable(builder : ClientBuilder) : Unit

    def ifAvailable : ApiDependency = new OptionalApiDependency(this)
  }
  implicit final class ApiNoOptions[O <: NotRequiredOptions](val api : PlayApi[O]) extends ApiDependency {
    override private[playservicesscala] def addApi(builder: ClientBuilder) = builder.addApi(api)

    override private[playservicesscala] def addApiIfAvailable(builder: ClientBuilder) = builder.addApiIfAvailable(api)
  }
  final class ApiWithOptions[O <: HasOptions](api : PlayApi[O], options : => O) extends ApiDependency {
    override private[playservicesscala] def addApi(builder : ClientBuilder) = builder.addApi(api, options)

    override private[playservicesscala] def addApiIfAvailable(builder: ClientBuilder) = builder.addApiIfAvailable(api, options)
  }

  implicit final class RichApi[O <: HasOptions](val api: PlayApi[O]) extends AnyVal {
    def withOptions(options: => O) = new ApiWithOptions(api, options)

    def %(options: => O) = withOptions(options)
  }

  implicit def wrapperToDependency[O <: NotRequiredOptions](apiWrapper: ApiWrapper[PlayApi[O]]) = ApiNoOptions(apiWrapper.requiredApi)
  implicit def wrapperToRichApi[O <: HasOptions](apiWrapper : ApiWrapper[PlayApi[O]]) = RichApi(apiWrapper.requiredApi)

  final class OptionalApiDependency(apiDependency: ApiDependency) extends ApiDependency {
    override private[playservicesscala] def addApi(builder: ClientBuilder): Unit = apiDependency.addApiIfAvailable(builder)

    override private[playservicesscala] def addApiIfAvailable(builder: ClientBuilder): Unit = apiDependency.addApiIfAvailable(builder)

    override def ifAvailable = this
  }

}
