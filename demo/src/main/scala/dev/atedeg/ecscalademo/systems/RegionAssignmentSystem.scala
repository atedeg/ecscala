package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, System, View, World }
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import dev.atedeg.ecscalademo.{ Circle, Position }

class RegionAssignmentSystem(private val regions: WritableSpacePartitionContainer)
    extends System[Position &: Circle &: CNil] {

  override def update(
      entity: Entity,
      components: Position &: Circle &: CNil,
  )(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Circle &: CNil],
  ): Deletable[Position &: Circle &: CNil] = {
    regions add entity
    components
  }

  override def after(deltaTime: DeltaTime, world: World, view: View[Position &: Circle &: CNil]): Unit = regions.build()
}
