package de.esotechnik.playservicesscala

import com.google.android.gms.{panorama => gms}
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object panorama {

  val Panorama = loadApi(gms.Panorama.PanoramaApi)

  trait PlayServicesPanorama { self : PlayServices =>
    self.addApi(gms.Panorama.API)

    protected val panorama = Panorama
  }

}
