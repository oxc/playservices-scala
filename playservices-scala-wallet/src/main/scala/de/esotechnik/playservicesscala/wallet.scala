package de.esotechnik.playservicesscala

import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import de.esotechnik.playservicesscala.macros.loadApi

package object wallet {

  @loadApi(Wallet.Payments) object Payments {}

  trait PlayServicesWallet { self : PlayServices =>
    self.addApi(Wallet.API, walletOptions)

    protected val walletOptions : WalletOptions

    protected val payments = Payments
  }

}
