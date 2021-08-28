package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.TypeTag

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

object Component {

  extension [H <: Component: TypeTag, C <: Component: TypeTag](head: H)
    /**
     * Convert two [[Component]] in a [[CList]].
     */
    def &:(otherComp: C): H &: C &: CNil = dev.atedeg.ecscala.&:(head, otherComp &: CNil)
}
