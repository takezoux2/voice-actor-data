
lazy val commonSettings = Seq(
  organization := "com.takezoux2",
  version := "0.1.0",
  scalaVersion := "2.11.8"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "wiki-actor"
  ).aggregate(library).dependsOn(library)

lazy val library = (project in file("library")).
  settings(commonSettings: _*).
  settings(
    name := "wiki-actor-lib",
    libraryDependencies ++= Seq(
      "org.scalikejdbc" %% "scalikejdbc"       % "2.3.5",
      "ch.qos.logback"  %  "logback-classic"   % "1.1.3",
      "mysql" % "mysql-connector-java" % "5.1.38" ,
      "org.scalatest" % "scalatest_2.11" % "2.2.6" % "test"
    )
  )

