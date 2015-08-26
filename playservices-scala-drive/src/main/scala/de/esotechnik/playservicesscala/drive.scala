package de.esotechnik.playservicesscala

import com.google.android.gms.{drive => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object drive {

  @loadApi(gms.Drive.DriveApi) object Drive {}
  @loadApi(gms.Drive.DrivePreferencesApi) object DrivePreferences {}

  trait PlayServicesDrive { self : PlayServices =>
    self.addApi(gms.Drive.API)

    protected val drive = Drive
    protected val drivePreferences = DrivePreferences
  }

}
