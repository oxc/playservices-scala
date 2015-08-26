package de.esotechnik.playservicesscala

import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.Wallet.WalletOptions
import de.esotechnik.playservicesscala.ApiLoader.loadApi

package object wallet {

  val Payments = loadApi(Wallet.Payments)

  trait PlayServicesWallet { self : PlayServices =>
    self.addApi(Wallet.API, walletOptions)

    protected val walletOptions : WalletOptions

    protected val payments = Payments
  }

}
