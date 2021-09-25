package dev.atedeg.ecscalademo.util

import scala.collection
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.Entity
import dev.atedeg.ecscalademo.{ Circle, Mass, Position, Velocity }

trait SpacePartitionContainer extends Iterable[((Int, Int), Seq[Entity])] {
  def regionSize: Double
  def get(region: (Int, Int)): Seq[Entity]
  def regionsIterator: Iterator[(Int, Int)]
}

trait WritableSpacePartitionContainer extends SpacePartitionContainer {
  def add(entity: Entity): Unit
  def build(): Unit
}

object WritableSpacePartitionContainer {
  def apply(): WritableSpacePartitionContainer = new WritableSpacePartitionContainerImpl()

  private class WritableSpacePartitionContainerImpl() extends WritableSpacePartitionContainer {
    private var regions: Map[(Int, Int), Seq[Entity]] = Map()
    private var entities: Seq[Entity] = List()
    private var _regionSize = 0.0d
    private val regionSizeMultiplier = 1.5

    override def regionSize: Double = _regionSize

    override def add(entity: Entity): Unit = {
      val positionComponent = entity.getComponent[Position]
      val velocityComponent = entity.getComponent[Velocity]
      val circleComponent = entity.getComponent[Circle]
      val massComponent = entity.getComponent[Mass]
      require(
        positionComponent.isDefined && velocityComponent.isDefined && circleComponent.isDefined && massComponent.isDefined,
      )
      entities :+= entity
      _regionSize = math.max(_regionSize * regionSizeMultiplier, circleComponent.get.radius)
    }

    override def build(): Unit = {
      regions = entities.foldLeft(Map[(Int, Int), Seq[Entity]]()) { (acc, elem) =>
        val positionComponent = elem.getComponent[Position].get
        val region = getRegionFromPosition(positionComponent)
        acc + (region -> (acc get region map (_ :+ elem) getOrElse List(elem)))
      }
    }

    override def get(region: (Int, Int)): Seq[Entity] = regions.getOrElse(region, Seq())

    override def iterator: Iterator[((Int, Int), Seq[Entity])] = regions.iterator

    override def regionsIterator: Iterator[(Int, Int)] = regions.keysIterator

    private def getRegionFromPosition(positionComponent: Position): (Int, Int) =
      (
        math.floor(positionComponent.position.x / _regionSize).toInt,
        math.floor(positionComponent.position.y / _regionSize).toInt,
      )
  }
}
