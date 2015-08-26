package de.esotechnik.playservicesscala

import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.Plus.PlusOptions
import de.esotechnik.playservicesscala.macros.loadApi

package object plus {

  @loadApi(Plus.AccountApi) object Account {}
  @loadApi(Plus.MomentsApi) object Moments {}
  @loadApi(Plus.PeopleApi) object People {}

  trait PlayServicesPlus { self : PlayServices =>
    plusOptions match {
      case Some(options) => self.addApi(Plus.API, options)
      case None => self.addApi(Plus.API)
    }

    protected val plusOptions : Option[PlusOptions] = None

    protected val account = Account
    protected val moment = Moments
    protected val people = People
  }

}
