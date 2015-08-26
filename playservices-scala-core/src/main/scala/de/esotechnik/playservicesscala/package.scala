package de.esotechnik

import android.app.Activity
import com.google.android.gms.common.api.Api.ApiOptions.{HasOptions, NotRequiredOptions}
import com.google.android.gms.common.api.GoogleApiClient.{Builder => ClientBuilder, ConnectionCallbacks, OnConnectionFailedListener}
import com.google.android.gms.common.api.{Api => PlayApi, GoogleApiClient}

import scala.collection.mutable

package object playservicesscala {

  trait PlayServices extends Activity with ConnectionCallbacks with OnConnectionFailedListener {
    this: Activity =>

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

      assert(playServiceAPIs.nonEmpty, "No APIs added to PlayServices. Call addApi() before onStart() is called.")
      playServiceAPIs.foreach { _.addApi(builder) }

      return builder.build()
    }

    override def onStart(): Unit = {
      super.onStart()
      googleApiClient.connect()
    }

    override def onStop() : Unit = {
      googleApiClient.disconnect()
    }

  }

  private[playservicesscala] sealed trait ApiDependency {
    def addApi(builder : ClientBuilder) : Unit
  }
  private[playservicesscala] case class ApiWithOptions[O <: HasOptions](api : PlayApi[O], options : O) extends ApiDependency {
    override def addApi(builder : ClientBuilder) = builder.addApi(api, options)
  }
  private[playservicesscala] case class ApiNoOptions[O <: NotRequiredOptions](api : PlayApi[O]) extends ApiDependency {
    override def addApi(builder: ClientBuilder) = builder.addApi(api)
  }

}
