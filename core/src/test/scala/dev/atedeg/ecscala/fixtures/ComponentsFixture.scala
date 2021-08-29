package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.Component
import dev.atedeg.ecscala.util.immutable.ComponentsContainer

case class Mass(m: Double) extends Component
case class Position(x: Double, y: Double) extends Component
case class Velocity(vx: Double, vy: Double) extends Component
case class Gravity(g: Double) extends Component

trait ComponentsFixture {
  var componentsContainer: ComponentsContainer = ComponentsContainer()
}
