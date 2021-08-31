package dev.atedeg.ecscala

import scala.annotation.targetName
import dev.atedeg.ecscala.util.types.{ CListTag, TypeTag }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.macros.ViewMacro.createViewImpl

/**
 * A [[View]] on a [[World]] that allows to iterate over its entities with components of the type specified in L.
 * @tparam L
 *   [[CList]] with the type of the components.
 */
trait View[L <: CList] extends Iterable[(Entity, L)]

private[ecscala] object View {
  def apply[T <: CList](world: World)(using clt: CListTag[T]): View[T] = createViewImpl(world)(using clt)
}
