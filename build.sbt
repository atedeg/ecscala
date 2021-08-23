import sbtghactions.GenerativePlugin.autoImport.{ githubWorkflowPublishPreamble, WorkflowStep }

val scala3Version = "3.0.1"

inThisBuild(
  List(
    organization := "dev.atedeg.ecscala",
    licenses := List("MIT" -> url("https://opensource.org/licenses/MIT")),
    developers := List(
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
    ),
    scalaVersion := scala3Version,
    githubWorkflowScalaVersions := Seq("3.0.1"),
    githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11", "adopt@1.16"),
    githubWorkflowTargetBranches := Seq("master", "develop"),
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
    githubWorkflowBuild := Seq(
      WorkflowStep.Sbt(List("scalafmtCheck"), name = Some("Lint check with scalafmt")),
      WorkflowStep.Sbt(List("test"), name = Some("Tests")),
      WorkflowStep.Sbt(
        List("jacoco"),
        name = Some("Generate JaCoCo report"),
      ),
      WorkflowStep.Use(
        "codecov",
        "codecov-action",
        "v2",
        name = Some("Publish coverage to codecov"),
        params = Map(
          "token" -> "${{ secrets.CODECOV_TOKEN }}",
          "directory" -> s"target/scala-$scala3Version/jacoco/report",
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
    ),
    githubWorkflowPublishPreamble ++= Seq(
      WorkflowStep.Run(
        List(
          """VERSION=`sbt -Dsbt.ci=true 'inspect actual version' | grep "Setting: java.lang.String" | cut -d '=' -f2 | tr -d ' '`""",
          """echo "VERSION=${VERSION}" >> $GITHUB_ENV""",
          """IS_SNAPSHOT=`if [[ "${VERSION}" =~ "-" ]] ; then echo "true" ; else echo "false" ; fi`""",
          """echo "IS_SNAPSHOT=${IS_SNAPSHOT}" >> $GITHUB_ENV""",
        ),
        name = Some("Setup environment variables"),
      ),
    ),
    githubWorkflowPublish := Seq(
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
          "files" -> s"target/scala-$scala3Version/*.jar\ntarget/scala-$scala3Version/*.pom\ndoc/ecscala-report.pdf",
        ),
      ),
    ),
    scalacOptions ++= Seq(
      "-Yexplicit-nulls",
    ),
    libraryDependencies := Seq(
      "org.scalactic" %% "scalactic" % "3.2.9",
      "org.scalatest" %% "scalatest" % "3.2.9" % "test",
    ),
  ),
)

ThisProject / name := "ecscala"

ThisProject / jacocoReportSettings := JacocoReportSettings(
  title = "Jaoco coverage report",
  subDirectory = None,
  thresholds = JacocoThresholds(),
  formats = Seq(JacocoReportFormats.HTML, JacocoReportFormats.XML),
  fileEncoding = "utf-8",
)
ThisProject / jacocoExcludes := Seq("**.macros.*")
