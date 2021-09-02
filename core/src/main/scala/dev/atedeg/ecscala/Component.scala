package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.ComponentTag

/**
 * This trait represents an [[Entity]] 's feature.
 */
trait Component {
  private var _entity: Option[Entity] = Option.empty
  def entity: Option[Entity] = _entity

  private[ecscala] def setEntity(entity: Option[Entity]): Unit = {
    _entity = entity
  }
}

/**
 * A special [[Component]] type that is used to represent a component removal inside a [[System]]
 */
sealed trait Deleted extends Component
case object Deleted extends Deleted

object Component {

  extension [H <: Component: ComponentTag, C <: Component: ComponentTag](head: H)
    /**
     * Convert two [[Component]] in a [[CList]].
     */
    def &:(otherComp: C): H &: C &: CNil = dev.atedeg.ecscala.&:(head, otherComp &: CNil)
}
