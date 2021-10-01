package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, EnvironmentState, Mass, PlayState, Position, Velocity }
import dev.atedeg.ecscalademo
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import scalafx.beans.property.DoubleProperty

trait WallCollisionsFixture extends ECScalaDSL {
  val world = World()
  val playState = PlayState()
  val environmentState = mock[EnvironmentState]

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
