package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ &:, CList, CListTag, CNil, Component, ComponentTag, Entity }

trait Conversions {

  /**
   * This conversion enables the removal of a single component from an entity with the following syntax:
   *
   * remove { myComponent } from myEntity
   */
  given componentToClist[C <: Component: ComponentTag]: Conversion[C, C &: CNil] with
    def apply(component: C): C &: CNil = component &: CNil

  /**
   * This conversion enables the removal of a single entity from the world with the following syntax:
   *
   * remove { myEntity } from world
   */
  given Conversion[Entity, Seq[Entity]] = Seq(_)
}
