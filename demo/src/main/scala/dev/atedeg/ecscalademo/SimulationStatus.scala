package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.Entity

object MouseState {
  var coordinates = Point(0, 0)
  var clicked = false
  var down = false
  var up = false
}

object PlayState {
  var playing = false
  var selectedBall: Option[Entity] = Option.empty
}
