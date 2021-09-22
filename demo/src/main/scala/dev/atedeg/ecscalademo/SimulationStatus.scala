package dev.atedeg.ecscalademo

object MouseState {
  var coordinates = Coordinates(0, 0)
  var clicked = false
  var down = false
  var up = false
}

object PlayState {
  var playing = false
  var selectedBall = Option.empty
}
