package de.esotechnik.playservicesscala

import com.google.android.gms.{search => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object search {

  val SearchAuth = loadApi(gms.SearchAuth.SearchAuthApi)

  trait PlayServicesSearchAuth { self : PlayServices =>
    self.addApi(gms.SearchAuth.API)

    protected val searchAuth = SearchAuth
  }

}
