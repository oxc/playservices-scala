# Play Services for Scala #

A collection of traits and objects for making
[Google Play Services](https://developers.google.com/android/) more convenient to use when
programming for Android in Scala.

## Disclaimer ##

Please be aware that up until now, this library is untested in large parts. Please report any
issues or improvements using the GitHub [Issues](https://github.com/oxc/playservices-scala/issues)
system.

## Usage ##

### PlayServices trait ###

The central starting point of this library is a trait called `PlayServices`, which you can add on
your `Activity`. It will automatically create a `GoogleApiClient`, which is started `onStart()` and
stopped `onStop()`.

(NOTE: This is only the basics, so please make sure you also read the next sections, so you don't
miss out on the cool stuff! ;))

#### Adding APIs ####

Google Play Services APIs can be added to the `api` object in the class body, in form of a
`ApiDependency` which can be created using one of the implicit conversions available
in `de.esotechnik.playservicesscala`:

```scala
import com.google.android.gms.location.LocationServices
import de.esotechnik.playservicesscala._

class MyActivity extends Activity with PlayServices {
  apis += LocationServices.API

  override onConnected(bundle: Bundle) = {
    val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    Toast.makeText(this, "Last known location: " + lastLocation, Toast.LENGTH_LONG).show()
  }
}
```

##### APIs with Options #####

APIs that require or optionally take options can be added using the implicitly available `%`
function (or `withOptions` if you prefer a more speaking name):

```scala
import com.google.android.gms.games.Games
import com.google.android.gms.games.Games.GamesOptions
import de.esotechnik.playservicesscala._

class MyActivity extends Activity with PlayServices {
  apis += Games.API % new GamesOptions.Builder().setShowConnectingPopup(false).build()

  override onConnected(bundle: Bundle) = {
    val playerName = Games.Players.getCurrentPlayer(googleApiClient).getDisplayName

    Toast.makeText(this, s"Hi $playerName", Toast.LENGTH_LONG).show()
  }
}
```

##### Optional APIs #####

Some APIs, namely the `Wearable` API, are not always available. Those APIs can either be added
using the `ifAvailable` method, or the `apis ?=` mutator:

```scala
import de.esotechnik.playservicesscala._

class MyActivity extends Activity with PlayServices {
  // those two lines are equivalent
  apis += Wearable.API.ifAvailable
  apis ?= Wearable.API

  override onConnected(bundle: Bundle) = {
    val playerName = Players.getCurrentPlayer().getDisplayName

    Toast.makeText(this, s"Hi $playerName", Toast.LENGTH_LONG).show()
  }
}
```

### API wrappers ###

However, more conveniently, you can use the API wrapper objects that are provided for each API.
Using Scala's macro feature, for every API a matching wrapper object is created, that accepts the
`GoogleApiClient` implicitly, which is automatically available if you use the `PlayServices` trait.

Furthermore, every ApiWrapper knows which Api it requires, and there are also implicit conversions
to ApiDependency available.

This lets us re-write above imaginary Games example like this:


```scala
import de.esotechnik.playservicesscala.games.Players

class MyActivity extends Activity with PlayServices {
  apis += Players
  // or with options:
  apis += Players % new GamesOptions.Builder().setShowConnectingPopup(false).build()

  override onConnected(bundle: Bundle) = {
    val playerName = Players.getCurrentPlayer().getDisplayName

    Toast.makeText(this, s"Hi $playerName", Toast.LENGTH_LONG).show()
  }
}
```

#### Future instead of PendingResult ####

Apart from accepting an implicit client, the API wrappers also convert PendingResults into Futures,
which makes them much more comfortable to use:

```scala
import de.esotechnik.playservicesscala.games.GamesMetadata
import scala.collection.JavaConversions._

class MyActivity extends Activity with PlayServices {
  apis += GamesMetadata

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

#### Drive ####

For Google Drive queries, the provided implicit conversions allow you to write such queries in a
much more concise and readable manner:

```scala
import com.google.android.gms.drive.query.SearchableField._
import com.google.android.gms.drive.query.{ SortableField => by }

import de.esotechnik.playservicesscala.drive._

val metaData = for {
  metadataBuffer <- Drive.query(
    TITLE === "Scala is Fun" && IS_PINNED
      sortBy by.TITLE
  )
  buffer = metadataBuffer.getMetadataBuffer
} yield buffer.head
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
  "de.esotechnik" %% "playservices-scala-maps" % "0.1-gms_7.8.0",
  aar("com.google.android.gms" % "play-services-location" % "7.8.0"),
  "de.esotechnik" %% "playservices-scala-location" % "0.1-gms_7.8.0",
  ...
)

```

Some of the subprojects don't do anything at all, except providing an empty object. They are only
here for your convenience, if you have some logic that automatically adds the matching subproject
for each `play-services-` split project.

There is also a `playservices-scala` project that aggregates all subprojects, but you probably
don't want to use that or you'll hit the 65,536 method limit very quickly, even when using proguard.

License
======

Copyright (C) 2015 Bernhard Frauendienst

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions and limitations under the
License.