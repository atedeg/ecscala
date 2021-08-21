package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.Component

trait ComponentsFixture {
  case class Position(x: Double, y: Double) extends Component
  case class Velocity(vx: Double, vy: Double) extends Component
}
