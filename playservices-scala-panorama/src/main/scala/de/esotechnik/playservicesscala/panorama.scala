package de.esotechnik.playservicesscala

import com.google.android.gms.{panorama => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object panorama {

  @loadApi(gms.Panorama.PanoramaApi) object Panorama {}

  trait PlayServicesPanorama { self : PlayServices =>
    self.addApi(gms.Panorama.API)

    protected val panorama = Panorama
  }

}
