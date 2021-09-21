package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.Component

case class Position(position: Point) extends Component
case class Velocity(velocity: Vector) extends Component
case class Circle(radius: Double, color: Color) extends Component
case class Mass(mass: Double) extends Component
