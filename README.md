# Play Services for Scala #

[ ![Download](https://api.bintray.com/packages/oxc/maven/playservices-scala/images/download.svg) ](https://bintray.com/oxc/maven/playservices-scala/_latestVersion) 
[![Join the chat at https://gitter.im/oxc/playservices-scala](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/oxc/playservices-scala?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A collection of traits and objects for making
[Google Play Services](https://developers.google.com/android/) more convenient to use when
programming for Android in Scala.

## Issues / Contributions ##

Please be aware that up until now, this library is untested in large parts. Please report any
issues or improvements using the GitHub [Issues](https://github.com/oxc/playservices-scala/issues)
system.

This also means that the library is possibly still subject to major changes.

Contributions are welcome and encouraged!

## Usage ##

This library mainly consists of two core features, the `PlayServices` trait and the API wrappers.

Both can be used individually, but they show their real power when combined together. Therefore,
the following examples will use always use both features at once. Keep in mind, that you can use
them separately, if you really want to.

The `PlayServices` trait sets up an implicit `GoogleApiClient` in your Activity with the APIs and
Scopes you specify, and which is started `onStart()` and stopped `onStop()`. You can add the raw
Google Play `Api[Options]` instances, and call the raw APIs' methods, but adding and using API
Wrappers is much more convenient.

The API wrappers provide API calls that take an implicit GoogleApiClient, and simplify adding the
APIs to the PlayServices trait. You can provide the GoogleApiClient yourself (implicitly or
explicitly), if you really want to, but the `PlayServices` trait does that already for you.

### Adding APIs ###

Google Play Services APIs can be added to the `api` object in the class body, in form of a
`ApiDependency` which can be created using one of the implicit conversions available
in `de.esotechnik.playservicesscala`:

```scala
import de.esotechnik.playservicesscala._
import de.esotechnik.playservicesscala.location.LocationServices

class MyActivity extends Activity with PlayServices {
  apis += LocationServices

  override onConnected(bundle: Bundle) = {
    val lastLocation = LocationServices.FusedLocation.getLastLocation();

    Toast.makeText(this, "Last known location: " + lastLocation, Toast.LENGTH_LONG).show()
  }
}
```

#### APIs with Options ####

APIs that require or optionally take options can be added using the implicitly available `%`
function (or `withOptions` if you prefer a more speaking name):

```scala
import com.google.android.gms.games.Games.GamesOptions
import de.esotechnik.playservicesscala._
import de.esotechnik.playservicesscala.games.Games

class MyActivity extends Activity with PlayServices {
  apis += Games % new GamesOptions.Builder().setShowConnectingPopup(false).build()

  override onConnected(bundle: Bundle) = {
    val playerName = Games.Players.getCurrentPlayer().getDisplayName()

    Toast.makeText(this, s"Hi $playerName", Toast.LENGTH_LONG).show()
  }
}
```

#### Optional APIs ####

Some APIs, namely the `Wearable` API, are not always available. Those APIs can either be added
using the `apis ?=` mutator. They can then be tested for connectivity by using obtaining an
`Option[_]` from their `ifAvailable` method, or by simply applying a body (which is short-hand for
`wrapper.ifAvailable.map`):

```scala
import de.esotechnik.playservicesscala._
import de.esotechnik.playservicesscala.wearable.Wearable

class MyActivity extends Activity with PlayServices {
  apis ?= Wearable

  override onConnected(bundle: Bundle) = {
    Wearable.Message { _.addListener(myWearableListener) }
  }

  override def onStop() = {
    super.onStop();

    Wearable.Message { _.removeListener(myWearableListener) }
  }
}
```


#### OAuth Scopes ####

Scopes can be added by simply appending them to the API dependency using yet another `%` method.
This adds them to the builder for normal apis, and passes them to the `addApiIfAvailable` call
for optional apis:

```scala
import com.google.android.gms
import de.esotechnik.playservicesscala._
import de.esotechnik.playservicesscala.plus.Plus
import de.esotechnik.playservicesscala.wearable.Wearable

class MyActivity extends Activity with PlayServices {

  apis += Drive % gms.drive.Drive.SCOPE_APPFOLDER

  apis ?= Plus % plusOptions() % gms.plus.Plus.SCOPE_PLUS_LOGIN

  def plusOptions() = new PlusOptions.Builder()
    .addActivityTypes("http://schemas.google.com/AddActivity")
    .build()
}
```

### API wrappers ###

In Google Play Services, there are two types of API objects:
* First there are the `Api[Options]` objects, which can be added to the GoogleApiClient.Builder.
  They are mapped to objects extending the `ApiRequirement` trait, can be added to the `apis`
  property of `PlayServices`, and encapsulate objects of the second type of Api:
* The second type are method providers, which contain the actual methods the Api provides. Those
  are mapped to objects extending the `ApiProvider` trait, which contain all the methods from the
  original Api, but accepting the `GoogleApiClient` implicitly (which is automatically available if
  you use the `PlayServices` trait on your Activity).

  Some APIs also return types that themselves provide API-like methods that take a
  `GoogleApiClient`. For those classes, implicitly available API wrappers in form of a value class
  are available (if I missed one, let me know!).

*NOTE*: For simple APIs, where there is only one object of each type, both are mapped onto a single
object (for example the AppInvite API). With some versatile APIs on the other hand, each method
provider has its own required API. They are still grouped into a parent object for consistency (for
example the Fitness API).

#### Future instead of PendingResult ####

Apart from accepting an implicit client, the API wrappers also convert PendingResults into Futures,
which makes them much more comfortable to use:

```scala
import de.esotechnik.playservicesscala.games.Games
import de.esotechnik.playservicesscala.games.Games.GamesMetaData
import scala.collection.JavaConversions._

class MyActivity extends Activity with PlayServices {
  apis += Games

  override def onResume() = {
    super.onResume()

    GamesMetadata.loadGame() onSuccess {
      case result => {
        val games = result.getGames.map(_.getDisplayName).mkString(" / ")

        Toast.makeText(this, s"Found the following games: $games", Toast.LENGTH_LONG).show()
      }
    }
  }
}
```

### Module specific API ###

For some modules, there is also some module-specific API provided by the library.

NOTE: Please be aware that I have not used several of the Google Play modules myself, so if you feel
there could be a convenient Scala wrapper for some of the Java api, don't hesitate to create an
Issue or, even better, a Pull Request.

The following module-specific additional APIs are available so far:

#### Drive ####

For Google Drive queries, the provided implicit conversions allow you to write such queries in a
much more concise and readable manner. The provided implicit API wrappers for DriveFile/Folder
make things even easier:

```scala
import com.google.android.gms.drive.query.SearchableField._
import com.google.android.gms.drive.query.{ SortableField => by }

import de.esotechnik.playservicesscala.drive._

val unpinnedMetadata = for {
  queryResult <- Drive.query(
    TITLE === "Scala is Fun" && IS_PINNED
      sortBy by.TITLE
  )
  firstId = queryResult.getMetadataBuffer.head.getDriveId
  file = Drive.getFile(firstId)
  metadataResult <- file.updateMetadata(new MetadataChangeSet.Builder().setPinned(false).build())
} yield metadataResult.getMetadata
```

You can also specify the sort order:
```scala
val result = Drive.query(
  TITLE === "Java is boring" && TRASHED
    sortBy ( by.TITLE DESC )
)
```

And you can also sort by multiple fields:
```scala
val result = Drive.query(
  TITLE contains "Scala"
    sortBy ( by.TITLE ASC, by.CREATED_DATE DESC )
)
```

## Build configuration ##

The library is split up in multiple subprojects. There is one `core` project, which contains some
macros and general utility classes that are not API-specific. Additionally, for each
[Google Play Services API library](https://developers.google.com/android/guides/setup#split),
there is a matching `playservices-scala-` library that contains an API wrapper (if applicable).

```scala

resolvers ++= Seq(
  ...
  Resolver.mavenLocal,
  Resolver.jcenterRepo
)

libraryDependencies ++= Seq(
  ...
  aar("com.google.android.gms" % "play-services-maps" % "7.8.0"),
  "de.esotechnik" %% "playservices-scala-maps" % "0.1.2-gms_7.8.0",
  aar("com.google.android.gms" % "play-services-location" % "7.8.0"),
  "de.esotechnik" %% "playservices-scala-location" % "0.1.2-gms_7.8.0",
  ...
)

```

Some of the subprojects don't do anything at all, except providing an empty object. They are only
here for your convenience, if you have some logic that automatically adds the matching subproject
for each `play-services-` split project.

There is also a `playservices-scala` project that aggregates all subprojects, but you probably
don't want to use that or you'll hit the 65,536 method limit very quickly, even when using proguard,
because it not only adds some retained classes itself, but especially because it pulls in all
Play Services modules (which were split exactly for that reason in the first place).


## License ##

Copyright (C) 2015 Bernhard Frauendienst

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.
