package de.esotechnik.playservicesscala

import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.Auth.AuthCredentialsOptions
import com.google.android.gms.auth.{api => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object auth {
  
  @loadApi(Auth.CredentialsApi) object Credentials {}

  trait PlayServicesAuthCred { self : PlayServices =>
    authCredientialsOptions match {
      case Some(options) => self.addApi(Auth.CREDENTIALS_API, options)
      case None => self.addApi(Auth.CREDENTIALS_API)
    }

    protected val authCredientialsOptions : Option[AuthCredentialsOptions] = None

    protected val credentials = Credentials
  }

}
