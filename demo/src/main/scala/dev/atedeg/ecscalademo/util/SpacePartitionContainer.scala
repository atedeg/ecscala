package dev.atedeg.ecscalademo.util

import scala.collection
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.Entity
import dev.atedeg.ecscalademo.{ Circle, Mass, Position, Velocity }

/**
 * This trait represents a read-only version of a space partition container that assigns each added entity to a region
 * according to its position.
 */
trait SpacePartitionContainer extends Iterable[((Int, Int), Seq[Entity])] {

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
  def get(region: (Int, Int)): Seq[Entity]

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

  /**
   * Add an entity to this space partition container. The entity must have the [[Position]], [[Velocity]], [[Mass]] and
   * [[Circle]] components.
   * @param entity
   *   the entity to be added.
   */
  def add(entity: Entity): Unit

  /**
   * Build the space partition container so that it can be used by other systems.
   */
  def build(): Unit
}

object WritableSpacePartitionContainer {
  def apply(): WritableSpacePartitionContainer = new WritableSpacePartitionContainerImpl()

  private class WritableSpacePartitionContainerImpl() extends WritableSpacePartitionContainer {
    private var regions: Map[(Int, Int), Seq[Entity]] = Map()
    private var entities: Seq[Entity] = List()
    private var _regionSize: Double = 0
    private val regionSizeMultiplier = 2

    override def regionSize: Double = _regionSize

    override def add(entity: Entity): Unit = {
      val position = entity.getComponent[Position]
      val velocity = entity.getComponent[Velocity]
      val circle = entity.getComponent[Circle]
      val mass = entity.getComponent[Mass]
      require(
        position.isDefined && velocity.isDefined && circle.isDefined && mass.isDefined,
      )
      entities :+= entity
      _regionSize = math.max(_regionSize * regionSizeMultiplier, circle.get.radius)
    }

    override def build(): Unit = {
      regions = entities.foldLeft(Map[(Int, Int), Seq[Entity]]()) { (acc, elem) =>
        val position = elem.getComponent[Position].get
        val region = getRegionFromPosition(position)
        val regionEntities = acc get region map (_ :+ elem) getOrElse List(elem)
        acc + (region -> regionEntities)
      }
    }

    override def get(region: (Int, Int)): Seq[Entity] = regions getOrElse (region, Seq())

    override def iterator: Iterator[((Int, Int), Seq[Entity])] = regions.iterator

    override def regionsIterator: Iterator[(Int, Int)] = regions.keysIterator

    private def getRegionFromPosition(positionComponent: Position): (Int, Int) =
      (
        math.floor(positionComponent.position.x / _regionSize).toInt,
        math.floor(positionComponent.position.y / _regionSize).toInt,
      )
  }
}
