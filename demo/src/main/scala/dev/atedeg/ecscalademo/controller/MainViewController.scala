package dev.atedeg.ecscalademo.controller

import javafx.fxml.{ FXML, Initializable }
import javafx.scene.layout as jfx
import scalafx.scene.control.Button
import scalafx.scene.layout.Pane

import java.net.URL
import java.util.ResourceBundle

class MainViewController extends Initializable {

  @FXML
  private var playPauseBtn: Button = _

  @FXML
  private var addBallBtn: Button = _

  @FXML
  private var selectBallBtn: Button = _

  @FXML
  private var moveBtn: Button = _

  @FXML
  private var changeVelBtn: Button = _

  @FXML
  private var canvas: jfx.Pane = _
  private var cnvs: Pane = _

  override def initialize(url: URL, resourceBundle: ResourceBundle): Unit = {
    cnvs = new Pane(canvas)
  }
}
