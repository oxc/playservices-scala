package de.esotechnik.playservicesscala

import com.google.android.gms.identity.intents.Address.AddressOptions
import com.google.android.gms.identity.intents.{Address => PlayServicesAddress, UserAddressRequest}
import com.google.android.gms.common.api.GoogleApiClient

package object identity {

  val Address = new ApiWrapper {
    def requestUserAddress(request: UserAddressRequest,requestCode: Int)(implicit googleApiClient: GoogleApiClient) =
      PlayServicesAddress.requestUserAddress(googleApiClient, request, requestCode)
  }

  trait PlayServicesIdentity { self : PlayServices =>
    self.addApi(PlayServicesAddress.API, identityOptions)

    protected val identityOptions: AddressOptions = new AddressOptions()

    protected val address = Address
  }

}
