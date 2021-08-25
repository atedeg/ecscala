package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.TypeTag
import dev.atedeg.ecscala.{ Component, Entity, World }

import javax.swing.text.Position

extension (entity: Entity) {
// TODO: gestione tipi diversi di component
  
  def withComponents[T <: Component](components: Seq[T])(using tt: TypeTag[T]): Entity = {
    components foreach { entity.addComponent(_)(tt) }
    entity
  }
}
