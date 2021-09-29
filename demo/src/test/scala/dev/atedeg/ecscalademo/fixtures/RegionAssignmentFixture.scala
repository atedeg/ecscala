package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.{ &:, CNil }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.systems.RegionAssignmentSystem
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

trait RegionAssignmentFixture extends ECScalaDSL with WorldFixture {
  private val color = Color(0, 0, 0)
  val world = World()

  val entity1 = world hasAn entity withComponents {
    Position(0, 0) &: Velocity(0, 0) &: Circle(2, color) &: Mass(1)
  }
  val entity1Components = entity1.getComponent[Position].get
    &: entity1.getComponent[Velocity].get
    &: entity1.getComponent[Circle].get
    &: entity1.getComponent[Mass].get
    &: CNil

  val entity2 = world hasAn entity withComponents {
    Position(19, 19) &: Velocity(0, 0) &: Circle(10, color) &: Mass(1)
  }
  val entity2Components = entity2.getComponent[Position].get
    &: entity2.getComponent[Velocity].get
    &: entity2.getComponent[Circle].get
    &: entity2.getComponent[Mass].get
    &: CNil

  val entity3 = world hasAn entity withComponents {
    Position(20, 20) &: Velocity(0, 0) &: Circle(5, color) &: Mass(1)
  }
  val entity3Components = entity3.getComponent[Position].get
    &: entity3.getComponent[Velocity].get
    &: entity3.getComponent[Circle].get
    &: entity3.getComponent[Mass].get
    &: CNil

  val spacePartition = WritableSpacePartitionContainer()
  val regionAssignmentSystem = RegionAssignmentSystem(spacePartition)
  world hasA system(regionAssignmentSystem)
}
