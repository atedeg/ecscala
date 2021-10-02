package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.VelocityArrowSystem
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, Position, Velocity }
import org.scalatestplus.mockito.MockitoSugar.mock

trait VelocityArrowSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(10, 10) }
  val arrowSystem = VelocityArrowSystem(playState, mouseState, canvas)
  world hasA system(arrowSystem)
}
