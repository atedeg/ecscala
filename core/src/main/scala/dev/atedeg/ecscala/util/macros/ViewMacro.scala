package dev.atedeg.ecscala.util.macros

import dev.atedeg.ecscala.util.types
import dev.atedeg.ecscala.{ &:, CList, CNil, Component, Entity, View, World }

object ViewMacro {
  import dev.atedeg.ecscala.util.types.given
  case class Position(x: Int) extends Component

  inline private[ecscala] def createViewImpl[L <: CList](world: World): View[L] = {
    new View[L] {
      override def iterator = new Iterator[(Entity, L)] {
        val maps: Seq[Map[Entity, Component]] = getMaps[L](world) sortBy (_.size)
        var smallerMap: Option[Map[Entity, Component]] = maps.headOption
        val otherMaps: Seq[Map[Entity, Component]] = if !maps.isEmpty then maps.tail else maps
        var current: Option[Entity] = smallerMap flatMap (_.headOption) map (_.head)

        override def hasNext = current.isDefined && otherMaps.forall(_.contains(current.get))

        override def next() = {
          val result = current
          smallerMap = smallerMap map (_ - current.get)
          current = smallerMap flatMap (_.headOption) map (_.head)
          (result.get, getEntityComponents[L](world)(result.get))
        }
      }
    }
  }

  inline private def getEntityComponents[L <: CList](world: World)(entity: Entity): L = {
    import scala.compiletime.*
    (inline erasedValue[L] match {
      case _: (head &: tail) =>
        world.getComponents[head].get(entity) &: getEntityComponents[tail](world)(entity)
      case _ => CNil
    }).asInstanceOf[L]
  }

  inline private def getMaps[L <: CList](world: World): Seq[Map[Entity, Component]] = {
    import scala.compiletime.*
    inline erasedValue[L] match {
      case _: (head &: tail) =>
        // TODO: If a map is empty immediatly end the recursion since the intersection of all maps is empty.
        val entitiesWithHeadComponent = world.getComponents[head] getOrElse (Map())
        getMaps[tail](world) :+ entitiesWithHeadComponent
      case _ => Seq()
    }
  }
}
