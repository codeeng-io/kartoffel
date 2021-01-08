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
      val caffeine   = "2.5.5"
      val cats       = "2.1.1"
      val scalaCheck = "1.14.3"
      val scalaTest  = "3.2.2"
      val zio        = "1.0.3"
    }
    val caffeine   = "com.github.ben-manes.caffeine" % "caffeine"    % Version.caffeine
    val catsEffect = "org.typelevel"                %% "cats-effect" % Version.cats
    val scalaCheck = "org.scalacheck"               %% "scalacheck"  % Version.scalaCheck
    val scalaTest  = "org.scalatest"                %% "scalatest"   % Version.scalaTest
    val zio        = "dev.zio"                      %% "zio"         % Version.zio
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
        "-encoding",
        "UTF-8",
        "-Ywarn-unused:imports"
      ),
    scalafmtOnCompile := true
  )
