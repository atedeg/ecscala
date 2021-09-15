package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ &:, CList, CNil, Component }
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

trait Conversions {
  given componentToClist[C <: Component: ComponentTag]: Conversion[C, C &: CNil] with
    def apply(component: C): C &: CNil = component &: CNil
}
