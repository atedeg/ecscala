package dev.atedeg.ecscalademo

import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.paint.Color.Green

object ECScalaDemo extends JFXApp3 {

  override def start(): Unit = {
    stage = new JFXApp3.PrimaryStage {
      title = "ECScala Demo"
      width = 1920 / 2
      height = 1080 / 2
      scene = new Scene {
        fill = Green
      }
    }
  }
}
