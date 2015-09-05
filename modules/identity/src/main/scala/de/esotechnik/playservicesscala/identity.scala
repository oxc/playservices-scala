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

import com.google.android.gms.identity.intents.{Address => PlayServicesAddress, UserAddressRequest}
import com.google.android.gms.common.api.{Api, GoogleApiClient}
import de.esotechnik.playservicesscala.macros.requireApi

package object identity {

  @requireApi(PlayServicesAddress.API) object Address extends AnyRef with ApiProvider {

    def requestUserAddress(request: UserAddressRequest,requestCode: Int)(implicit googleApiClient: GoogleApiClient) =
      PlayServicesAddress.requestUserAddress(googleApiClient, request, requestCode)
  }
}
