package de.esotechnik.playservicesscala

import com.google.android.gms.{appindexing => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object appindexing {

  val AppIndex = loadApi(gms.AppIndex.AppIndexApi)

  trait PlayServicesAppIndexing { self : PlayServices =>
    self.addApi(gms.AppIndex.API)

    protected val appIndex = AppIndex
  }

}
