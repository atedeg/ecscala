package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.Component
import dev.atedeg.ecscala.util.immutable.ComponentsContainer

case class Mass(m: Int) extends Component
case class Position(x: Int, y: Int) extends Component
case class Velocity(vx: Int, vy: Int) extends Component
case class Gravity(g: Int) extends Component

trait ComponentsFixture {
  var componentsContainer: ComponentsContainer = ComponentsContainer()
}
