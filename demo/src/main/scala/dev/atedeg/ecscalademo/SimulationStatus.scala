package dev.atedeg.ecscalademo

object MouseState {
  var coordinates = Coordinates(0, 0)
  var clicked = false
  var down = false
  var up = false
}

object PlayState {
  var playing = false
  var lastBallAddedTimeStamp = 0
  var selectedBall = Option.empty
}

case class Coordinates(x: Double, y: Double)
