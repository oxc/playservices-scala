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
