package de.esotechnik.playservicesscala

import com.google.android.gms.{appindexing => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object appindexing {

  @loadApi(gms.AppIndex.AppIndexApi) object AppIndex {}

  trait PlayServicesAppIndexing { self : PlayServices =>
    self.addApi(gms.AppIndex.API)

    protected val appIndex = AppIndex
  }

}
