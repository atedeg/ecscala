package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.systems.{ CollisionSystem, RegionAssignmentSystem }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait CollisionsFixture {
  private val spacePartition = WritableSpacePartitionContainer()
  val world = World()

  world addSystem (new RegionAssignmentSystem(spacePartition))
  world addSystem (new CollisionSystem(spacePartition))
}
