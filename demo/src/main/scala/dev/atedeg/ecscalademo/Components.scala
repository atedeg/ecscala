package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.Component

case class Color(r: Int, g: Int, b: Int) {
  require(r >= 0 && r <= 255)
  require(g >= 0 && g <= 255)
  require(b >= 0 && b <= 255)
}

case class Position(position: Point) extends Component {
  def this(x: Double, y: Double) = this(Point(x, y))
  def x: Double = position.x
  def y: Double = position.y
}

case class Velocity(velocity: Vector) extends Component {
  def this(x: Double, y: Double) = this(Vector(x, y))
  def x: Double = velocity.x
  def y: Double = velocity.y
}

case class Circle(radius: Double, color: Color) extends Component
case class Mass(mass: Double) extends Component
