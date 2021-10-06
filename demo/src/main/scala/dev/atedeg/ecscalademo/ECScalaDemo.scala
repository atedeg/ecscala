package dev.atedeg.ecscalademo

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.Includes.*
import scalafx.scene.Scene
import dev.atedeg.ecscala.World

object ECScalaDemo extends JFXApp3 {

  override def start(): Unit = {
    val root: Parent = FXMLLoader.load(getClass.getResource("/MainView.fxml"))

    stage = new JFXApp3.PrimaryStage() {
      title = "ECScala Demo"
      scene = new Scene(root)
    }
    stage.setMinHeight(540)
    stage.setMinWidth(960)
  }
}
