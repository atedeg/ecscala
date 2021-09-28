package dev.atedeg.ecscala

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.CListTag

/**
 * The generic operations that a SystemBuilder provides.
 * @tparam L the type of the [[CList]] used by the built [[System]].
 */
trait SystemBuilderOps[L <: CList: CListTag] {
  /**
   * The type of the SystemBuilder produced by the different chained operations.
   */
  type BuilderType <: SystemBuilderOps[L]

  /**
   * The type of the [[System]] produced when closing the builder.
   */
  type SystemType <: System[L]

  /**
   * @param before the before function used by Systems created by the returned Builder.
   * @return the new Builder with the specified before function.
   */
  def withBefore(before: (DeltaTime, World, View[L]) => Unit): BuilderType
  /**
   * @param before the after function used by Systems created by the returned Builder.
   * @return the new Builder with the specified after function.
   */
  def withAfter(after: (DeltaTime, World, View[L]) => Unit): BuilderType
  /**
   * @param before the precondition used by Systems created by the returned Builder.
   * @return the new Builder with the specified precondition.
   */
  def withPrecondition(precondition: => Boolean): BuilderType

  /**
   * @param f the [[System.update]] function to be used by the created System
   * @return a System with the given update function
   */
  def withUpdate(f: (Entity, L, DeltaTime) => Deletable[L]): SystemType

  /**
   * @param f the [[System.update]] function to be used by the created System
   * @return a System with the given update function
   */
  def withUpdate(f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]): SystemType
}

/**
 * A builder used to create [[System]].
 * @tparam L the type of the [[CList]] used by the built [[System]].
 */
trait SystemBuilder[L <: CList: CListTag] extends SystemBuilderOps[L] {
  override type BuilderType = SystemBuilder[L]
  override type SystemType = System[L]

  /**
   * Converts this builder to an [[ExcludingSystemBuilder]]
   * @tparam E the type of the [[CList]] of components to be excluded.
   * @return an [[ExcludingSystemBuilder]] from this builder.
   */
  def excluding[E <: CList: CListTag]: ExcludingSystemBuilder[L, E]
}

object SystemBuilder {
  /**
   * @tparam L the type of the [[CList]] used by the built [[System]].
   * @return a new [[SystemBuilder]]
   */
  def apply[L <: CList: CListTag]: SystemBuilder[L] = BuilderUtils.SystemBuilderImpl()
}

/**
 * A builder used to create [[ExcludingSystem]].
 * @tparam L the type of the [[CList]] used by the built [[System]].
 * @tparam E the type of the [[CList]] of components to be excluded.
 */
trait ExcludingSystemBuilder[L <: CList: CListTag, E <: CList: CListTag] extends SystemBuilderOps[L] {
  override type BuilderType = ExcludingSystemBuilder[L, E]
  override type SystemType = ExcludingSystem[L, E]
}

object ExcludingSystemBuilder {
  /**
   * @tparam L the type of the [[CList]] used by the built [[System]].
   * @tparam E the type of the [[CList]] of components to be excluded.
   * @return a new [[ExcludingSystemBuilder]]
   */
  def apply[L <: CList: CListTag, E <: CList: CListTag]: ExcludingSystemBuilder[L, E] =
    BuilderUtils.ExcludingSystemBuilderImpl()
}

private object BuilderUtils {
  abstract class BaseSystemBuilder[L <: CList: CListTag](
      before: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      after: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      precondition: => Boolean = true,
  ) extends SystemBuilderOps[L] {
    def systemConstructor(f: (Entity, L, DeltaTime) => Deletable[L]): SystemType
    def systemConstructor(f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]): SystemType

    def builderConstructor(
        before: (DeltaTime, World, View[L]) => Unit,
        after: (DeltaTime, World, View[L]) => Unit,
        precondition: => Boolean,
    ): BuilderType

    override def withBefore(newBefore: (DeltaTime, World, View[L]) => Unit) =
      builderConstructor(newBefore, after, precondition)

    override def withAfter(newAfter: (DeltaTime, World, View[L]) => Unit) =
      builderConstructor(before, newAfter, precondition)
    override def withPrecondition(newPrecondition: => Boolean) = builderConstructor(before, after, newPrecondition)
    override def withUpdate(f: (Entity, L, DeltaTime) => Deletable[L]) = systemConstructor(f)
    override def withUpdate(f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]) = systemConstructor(f)
  }

  class SystemBuilderImpl[L <: CList: CListTag](
      beforeHandler: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      afterHandler: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      precondition: => Boolean = true,
  ) extends BaseSystemBuilder[L](beforeHandler, afterHandler, precondition)
      with SystemBuilder[L] {

    override def systemConstructor(f: (Entity, L, DeltaTime) => Deletable[L]) = new System[L] {
      override def update(e: Entity, c: L)(dt: DeltaTime, w: World, v: View[L]) = f(e, c, dt)
      override def before(dt: DeltaTime, w: World, v: View[L]): Unit = beforeHandler(dt, w, v)
      override def after(dt: DeltaTime, w: World, v: View[L]): Unit = afterHandler(dt, w, v)
      override def shouldRun = precondition
    }

    override def systemConstructor(f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]) = new System[L] {
      override def update(e: Entity, c: L)(dt: DeltaTime, w: World, v: View[L]) = f(e, c, dt, w, v)
      override def before(dt: DeltaTime, w: World, v: View[L]): Unit = beforeHandler(dt, w, v)
      override def after(dt: DeltaTime, w: World, v: View[L]): Unit = afterHandler(dt, w, v)
      override def shouldRun = precondition
    }

    override def builderConstructor(
        before: (DeltaTime, World, View[L]) => Unit,
        after: (DeltaTime, World, View[L]) => Unit,
        precondition: => Boolean,
    ) = SystemBuilderImpl(before, after, precondition)
    override def excluding[E <: CList: CListTag] = ExcludingSystemBuilderImpl(beforeHandler, afterHandler, precondition)
  }

  class ExcludingSystemBuilderImpl[L <: CList: CListTag, E <: CList: CListTag](
      beforeHandler: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      afterHandler: (DeltaTime, World, View[L]) => Unit = (_, _, _: View[L]) => (),
      precondition: => Boolean = true,
  ) extends BaseSystemBuilder[L](beforeHandler, afterHandler, precondition)
      with ExcludingSystemBuilder[L, E] {

    override def systemConstructor(f: (Entity, L, DeltaTime) => Deletable[L]) = new ExcludingSystem[L, E] {
      override def update(e: Entity, c: L)(dt: DeltaTime, w: World, v: View[L]) = f(e, c, dt)
      override def before(dt: DeltaTime, w: World, v: View[L]): Unit = beforeHandler(dt, w, v)
      override def after(dt: DeltaTime, w: World, v: View[L]): Unit = afterHandler(dt, w, v)
      override def shouldRun = precondition
    }

    override def systemConstructor(f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]) =
      new ExcludingSystem[L, E] {
        override def update(e: Entity, c: L)(dt: DeltaTime, w: World, v: View[L]) = f(e, c, dt, w, v)
        override def before(dt: DeltaTime, w: World, v: View[L]): Unit = beforeHandler(dt, w, v)
        override def after(dt: DeltaTime, w: World, v: View[L]): Unit = afterHandler(dt, w, v)
        override def shouldRun = precondition
      }

    override def builderConstructor(
        before: (DeltaTime, World, View[L]) => Unit,
        after: (DeltaTime, World, View[L]) => Unit,
        precondition: => Boolean,
    ) = ExcludingSystemBuilderImpl(before, after, precondition)
  }
}
