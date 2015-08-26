package de.esotechnik.playservicesscala

import com.google.android.gms.{appinvite => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object appinvite {

  val AppInvite = loadApi(gms.AppInvite.AppInviteApi)

  trait PlayServicesAppInvite { self : PlayServices =>
    self.addApi(gms.AppInvite.API)

    protected val appInvite = AppInvite
  }

}
