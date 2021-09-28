package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{ CList, Component, Entity, System, View, World }
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.Words.*

trait Syntax {

  /**
   * This trait enables the use of the word "from" in the dsl.
   */
  trait From[A, B] {
    def from(elem: A): B
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   * remove (entity1) from world
   *   * remove (Seq(entity1, entity2)) from world
   * }}}
   */
  case class EntitiesFromWorld(entities: Seq[Entity]) extends From[World, Unit] {
    override def from(world: World): Unit = entities foreach { world.removeEntity(_) }
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   remove (system1) from world
   * }}}
   */
  case class SystemFromWorld[L <: CList: CListTag](system: System[L]) extends From[World, Unit] {
    override def from(world: World): Unit = world.removeSystem(system)
  }

  /**
   * This case class enables the following syntax:
   * {{{
   *   clearAll from world
   * }}}
   */
  case class ClearAllFromWorld(clearWord: ClearWord) extends From[World, Unit] {
    override def from(world: World): Unit = world.clear()
  }

  /**
   * This case class enables the following syntax:
   *
   * {{{
   *   * remove (MyComponent()) from entity1
   *   * remove { MyComponent1() &: MyComponent2() } from entity1
   * }}}
   */
  case class FromEntity[L <: CList](componentList: L)(using clt: CListTag[L]) extends From[Entity, Unit] {

    override def from(entity: Entity): Unit =
      componentList zip clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] foreach {
        entity.removeComponent(_)(using _)
      }
  }

  /**
   * This class enables the following syntax:
   *
   * {{{
   *   * getView[MyComponent1 &: MyComponent2 &: CNil] from world
   *   * getView[MyComponent1 &: MyComponent2 &: CNil].exluding[MyComponent3 &: CNil] from world
   * }}}
   */
  class ViewFromWorld[A <: CList](using cltA: CListTag[A]) extends From[World, View[A]] {
    override def from(world: World): View[A] = world.getView(using cltA)

    def excluding[B <: CList](using cltB: CListTag[B]): ExcludingViewFromWorld[A, B] = ExcludingViewFromWorld(using
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
  class ExcludingViewFromWorld[A <: CList, B <: CList](using cltA: CListTag[A])(using cltB: CListTag[B])
      extends From[World, View[A]] {
    def from(world: World) = world.getView(using cltA, cltB)
  }
}

object Words {

  /**
   * This case class enables the following syntax:
   *
   * {{{
   * world hasAn entity
   * }}}
   */
  case class EntityWord()

  /**
   * This case class enables the following syntax:
   *
   * {{{
   * clearAll from world
   * }}}
   */
  private[dsl] case class ClearWord()
}
