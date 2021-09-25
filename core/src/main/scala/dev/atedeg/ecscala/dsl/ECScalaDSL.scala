package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, CNil, Component, Deletable, DeltaTime, Entity, System, View, World }
import dev.atedeg.ecscala.dsl.Words.*
import dev.atedeg.ecscala.util.types

/**
 * This trait provides a domain specific language (DSL) for expressing the ECScala framework operations using an
 * english-like syntax. Here's the things you can do:
 *
 * '''Create an entity in a world:'''
 * {{{
 * val world = World()
 * val entity1 = world hasAn entity
 * }}}
 *
 * '''Remove entities from a world:'''
 * {{{
 *   *  world - entity1
 *   *  remove (entity1) from world
 *   *  remove (List(entity1, entity2, entity3)) from world
 * }}}
 *
 * '''Create an entity in a world with a component:'''
 * {{{
 * val entity1 = world hasAn entity withComponent MyComponent()
 * }}}
 *
 * '''Create an entity in a world with multiple components:'''
 * {{{
 * val entity1 = world hasAn entity withComponents {
 *       MyComponent1() &: MyComponent2() &: MyComponent3()
 * }
 * }}}
 *
 * '''Add components to an entity:'''
 * {{{
 *   *  entity1 + MyComponent()
 *   *  entity1 withComponent MyComponent()
 *   *  entity1 withComponents { MyComponent1() &: MyComponent2() }
 * }}}
 *
 * '''Remove components from an entity:'''
 * {{{
 *   *  remove { MyComponent() } from entity1
 *   *  entity1 - MyComponent()
 *   *  remove { MyComponent1() &: MyComponent2() &: MyComponent3() } from entity1
 * }}}
 *
 * '''Add a system to a world:'''
 * {{{
 * world hasA system[MyComponent &: CNil] { (_,_,_) => {}}
 * }}}
 *
 * '''Get a view from a world:'''
 * {{{
 *   val view = getView[MyComponent1 &: MyComponent2 &: CNil] from world
 * }}}
 *
 * '''Remove all entities and their components from a world:'''
 * {{{
 *   clearAll from world
 * }}}
 */
trait ECScalaDSL extends ExtensionMethods with Conversions with FromSyntax {

  /**
   * Keyword that enables the use of the word "entity" in the dsl.
   */
  def entity: EntityWord = EntityWord()

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: (Entity, L, DeltaTime, World, View[L]) => Deletable[L])(using
      clt: CListTag[L],
  )(using world: World): Unit = world.addSystem(system)(using clt)

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: System[L])(using clt: CListTag[L])(using world: World): Unit = world.addSystem(system)(using clt)

  /**
   * Keyword that enables the use of the word "getView" in the dsl.
   */
  def getView[L <: CList](using clt: CListTag[L]) = ViewFromWorld(using clt)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove(entities: Seq[Entity]) = FromWorld(entities)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove[L <: CList: CListTag](componentsList: L): FromEntity[L] = FromEntity(componentsList)

  /**
   * Keyword that enables the use of the word "clearAll" in the dsl.
   */
  def clearAll: FromWorld = FromWorld(ClearWord())
}

private[dsl] trait FromSyntax {

  class FromWorld(left: Seq[Entity] | ClearWord) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   * remove (entity1) from world
     *   * clearAll from world
     * }}}
     */
    def from(world: World): Unit = left match
      case entitities: Seq[Entity] => entitities foreach { world.removeEntity(_) }
      case _: ClearWord => world.clear()
  }

  class FromEntity[L <: CList](componentList: L)(using clt: CListTag[L]) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   remove (myComponent) from entity1
     * }}}
     */
    def from(entity: Entity): Unit =
      componentList zip clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] foreach {
        entity.removeComponent(_)(using _)
      }
  }

  class ViewFromWorld[L <: CList](using clt: CListTag[L]) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   getView[MyComponent1 &: MyComponent2 &: CNil] from world
     * }}}
     */
    def from(world: World): View[L] = world.getView(using clt)
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
  private[dsl] case class ClearWord()
}
