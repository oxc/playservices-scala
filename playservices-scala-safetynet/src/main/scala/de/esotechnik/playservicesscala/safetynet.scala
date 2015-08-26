package de.esotechnik.playservicesscala

import com.google.android.gms.{safetynet => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object safetynet {

  val SafetyNet = loadApi(gms.SafetyNet.SafetyNetApi)

  trait PlayServicesSafetyNet { self : PlayServices =>
    self.addApi(gms.SafetyNet.API)

    protected val safetyNet = SafetyNet
  }

}
