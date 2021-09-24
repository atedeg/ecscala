package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{
  Circle,
  Color,
  Mass,
  MouseState,
  PlayState,
  Point,
  Position,
  StartingState,
  Vector,
  Velocity,
}

class BallCreationSystem extends EmptySystem with ECScalaDSL {

  override def shouldRun: Boolean = MouseState.clicked && !PlayState.playing

  override def update(deltaTime: DeltaTime, world: World): Unit = {
    val canBeCreated = world.getView[Position &: Circle &: CNil] map (_._2) forall { cl =>
      val Position(point) &: Circle(radius, _) &: CNil = cl
      !isOverlapped(point, radius)
    }
    if (canBeCreated) {
      world hasAn entity withComponents {
        Position(MouseState.coordinates) &:
          Circle(StartingState.startingRadius, StartingState.startingColor) &:
          Velocity(Vector(0, 0)) &:
          Mass(StartingState.startingMass)
      }
    }
  }

  private def isOverlapped(point: Point, radius: Double): Boolean =
    (point - MouseState.coordinates).norm < (radius + StartingState.startingRadius)
}
