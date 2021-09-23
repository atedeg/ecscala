package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.Component
import dev.atedeg.ecscalademo.{ Point, Vector }

case class Color(r: Int, g: Int, b: Int) {
  require(r >= 0 && r <= 255)
  require(g >= 0 && g <= 255)
  require(b >= 0 && b <= 255)
}

case class Position(position: Point) extends Component {
  def x: Double = position.x
  def y: Double = position.y
}

object Position {
  def apply(x: Double, y: Double): Position = Position(Point(x, y))
}

given Conversion[Position, Point] = _.position

case class Velocity(velocity: Vector) extends Component {
  def x: Double = velocity.x
  def y: Double = velocity.y
}

object Velocity {
  def apply(x: Double, y: Double): Velocity = Velocity(Vector(x, y))
}

given Conversion[Velocity, Vector] = _.velocity

case class Circle(radius: Double, color: Color) extends Component
case class Mass(mass: Double) extends Component
