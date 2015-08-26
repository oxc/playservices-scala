package de.esotechnik.playservicesscala

import com.google.android.gms.location.LocationServices
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object location {

  val FusedLocationProvider = loadApi(LocationServices.FusedLocationApi)
  val Geofencing = loadApi(LocationServices.GeofencingApi)
  val Settings = loadApi(LocationServices.SettingsApi)

  trait PlayServicesLocation { self : PlayServices =>
    self.addApi(LocationServices.API)

    protected val fusedLocationProvider = FusedLocationProvider
    protected val geofencing = Geofencing
    protected val locationSettings = Settings
  }

}
