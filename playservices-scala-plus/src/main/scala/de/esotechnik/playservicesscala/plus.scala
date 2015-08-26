package de.esotechnik.playservicesscala

import com.google.android.gms.plus.Plus
import com.google.android.gms.plus.Plus.PlusOptions
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object plus {

  val Account = loadApi(Plus.AccountApi)
  val Moments = loadApi(Plus.MomentsApi)
  val People = loadApi(Plus.PeopleApi)

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
