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

import com.google.android.gms.{wearable => gms}
import de.esotechnik.playservicesscala.macros.{provideApi, requireApi}

package object wearable {

  @requireApi(gms.Wearable.API) object Wearable {

    @provideApi(gms.Wearable.CapabilityApi) object Capability {}
    @provideApi(gms.Wearable.ChannelApi) object Channel {}
    @provideApi(gms.Wearable.DataApi) object Data {}
    @provideApi(gms.Wearable.MessageApi) object Message {}
    @provideApi(gms.Wearable.NodeApi) object Node {}

  }
}
