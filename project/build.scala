import sbt._
import Keys._

object Build extends android.AutoBuild {
  val libraryVersion = settingKey[String]("Library version")
  val playServicesVersion = settingKey[String]("Play Services version")


}

