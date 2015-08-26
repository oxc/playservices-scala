package de.esotechnik.playservicesscala

import com.google.android.gms.appinvite.AppInviteApi
import com.google.android.gms.{appinvite => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object appinvite {

  @loadApi(gms.AppInvite.AppInviteApi) object AppInvite {}

  trait PlayServicesAppInvite { self : PlayServices =>
    self.addApi(gms.AppInvite.API)

    protected val appInvite = AppInvite
  }

}
