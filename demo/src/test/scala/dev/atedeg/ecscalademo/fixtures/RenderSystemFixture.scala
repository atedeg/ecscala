package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, ECSCanvas, Position }
import dev.atedeg.ecscalademo.systems.RenderSystem
import org.scalatestplus.mockito.MockitoSugar.mock

trait RenderSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val position = Position(0, 0)
  val circle = Circle(20, Color(255, 0, 0))
  val ball = world hasAn entity withComponents { circle &: position }
  val renderSystem = RenderSystem(playState, canvas)
  world hasA system(renderSystem)
}
