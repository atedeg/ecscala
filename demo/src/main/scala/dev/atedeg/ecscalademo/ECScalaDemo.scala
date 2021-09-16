package dev.atedeg.ecscalademo

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene

object ECScalaDemo extends JFXApp3 {

  override def start(): Unit = {

    val root: Parent = FXMLLoader.load(getClass.getResource("/MainView.fxml"))

    stage = new JFXApp3.PrimaryStage() {
      title = "ECScala Demo"
      scene = new Scene(root)
    }
  }
}
