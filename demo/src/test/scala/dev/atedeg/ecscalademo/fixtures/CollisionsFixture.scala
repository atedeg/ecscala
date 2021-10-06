package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.{ CollisionSystem, RegionAssignmentSystem }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait CollisionsFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  private val spacePartition = WritableSpacePartitionContainer()
  val regionAssignmentSystem = RegionAssignmentSystem(playState, spacePartition)
  val collisionSystem = CollisionSystem(playState, spacePartition)

  world hasA system(regionAssignmentSystem)
  world hasA system(collisionSystem)
}
