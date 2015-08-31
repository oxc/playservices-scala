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
import com.google.android.gms.common.api.Api.ApiOptions.{HasOptions, NotRequiredOptions}
import com.google.android.gms.common.api.GoogleApiClient.{Builder => ClientBuilder, ConnectionCallbacks, OnConnectionFailedListener}
import com.google.android.gms.common.api.{Api => PlayApi, GoogleApiClient}

import scala.collection.mutable

package object playservicesscala {

  trait PlayServices extends Activity with ConnectionCallbacks with OnConnectionFailedListener {
    this: Activity =>

    private val TAG = "PlayServicesScala"

    private val playServiceAPIs: mutable.MutableList[ApiDependency] = mutable.MutableList()

    protected final def addApi[O <: HasOptions](api : PlayApi[O], options : O): Unit = {
      playServiceAPIs += ApiWithOptions(api, options)
    }

    protected final def addApi[O <: NotRequiredOptions](api : PlayApi[O]) : Unit = {
      playServiceAPIs += ApiNoOptions(api)
    }

    lazy implicit protected val googleApiClient : GoogleApiClient = buildGoogleApiClient()

    private def buildGoogleApiClient(): GoogleApiClient = {
      val builder = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)

      require(playServiceAPIs.nonEmpty, "No APIs added to PlayServices. Call addApi() before onStart() is called.")
      playServiceAPIs.foreach { _.addApi(builder) }

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

  }

  private[esotechnik] sealed trait ApiDependency {
    def addApi(builder : ClientBuilder) : Unit
  }
  private[esotechnik] case class ApiWithOptions[O <: HasOptions](api : PlayApi[O], options : O) extends ApiDependency {
    override def addApi(builder : ClientBuilder) = builder.addApi(api, options)
  }
  private[esotechnik] case class ApiNoOptions[O <: NotRequiredOptions](api : PlayApi[O]) extends ApiDependency {
    override def addApi(builder: ClientBuilder) = builder.addApi(api)
  }

}
