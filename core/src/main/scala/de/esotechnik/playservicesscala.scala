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
import com.google.android.gms.common.api.GoogleApiClient.{ConnectionCallbacks, OnConnectionFailedListener}
import com.google.android.gms.common.api.{Api => PlayApi, GoogleApiClient, Scope}

import scala.collection.mutable
import scala.language.implicitConversions

package object playservicesscala {

  trait PlayServices extends Activity with ConnectionCallbacks with OnConnectionFailedListener {
    this: Activity =>

    private val TAG = "PlayServicesScala"

    protected val apis = new DeclaredApis()

    lazy implicit protected val googleApiClient : GoogleApiClient = buildGoogleApiClient().build()

    protected def buildGoogleApiClient(): GoogleApiClient.Builder = {
      val builder = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)

      require(apis.hasApis, "No APIs added to PlayServices. Add some to `apis` before onStart() is called.")
      apis.requiredApis.foreach { dep =>
        dep match {
          case ApiNoOptions(api, _) => builder.addApi(api)
          case ApiWithOptions(api, options, _) => builder.addApi(api, options())
        }
        dep.scopes.foreach { builder.addScope }
      }
      apis.optionalApis.foreach {
        case ApiNoOptions(api, scopes) => builder.addApiIfAvailable(api, scopes: _*)
        case ApiWithOptions(api, options, scopes) => builder.addApiIfAvailable(api, options(), scopes: _*)
      }
      apis.frozen = true

      return builder
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

  class DeclaredApis {
    private[playservicesscala] val requiredApis : mutable.HashSet[ApiDependency] = mutable.HashSet()
    private[playservicesscala] val optionalApis : mutable.HashSet[ApiDependency] = mutable.HashSet()

    private[playservicesscala] var frozen = false

    def hasApis = requiredApis.nonEmpty || optionalApis.nonEmpty

    @inline private[this] def requireUnfrozen() = require(!frozen, "Cannot add apis after the GoogleApiClient has been built.")

    def ++=(xs: TraversableOnce[ApiDependency]) = {
      requireUnfrozen()
      requiredApis ++= xs
    }

    def +=(apiDependency: ApiDependency) = {
      requireUnfrozen()
      requiredApis += apiDependency
    }

    def ?=(apiDependency: ApiDependency) = {
      requireUnfrozen()
      optionalApis += apiDependency
    }
  }

  sealed trait ApiDependency {
    val scopes : Seq[Scope]

    @inline def withScopes(scopes : Scope*): ApiDependency

    @inline def %(scopes: Scope*) = withScopes(scopes: _*)
  }

  final case class ApiNoOptions[O <: NotRequiredOptions](api : PlayApi[O], scopes: Seq[Scope]) extends ApiDependency {
    @inline override def withScopes(scopes: Scope*): ApiNoOptions[O] = ApiNoOptions(api, this.scopes ++ scopes)
  }

  final case class ApiWithOptions[O <: HasOptions](api : PlayApi[O], options : () => O, scopes: Seq[Scope]) extends ApiDependency {
    @inline override def withScopes(scopes: Scope*): ApiWithOptions[O] = ApiWithOptions(api, options, this.scopes ++ scopes)
  }


  implicit final class RichApi[O <: HasOptions](val api: PlayApi[O]) extends AnyVal {
    @inline def withOptions(options: => O) = new ApiWithOptions(api, () => options, Nil)

    @inline def %(options: => O) = withOptions(options)
  }

  implicit def apiToDependency[O <: NotRequiredOptions](api: PlayApi[O]): ApiNoOptions[O] = ApiNoOptions(api, Nil)
  implicit def requirementToDependency[O <: NotRequiredOptions](apiRequrement: ApiRequirement[PlayApi[O]]): ApiNoOptions[O] = ApiNoOptions(apiRequrement.requiredApi, Nil)
  implicit def requirementToRichApi[O <: HasOptions](apiRequirement : ApiRequirement[PlayApi[O]]): RichApi[O] = RichApi(apiRequirement.requiredApi)

}
