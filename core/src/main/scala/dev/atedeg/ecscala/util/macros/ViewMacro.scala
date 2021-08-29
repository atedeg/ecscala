package dev.atedeg.ecscala.util.macros

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.{ &:, CList, Component, Entity, View, World }

object ViewMacro {

  inline private[ecscala] def createViewImpl[T <: CList](world: World): View[T] = {
    new View[T] {
      override def iterator = new Iterator[Entity] {
        val maps: Seq[Map[Entity, Component]] = getMaps[T](world) sortBy (_.size)
        var smallerMap: Option[Map[Entity, Component]] = maps.headOption
        val otherMaps: Seq[Map[Entity, Component]] = if !maps.isEmpty then maps.tail else maps
        var current: Option[Entity] = smallerMap flatMap (_.headOption) map (_.head)

        override def hasNext = current.isDefined && otherMaps.forall(_.contains(current.get))

        override def next() = {
          val result = current
          smallerMap = smallerMap map (_ - current.get)
          current = smallerMap flatMap (_.headOption) map (_.head)
          result.get
        }
      }
    }
  }

  inline private def getMaps[T <: CList](world: World): Seq[Map[Entity, Component]] = {
    import scala.compiletime.*
    inline erasedValue[T] match {
      case _: (head &: tail) =>
        // TODO: If a map is empty immediatly end the recursion since the intersection of all maps is empty.
        val entitiesWithHeadComponent = world.getComponents[head] getOrElse (Map())
        getMaps[tail](world) :+ entitiesWithHeadComponent
      case _ => Seq()
    }
  }
}
