package dev.atedeg.ecscalademo.fixtures

import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, Position, Velocity }
import dev.atedeg.ecscalademo.systems.VelocityArrowSystem

trait VelocityArrowSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(10, 10) }
  val arrowSystem = VelocityArrowSystem(playState, mouseState, canvas)
  world hasA system(arrowSystem)
}
