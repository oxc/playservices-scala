import Build._

libraryVersion in ThisBuild := "0.1.1"
playServicesVersion in ThisBuild := "7.8.0"
isSnapshot := true

scalaVersion in ThisBuild := "2.11.7"

organization in ThisBuild := "de.esotechnik"
homepage in ThisBuild := Some(url("https://github.com/oxc/playservices-scala"))
licenses in ThisBuild += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
organizationHomepage in ThisBuild := None

version in ThisBuild := libraryVersion.value + "-gms_" + playServicesVersion.value + (if (isSnapshot.value) "-SNAPSHOT" else "")

bintrayReleaseOnPublish in ThisBuild := false

val packageName = "playservices-scala"

lazy val root = (project in file(".")).
  settings(
    name := packageName,
    artifacts := Seq()
  ).
  aggregate(allModules.map(p => p : ProjectReference): _*).
  dependsOn(allModules.map(p => p : ClasspathDep[ProjectReference]): _*)

def commonSettings(project : Project) = {
  val module = project.id

  project.
    settings(android.Plugin.androidBuildJar).
    settings(
      name := s"$packageName-$module",

      bintrayPackage := packageName,

      libraryProject in Android := true,
      transitiveAndroidLibs in Android := false,

      platformTarget in Android := "android-23",
      minSdkVersion in Android := "4",

      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "com.google.android.gms" % s"play-services-base" % playServicesVersion.value
      ),

      addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
    )
}

def asSubProject(project : Project) = {
  val module = project.id

  (project in file(s"modules/$module")).configure(commonSettings).
    dependsOn(core).
    settings(
      libraryDependencies ++= Seq(
        "com.google.android.gms" % s"play-services-${module}" % playServicesVersion.value
      )
    )
}

lazy val core = (project configure commonSettings).
  settings(
  )

lazy val allModules = Seq(core, plus, identity, base, appindexing, appinvite, analytics, cast, gcm,
  drive, fitness, location, maps, ads, vision, nearby, panorama, games, safetynet, wallet, wearable)

lazy val plus = project configure asSubProject
lazy val identity = project configure asSubProject
lazy val base = project configure asSubProject
lazy val appindexing = project configure asSubProject
lazy val appinvite = project configure asSubProject
lazy val analytics = project configure asSubProject
lazy val cast = project configure asSubProject
lazy val gcm = project configure asSubProject
lazy val drive = project configure asSubProject
lazy val fitness = project configure asSubProject
lazy val location = project configure asSubProject
lazy val maps = project configure asSubProject
lazy val ads = project configure asSubProject
lazy val vision = project configure asSubProject
lazy val nearby = project configure asSubProject
lazy val panorama = project configure asSubProject
lazy val games = project configure asSubProject
lazy val safetynet = project configure asSubProject
lazy val wallet = project configure asSubProject
lazy val wearable = project configure asSubProject
