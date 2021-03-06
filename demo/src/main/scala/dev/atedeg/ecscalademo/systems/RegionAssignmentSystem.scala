package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, IteratingSystem, View, World }
import dev.atedeg.ecscalademo.{ Circle, Mass, PlayState, Position, State, Velocity }
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

/**
 * This system populates the [[SpacePartitionContainer]] with all the required entities. This system is to be run before
 * any collision system.
 * @param regions
 *   the [[WritableSpacePartitionContainer]] that will be populated.
 */
class RegionAssignmentSystem(private val playState: PlayState, val regions: WritableSpacePartitionContainer)
    extends IteratingSystem[Position &: Velocity &: Circle &: Mass &: CNil] {

  override def shouldRun = playState.gameState == State.Play

  override def before(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: Mass &: CNil],
  ): Unit = regions.clear()

  override def update(
      entity: Entity,
      components: Position &: Velocity &: Circle &: Mass &: CNil,
  )(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: Mass &: CNil],
  ): Deletable[Position &: Velocity &: Circle &: Mass &: CNil] = {
    regions add (entity, components)
    components
  }

  override def after(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: Circle &: Mass &: CNil],
  ): Unit = regions.build()
}
