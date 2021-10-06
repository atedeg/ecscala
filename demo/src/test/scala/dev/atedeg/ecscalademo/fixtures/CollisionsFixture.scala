package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, PlayState, Position, Velocity }
import dev.atedeg.ecscalademo.systems.{ CollisionSystem, RegionAssignmentSystem }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait CollisionsFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  private val spacePartition = WritableSpacePartitionContainer()
  val regionAssignmentSystem = RegionAssignmentSystem(playState, spacePartition)
  val collisionSystem = CollisionSystem(playState, spacePartition)

  world hasA system(regionAssignmentSystem)
  world hasA system(collisionSystem)
}
