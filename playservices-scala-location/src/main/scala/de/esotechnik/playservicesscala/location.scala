package de.esotechnik.playservicesscala

import com.google.android.gms.location.LocationServices
import de.esotechnik.playservicesscala.macros.loadApi

package object location {

  @loadApi(LocationServices.FusedLocationApi) object FusedLocationProvider {}
  @loadApi(LocationServices.GeofencingApi) object Geofencing {}
  @loadApi(LocationServices.SettingsApi) object Settings {}

  trait PlayServicesLocation { self : PlayServices =>
    self.addApi(LocationServices.API)

    protected val fusedLocationProvider = FusedLocationProvider
    protected val geofencing = Geofencing
    protected val locationSettings = Settings
  }

}
