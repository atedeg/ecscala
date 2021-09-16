package dev.atedeg.ecscalademo.controller

import java.net.URL
import java.util.ResourceBundle
import javafx.scene.{ control as jfxsc, layout as jfxsl }
import javafx.{ event as jfxe, fxml as jfxf }
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane

class MainViewController extends jfxf.Initializable {

  @jfxf.FXML
  private var playPauseBtn: Button = _

  @jfxf.FXML
  private var addBallBtn: Button = _

  @jfxf.FXML
  private var selectBallBtn: Button = _

  @jfxf.FXML
  private var moveBtn: Button = _

  @jfxf.FXML
  private var changeVelBtn: Button = _

  @jfxf.FXML
  private var canvas: jfxsl.Pane = _
  private var cnvs: Pane = _

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    cnvs = new Pane(canvas)
  }
}
