package de.esotechnik.playservicesscala

import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.MessagesOptions
import de.esotechnik.playservicesscala.macros.loadApi

package object nearby {

  @loadApi(Nearby.Connections) object Connections {}
  @loadApi(Nearby.Messages) object Messages {}

  trait PlayServicesNearbyConnections { self : PlayServices =>
    self.addApi(Nearby.CONNECTIONS_API)

    protected val nearbyConnections = Connections
  }

  trait PlayServicesNearbyMessages { self: PlayServices =>
    nearbyMessagesOptions match {
      case Some(options) => self.addApi(Nearby.MESSAGES_API, options)
      case None => self.addApi(Nearby.MESSAGES_API)
    }

    protected val nearbyMessagesOptions: Option[MessagesOptions] = None

    protected val nearbyMessages = Messages
  }

  trait PlayServicesNearby extends AnyRef
    with PlayServicesNearbyConnections
    with PlayServicesNearbyMessages { self : PlayServices =>
  }

}
