package de.esotechnik.playservicesscala

import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.Wearable.WearableOptions
import de.esotechnik.playservicesscala.macros.loadApi

package object wearable {

  @loadApi(Wearable.CapabilityApi) object Capability {}
  @loadApi(Wearable.ChannelApi) object Channel {}
  @loadApi(Wearable.DataApi) object Data {}
  @loadApi(Wearable.MessageApi) object Message {}
  @loadApi(Wearable.NodeApi) object Node {}
  
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
