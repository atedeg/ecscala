package dev.atedeg.ecscalademo.fixtures

import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, Position }
import dev.atedeg.ecscalademo.systems.RenderSystem

trait RenderSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val position = Position(0, 0)
  val circle = Circle(20, Color(255, 0, 0))
  val ball = world hasAn entity withComponents { circle &: position }
  val renderSystem = RenderSystem(playState, canvas)
  world hasA system(renderSystem)
}
