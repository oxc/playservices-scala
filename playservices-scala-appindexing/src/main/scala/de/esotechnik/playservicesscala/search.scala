package de.esotechnik.playservicesscala

import com.google.android.gms.{search => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object search {

  @loadApi(gms.SearchAuth.SearchAuthApi) object SearchAuth {}

  trait PlayServicesSearchAuth { self : PlayServices =>
    self.addApi(gms.SearchAuth.API)

    protected val searchAuth = SearchAuth
  }

}
