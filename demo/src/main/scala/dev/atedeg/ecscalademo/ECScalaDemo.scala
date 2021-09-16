package dev.atedeg.ecscalademo

import javafx.{ fxml => jfxf, scene => jfxs }
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

object ECScalaDemo extends JFXApp3 {

  override def start(): Unit = {

    val root: jfxs.Parent = jfxf.FXMLLoader.load(getClass.getResource("/MainView.fxml"))

    stage = new JFXApp3.PrimaryStage() {
      title = "ECScala Demo"
      scene = new Scene(root)
    }
  }
}
