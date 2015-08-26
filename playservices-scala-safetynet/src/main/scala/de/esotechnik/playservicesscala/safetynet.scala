package de.esotechnik.playservicesscala

import com.google.android.gms.{safetynet => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object safetynet {

  @loadApi(gms.SafetyNet.SafetyNetApi) object SafetyNet {}

  trait PlayServicesSafetyNet { self : PlayServices =>
    self.addApi(gms.SafetyNet.API)

    protected val safetyNet = SafetyNet
  }

}
