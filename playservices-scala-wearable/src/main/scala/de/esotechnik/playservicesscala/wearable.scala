package de.esotechnik.playservicesscala

import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.Wearable.WearableOptions
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object wearable {

  val Capability = loadApi(Wearable.CapabilityApi)
  val Channel = loadApi(Wearable.ChannelApi)
  val Data = loadApi(Wearable.DataApi)
  val Message = loadApi(Wearable.MessageApi)
  val Node = loadApi(Wearable.NodeApi)
  
  trait PlayServicesWearable { self : PlayServices =>
    wearableOptions match {
      case Some(options) => self.addApi(Wearable.API, options)
      case None => self.addApi(Wearable.API)
    }

    protected val wearableOptions : Option[WearableOptions] = None

    protected val capability = Capability
    protected val channel = Channel
    protected val data = Data
    protected val message = Message
    protected val node = Node
  }

}
