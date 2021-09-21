import sbtghactions.GenerativePlugin.autoImport.{ githubWorkflowPublishPreamble, WorkflowStep }

val scala3Version = "3.0.1"

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "dev.atedeg"
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))

ThisBuild / scalaVersion := scala3Version

ThisBuild / developers := List(
  Developer(
    "giacomocavalieri",
    "Giacomo Cavalieri",
    "giacomo.cavalieri@icloud.com",
    url("https://github.com/giacomocavalieri"),
  ),
  Developer(
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@gmail.com",
    url("https://github.com/nicolasfara"),
  ),
  Developer(
    "ndido98",
    "Nicolò Di Domenico",
    "ndido98@gmail.com",
    url("https://github.com/ndido98"),
  ),
  Developer(
    "vitlinda",
    "Linda Vitali",
    "lindav94vitali@gmail.com",
    url("https://github.com/vitlinda"),
  ),
)

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / githubWorkflowScalaVersions := Seq("3.0.1")
ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11", "adopt@1.16")
ThisBuild / githubWorkflowTargetBranches := Seq("master", "develop")
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("scalafmtCheck"), name = Some("Lint check with scalafmt")),
  WorkflowStep.Sbt(List("core / test"), name = Some("Tests")),
  WorkflowStep.Sbt(
    List("core / jacoco"),
    name = Some("Generate JaCoCo report"),
  ),
  WorkflowStep.Use(
    "codecov",
    "codecov-action",
    "v2",
    name = Some("Publish coverage to codecov"),
    params = Map(
      "token" -> "${{ secrets.CODECOV_TOKEN }}",
      "directory" -> s"core/target/scala-$scala3Version/jacoco/report",
      "fail_ci_if_error" -> "true",
    ),
  ),
  WorkflowStep.Use(
    "xu-cheng",
    "latex-action",
    "v2",
    name = Some("Build LaTeX report"),
    params = Map(
      "root_file" -> "ecscala-report.tex",
      "args" -> "-output-format=pdf -file-line-error -synctex=1 -halt-on-error -interaction=nonstopmode -shell-escape",
      "working_directory" -> "doc",
    ),
  ),
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Use(
    "xu-cheng",
    "latex-action",
    "v2",
    name = Some("Build LaTeX report"),
    params = Map(
      "root_file" -> "ecscala-report.tex",
      "args" -> "-output-format=pdf -file-line-error -synctex=1 -halt-on-error -interaction=nonstopmode -shell-escape",
      "working_directory" -> "doc",
    ),
  ),
  WorkflowStep.Sbt(
    List("ci-release"),
    name = Some("Release to Sonatype"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_CLEAN" -> "sonatypeBundleClean",
    ),
  ),
  WorkflowStep.Use(
    "marvinpinto",
    "action-automatic-releases",
    "latest",
    name = Some("Release to Github Releases"),
    params = Map(
      "repo_token" -> "${{ secrets.GITHUB_TOKEN }}",
      "prerelease" -> "${{ env.IS_SNAPSHOT }}",
      "title" -> """Release - Version ${{ env.VERSION }}""",
      "files" -> s"core/target/scala-$scala3Version/*.jar\ncore/target/scala-$scala3Version/*.pom\ndoc/ecscala-report.pdf",
    ),
  ),
)

lazy val root = project
  .in(file("."))
  .aggregate(core, benchmarks)
  .settings(
    name := "ecscala",
    publish / skip := true,
  )

lazy val core = project
  .in(file("core"))
  .settings(
    name := "ecscala",
    libraryDependencies := Seq(
      "org.scalactic" %% "scalactic" % "3.2.9",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    ),
    scalacOptions ++= Seq(
      "-Yexplicit-nulls",
    ),
    jacocoReportSettings := JacocoReportSettings(
      title = "Jaoco coverage report",
      subDirectory = None,
      thresholds = JacocoThresholds(),
      formats = Seq(JacocoReportFormats.HTML, JacocoReportFormats.XML),
      fileEncoding = "utf-8",
    ),
    Test / jacocoExcludes := Seq("**.macros.*", "**.types.*"),
  )

lazy val benchmarks = project
  .in(file("benchmarks"))
  .dependsOn(core)
  .enablePlugins(JmhPlugin)
  .settings(
    publish / skip := true,
    test / skip := true,
    githubWorkflowArtifactUpload := false,
  )
