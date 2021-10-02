package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.Entity
import scalafx.beans.property.DoubleProperty

enum State {
  case Pause, Play, AddBalls, SelectBall, ChangeVelocity, Dragging
}

trait MouseState {
  var coordinates = Point(0, 0)
  var clicked = false
  var down = false
  var up = false
}

object MouseState {
  def apply(
    coordinates: Point = Point(0, 0),
    clicked: Boolean = false,
    down: Boolean = false,
    up: Boolean = false
  ): MouseState = {
    val mouseState = new MouseState {}
    mouseState.clicked = clicked
    mouseState.down = down
    mouseState.up = up
    mouseState
  }
}

trait PlayState {
  var gameState: State = State.Pause
  var selectedBall: Option[Entity] = Option.empty
}

object PlayState {
  def apply(): PlayState = new PlayState {}
}

trait StartingState {
  val startingRadius: Double = 20.0
  val startingColor: Color = Color(255, 255, 0)
  val startingMass: Double = 1
  val startingVelocity: Vector = Vector(0.0, 0.0)
  val startingPosition: Seq[Position]

  val startingVelocities = List(
    Velocity(1000, 0),
    Velocity(0, 0),
    Velocity(0, 0),
    Velocity(0, 0),
    Velocity(0, 0),
    Velocity(0, 0),
    Velocity(0, 0),
  )

  val startingColors = List(
    Color(255, 255, 255),
    Color(255, 215, 0),
    Color(0, 0, 255),
    Color(255, 0, 0),
    Color(75, 0, 130),
    Color(255, 69, 0),
    Color(34, 139, 34),
  )
}

object StartingState {

  def apply(canvas: ECSCanvas): StartingState = new StartingState {

    override val startingPosition = Seq(
      Position(canvas.width / 3, canvas.height / 2),
      Position(0.66 * canvas.width, canvas.height / 2),
      Position(0.66 * canvas.width, canvas.height / 2 - (2 * startingRadius + 4)),
      Position(0.66 * canvas.width, canvas.height / 2 + (2 * startingRadius + 4)),
      Position(0.66 * canvas.width - 2 * startingRadius, canvas.height / 2 - (startingRadius + 2)),
      Position(0.66 * canvas.width - 2 * startingRadius, canvas.height / 2 + (startingRadius + 2)),
      Position(0.66 * canvas.width - 4 * startingRadius, canvas.height / 2),
    )
  }
}

trait EnvironmentState {
  def frictionCoefficient: Double
  def wallRestitution: Double
  val gravity: Double = 9.81
}

object EnvironmentState {

  def apply(frictionCoefficentProperty: DoubleProperty, wallRestitutionProperty: DoubleProperty): EnvironmentState =
    new EnvironmentState {
      override def frictionCoefficient: Double = frictionCoefficentProperty.value

      override def wallRestitution: Double = wallRestitutionProperty.value
    }
}
