package de.esotechnik.playservicesscala

import com.google.android.gms.{drive => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object drive {

  val Drive = loadApi(gms.Drive.DriveApi)
  val DrivePreferences = loadApi(gms.Drive.DrivePreferencesApi)

  trait PlayServicesDrive { self : PlayServices =>
    self.addApi(gms.Drive.API)

    protected val drive = Drive
    protected val drivePreferences = DrivePreferences
  }

}
