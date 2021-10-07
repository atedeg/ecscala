package dev.atedeg.ecscalademo.util

import scala.collection.mutable
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Entity }
import dev.atedeg.ecscalademo.{ Circle, Mass, Position, Velocity }

/**
 * This trait represents a read-only version of a space partition container that assigns each added entity to a region
 * according to its position.
 */
trait SpacePartitionContainer extends Iterable[((Int, Int), Iterable[Entity])] {

  /**
   * Get the size of each region.
   * @return
   *   the size of each region.
   */
  def regionSize: Double

  /**
   * Get all entities that belong to the specified region, if present.
   * @param region
   *   the specified region.
   * @return
   *   the entities that belong the the specified region.
   */
  def get(region: (Int, Int)): Iterable[Entity]

  /**
   * Return an iterator that iterates over the non-empty regions.
   * @return
   *   the iterator.
   */
  def regionsIterator: Iterator[(Int, Int)]
}

/**
 * This trait represents a writable version of the space partition container.
 */
trait WritableSpacePartitionContainer extends SpacePartitionContainer {

  type SpacePartitionComponents = Position &: Velocity &: Circle &: Mass &: CNil

  /**
   * Add an entity to this space partition container. The entity must have the [[Position]], [[Velocity]], [[Mass]] and
   * [[Circle]] components.
   * @param entity
   *   the entity to be added.
   */
  def add(entityComponentsPair: (Entity, SpacePartitionComponents)): Unit

  /**
   * Build the space partition container so that it can be used by other systems.
   */
  def build(): Unit

  /**
   * Clear the contents of the space partition container.
   */
  def clear(): Unit
}

object WritableSpacePartitionContainer {
  def apply(): WritableSpacePartitionContainer = new WritableSpacePartitionContainerImpl()

  private class WritableSpacePartitionContainerImpl() extends WritableSpacePartitionContainer {
    private val regions: mutable.Map[(Int, Int), mutable.Set[Entity]] = mutable.Map.empty
    private val entities: mutable.Map[Entity, SpacePartitionComponents] = mutable.Map.empty
    private var _regionSize: Double = 0
    private val regionSizeMultiplier = 2

    override def regionSize: Double = _regionSize

    override def add(entityComponentsPair: (Entity, SpacePartitionComponents)): Unit = {
      val (_, components) = entityComponentsPair
      val _ &: _ &: Circle(radius, _) &: _ &: CNil = components
      entities += entityComponentsPair
      _regionSize = math.max(_regionSize, radius * regionSizeMultiplier)
    }

    override def build(): Unit = {
      regions.clear()
      for ((entity, components) <- entities) {
        val position &: _ &: Circle(_, color) &: _ &: CNil = components
        val region = getRegionFromPosition(position)
        val regionEntities = regions get region
        val regionNewEntities = regionEntities match {
          case Some(entitiesInRegion) => entitiesInRegion += entity
          case None => mutable.Set(entity)
        }
        regions += region -> regionNewEntities
      }
    }

    override def clear(): Unit = {
      regions.clear()
      entities.clear()
      _regionSize = 0
    }

    override def get(region: (Int, Int)): Iterable[Entity] = regions getOrElse (region, mutable.Set())

    override def iterator: Iterator[((Int, Int), Iterable[Entity])] = regions.iterator

    override def regionsIterator: Iterator[(Int, Int)] = regions.keysIterator

    private def getRegionFromPosition(positionComponent: Position): (Int, Int) =
      (
        math.floor(positionComponent.position.x / _regionSize).toInt,
        math.floor(positionComponent.position.y / _regionSize).toInt,
      )
  }
}
