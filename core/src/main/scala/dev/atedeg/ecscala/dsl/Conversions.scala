package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ &:, CList, CNil, Component, Entity }
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

trait Conversions {

  /**
   * This conversion enable the removal of a single component from an entity with the following syntax:
   *
   * remove { Component() } from entity1
   */
  given componentToClist[C <: Component: ComponentTag]: Conversion[C, C &: CNil] with
    def apply(component: C): C &: CNil = component &: CNil

  /**
   * This conversion enable the removal of a single entity from the world with the following syntax:
   *
   * remove { entity1 } from world
   */
  given Conversion[Entity, Seq[Entity]] = Seq(_)
}
