package de.esotechnik.playservicesscala

import com.google.android.gms.cast.Cast.CastOptions
import com.google.android.gms.cast.CastRemoteDisplay.CastRemoteDisplayOptions
import com.google.android.gms.{cast => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object cast {

  val Cast = loadApi(gms.Cast.CastApi)
  val CastRemoteDisplay = loadApi(gms.CastRemoteDisplay.CastRemoteDisplayApi)

  trait PlayServicesCast { self : PlayServices =>
    self.addApi(gms.Cast.API, castOptions)

    protected val castOptions : CastOptions

    protected val cast = Cast
  }

  trait PlayServicesCastRemoteDisplay { self : PlayServices =>
    self.addApi(gms.CastRemoteDisplay.API, castRemoteDisplayOptions)

    protected val castRemoteDisplayOptions : CastRemoteDisplayOptions

    protected val castRemoteDisplay = CastRemoteDisplay
  }

}
