package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, PlayState, Position, Velocity }
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.systems.{ CollisionSystem, RegionAssignmentSystem }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait CollisionsFixture extends WorldFixture with WorldStateFixture {
  private val spacePartition = WritableSpacePartitionContainer()

  world addSystem (new RegionAssignmentSystem(spacePartition))
  world addSystem (new CollisionSystem(playState, spacePartition))
}
