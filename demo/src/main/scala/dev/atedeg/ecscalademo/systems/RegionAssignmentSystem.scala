package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, System, View, World }
import dev.atedeg.ecscala
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import dev.atedeg.ecscalademo.{ Circle, Mass, Position, Velocity }

/**
 * This system populates the [[SpacePartitionContainer]] with all the required entities. This system is to be run before
 * any collision system.
 * @param regions
 *   the [[WritableSpacePartitionContainer]] that will be populated.
 */
class RegionAssignmentSystem(private val regions: WritableSpacePartitionContainer)
    extends System[Position &: Velocity &: Circle &: Mass &: CNil] {

  override def update(
      entity: Entity,
      components: Position &: Velocity &: Circle &: Mass &: CNil,
  )(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: Mass &: CNil],
  ): Deletable[Position &: Velocity &: Circle &: Mass &: CNil] = {
    regions add entity
    components
  }

  override def after(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: Mass &: CNil],
  ): Unit = regions.build()
}
