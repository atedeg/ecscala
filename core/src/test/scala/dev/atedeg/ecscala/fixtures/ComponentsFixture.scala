package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.Component
import dev.atedeg.ecscala.util.immutable.ComponentsContainer

trait ComponentsFixture {
  case class Position(x: Double, y: Double) extends Component
  case class Velocity(vx: Double, vy: Double) extends Component
  var componentsContainer: ComponentsContainer = ComponentsContainer()
}
