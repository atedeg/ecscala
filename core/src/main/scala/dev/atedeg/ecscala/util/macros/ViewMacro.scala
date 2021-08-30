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
        val otherMaps: Seq[Map[Entity, Component]] = if !maps.isEmpty then maps.tail else maps
        var smallerMap: Option[Map[Entity, Component]] = maps.headOption.map(keepCommonKeys(_)(otherMaps))
        var current: Option[Entity] = smallerMap flatMap (_.headOption) map (_.head)

        private def keepCommonKeys[K, V](map: Map[K, V])(maps: Seq[Map[K, V]]) =
          map.filter((k, _) => isInAllMaps(k)(maps))
        private def isInAllMaps[K, V](key: K)(maps: Seq[Map[K, V]]) = maps.forall(_.contains(key))

        override def hasNext = current.isDefined

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
