package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.systems.RegionAssignmentSystem
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait RegionAssignmentFixture extends ECScalaDSL with WorldFixture {
  private val color = Color(0, 0, 0)
  val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(0, 0) &: Circle(2, color) &: Mass(1) }
  val entity2 = world hasAn entity withComponents { Position(19, 19) &: Velocity(0, 0) &: Circle(10, color) &: Mass(1) }
  val entity3 = world hasAn entity withComponents { Position(20, 20) &: Velocity(0, 0) &: Circle(5, color) &: Mass(1) }
  val entity4 = world hasAn entity withComponents { Position(0, 0) }
  val entity5 = world hasAn entity withComponents { Circle(10, color) }
  val spacePartition = WritableSpacePartitionContainer()
  val regionAssignmentSystem = RegionAssignmentSystem(spacePartition)
  world hasA system(regionAssignmentSystem)
}
