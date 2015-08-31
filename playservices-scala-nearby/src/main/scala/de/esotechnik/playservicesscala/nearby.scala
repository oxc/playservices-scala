/* Copyright 2015 Bernhard Frauendienst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
