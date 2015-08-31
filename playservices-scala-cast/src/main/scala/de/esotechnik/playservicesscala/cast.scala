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

import com.google.android.gms.cast.Cast.CastOptions
import com.google.android.gms.cast.CastRemoteDisplay.CastRemoteDisplayOptions
import com.google.android.gms.{cast => gms}
import de.esotechnik.playservicesscala.macros.loadApi

package object cast {

  @loadApi(gms.Cast.CastApi : gms.Cast.CastApi) object Cast {}
  @loadApi(gms.CastRemoteDisplay.CastRemoteDisplayApi) object CastRemoteDisplay {}

  trait PlayServicesCast { self : PlayServices =>
    self.addApi(gms.Cast.API, castOptions)

    protected val castOptions : CastOptions

    protected val cast = Cast
  }

  trait PlayServicesCastRemoteDisplay { self : PlayServices =>
    self.addApi(gms.CastRemoteDisplay.API, castRemoteDisplayOptions)

    protected val castRemoteDisplayOptions : CastRemoteDisplayOptions

    protected val castRemoteDisplay = CastRemoteDisplay
  }

}
