package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ taggedWith, CList, CListTag, Component, ComponentTag, Entity, System, View, World }

/**
 * Trait that enables the syntax of the DSL.
 */
trait Syntax {

  /**
   * This trait enables the use of the word "from" in the dsl.
   */
  sealed trait From[A, B] {
    def from(elem: A): B
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   * remove (entity1) from world
   *   * remove (Seq(myEntity1, myEntity2)) from world
   * }}}
   */
  case class EntitiesFromWorld(entities: Seq[Entity]) extends From[World, Unit] {
    override def from(world: World): Unit = entities foreach { world.removeEntity(_) }
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   remove (mySystem) from world
   * }}}
   */
  case class SystemFromWorld(system: System) extends From[World, Unit] {
    override def from(world: World): Unit = world.removeSystem(system)
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   clearAll from world
   * }}}
   */
  case class ClearAllFromWorld() extends From[World, Unit] {
    override def from(world: World): Unit = world.clearEntities()
  }

  /**
   * This case class enables the following syntax:
   *
   * {{{
   *   remove { myComponent1 &: myComponent2 } from entity1
   * }}}
   */
  case class ComponentsFromEntity[L <: CList](componentList: L)(using clt: CListTag[L]) extends From[Entity, Unit] {

    override def from(entity: Entity): Unit =
      componentList.taggedWith(clt) foreach { entity.removeComponent(_)(using _) }
  }

  /**
   * This class enables the following syntax:
   *
   * {{{
   *   remove[Component] from entity1
   * }}}
   */
  class ComponentTypeFromEntity[C <: Component](using ct: ComponentTag[C]) extends From[Entity, Unit] {
    override def from(entity: Entity): Unit = entity.removeComponent(using ct)
  }

  /**
   * This class enables the following syntax:
   *
   * {{{
   *   remove[Component1 &: Component2 &: CNil ] from entity1
   * }}}
   */
  class ComponentsTypeFromEntity[L <: CList](using clt: CListTag[L]) extends From[Entity, Unit] {

    override def from(entity: Entity): Unit = clt.tags foreach { entity.removeComponent(using _) }
  }

  /**
   * This class enables the following syntax:
   *
   * {{{
   *   * getView[MyComponent1 &: MyComponent2 &: CNil] from world
   *   * getView[MyComponent1 &: MyComponent2 &: CNil].exluding[MyComponent3 &: CNil] from world
   * }}}
   */
  class ViewFromWorld[LA <: CList](using cltA: CListTag[LA]) extends From[World, View[LA]] {
    override def from(world: World): View[LA] = world.getView(using cltA)

    def excluding[LB <: CList](using cltB: CListTag[LB]): ExcludingViewFromWorld[LA, LB] = ExcludingViewFromWorld(using
      cltA,
    )(using cltB)
  }

  /**
   * This class enables the following syntax:
   *
   * {{{
   *   getView[MyComponent1 &: MyComponent2 &: CNil].exluding[MyComponent3 &: CNil] from world
   * }}}
   */
  class ExcludingViewFromWorld[LA <: CList, LB <: CList](using cltA: CListTag[LA])(using cltB: CListTag[LB])
      extends From[World, View[LA]] {
    def from(world: World) = world.getView(using cltA, cltB)
  }
}

/**
 * Trait that enables the words of the DSL.
 */
trait Words

/**
 * This case class enables the following syntax:
 *
 * {{{
 * world hasAn entity
 * }}}
 */
case class EntityWord() extends Words
