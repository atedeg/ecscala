package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo.given

trait RegionAssignmentFixture {
  private val color = Color(0, 0, 0)
  val world = World()
  val entity1 = world.createEntity()
  val entity2 = world.createEntity()
  val entity3 = world.createEntity()
  val entity4 = world.createEntity()
  val entity5 = world.createEntity()
  entity1.addComponent(Position(0.0, 0.0))
  entity1.addComponent(Velocity(0.0, 0.0))
  entity1.addComponent(Circle(2, color))
  entity1.addComponent(Mass(1))

  entity2.addComponent(Position(14.0, 14.0))
  entity2.addComponent(Velocity(0.0, 0.0))
  entity2.addComponent(Circle(10, color))
  entity2.addComponent(Mass(1))

  entity3.addComponent(Position(15.0, 15.0))
  entity3.addComponent(Velocity(0.0, 0.0))
  entity3.addComponent(Circle(5, color))
  entity3.addComponent(Mass(1))

  entity4.addComponent(Position(0.0, 0.0))

  entity5.addComponent(Circle(10, color))
}
