// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `kartoffel-core` =
  project
    .in(file("./kartoffel-core"))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
    )

lazy val `kartoffel-caffeine` =
  project
    .in(file("./kartoffel-caffeine"))
    .enablePlugins(AutomateHeaderPlugin)
    .dependsOn(`kartoffel-core`, `kartoffel-cats`, `kartoffel-zio`)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
          library.caffeine,
          library.zio        % Test,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
    )

lazy val `kartoffel-redis` =
  project
    .in(file("./kartoffel-redis"))
    .enablePlugins(AutomateHeaderPlugin)
    .dependsOn(`kartoffel-core`, `kartoffel-cats`, `kartoffel-zio`)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
          library.lettuce,
          library.circeCore     % Test,
          library.circeGeneric  % Test,
          library.circeParser   % Test,
          library.scalaCheck    % Test,
          library.scalaTest     % Test,
          library.testContainer % Test,
          library.zio           % Test
        )
    )

lazy val `kartoffel-cats` =
  project
    .in(file("./kartoffel-cats"))
    .enablePlugins(AutomateHeaderPlugin)
    .dependsOn(`kartoffel-core`)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
          library.catsEffect,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
    )

lazy val `kartoffel-zio` =
  project
    .in(file("./kartoffel-zio"))
    .enablePlugins(AutomateHeaderPlugin)
    .dependsOn(`kartoffel-core`)
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
          library.zio,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val caffeine      = "2.5.5"
      val cats          = "2.1.1"
      val circe         = "0.12.3"
      val lettuce       = "6.0.1.RELEASE"
      val scalaCheck    = "1.14.3"
      val scalaTest     = "3.2.2"
      val testContainer = "0.38.8"
      val zio           = "1.0.3"
    }
    val caffeine      = "com.github.ben-manes.caffeine" % "caffeine"                       % Version.caffeine
    val catsEffect    = "org.typelevel"                %% "cats-effect"                    % Version.cats
    val circeCore     = "io.circe"                     %% "circe-core"                     % Version.circe
    val circeGeneric  = "io.circe"                     %% "circe-generic"                  % Version.circe
    val circeParser   = "io.circe"                     %% "circe-parser"                   % Version.circe
    val lettuce       = "io.lettuce"                    % "lettuce-core"                   % Version.lettuce
    val scalaCheck    = "org.scalacheck"               %% "scalacheck"                     % Version.scalaCheck
    val scalaTest     = "org.scalatest"                %% "scalatest"                      % Version.scalaTest
    val testContainer = "com.dimafeng"                 %% "testcontainers-scala-scalatest" % Version.testContainer
    val zio           = "dev.zio"                      %% "zio"                            % Version.zio
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val commonSettings =
  Seq(
    scalaVersion := "2.13.4",
    organization := "codeeng",
    organizationName := "codeeng",
    startYear := Some(2021),
    licenses += ("MIT", url("https://opensource.org/licenses/MIT")),
    scalacOptions ++= Seq(
        "-unchecked",
        "-deprecation",
        "-language:_",
        "-target:jvm-11",
        "-encoding",
        "UTF-8",
        "-Ywarn-unused:imports"
      ),
    scalafmtOnCompile := true
  )
