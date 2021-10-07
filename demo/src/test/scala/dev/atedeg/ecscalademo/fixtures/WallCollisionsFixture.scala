package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import org.mockito.Mockito.when
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo.systems.WallCollisionSystem

trait WallCollisionsFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val wallCollisionSystem = WallCollisionSystem(playState, environmentState, canvas)
  world hasA system(wallCollisionSystem)

  val entities = for {
    x <- Seq(-1.0, 50.0, 101.0)
    y <- Seq(-1.0, 50.0, 101.0)
  } yield {
    world hasAn entity withComponents {
      Position(x, y) &: Velocity(1, 1) &: Circle(10, Color(0, 0, 0)) &: Mass(1)
    }
  }

  when(environmentState.frictionCoefficient) thenReturn 0.05
  when(environmentState.wallRestitution) thenReturn 1.0
  when(environmentState.gravity) thenReturn 9.81
}
