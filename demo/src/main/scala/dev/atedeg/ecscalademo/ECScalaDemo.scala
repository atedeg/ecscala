package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.World
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import dev.atedeg.ecscalademo.controller.MainViewController
import javafx.scene.layout.VBox

object ECScalaDemo extends JFXApp3 {

  override def start(): Unit = {
    val world = World()
    val mainViewController = new MainViewController(world)

    val loader: FXMLLoader = FXMLLoader(getClass.getResource("/MainView.fxml"))
    loader.setController(mainViewController)

    stage = new JFXApp3.PrimaryStage() {
      title = "ECScala Demo"
      scene = new Scene(loader.load[VBox]())
    }
    stage.setMinHeight(540)
    stage.setMinWidth(960)
  }
}
