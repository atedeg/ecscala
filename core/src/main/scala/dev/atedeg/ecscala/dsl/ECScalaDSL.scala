package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.dsl.Words.EntityWord
import dev.atedeg.ecscala.dsl.Syntax
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, CNil, Component, Deletable, DeltaTime, Entity, System, View, World }

/**
 * This trait provides a domain specific language (DSL) for expressing the ECScala framework operations using an
 * english-like syntax. Here's the things you can do:
 *
 * '''Create an Entity in a World:'''
 * {{{
 * val world = World()
 * val entity1 = world hasAn entity
 * }}}
 *
 * '''Remove Entities from a World:'''
 * {{{
 *   *  world -= entity1
 *   *  remove (entity1) from world
 *   *  remove (List(entity1, entity2, entity3)) from world
 * }}}
 *
 * '''Create an Entity in a World with a Component:'''
 * {{{
 * val entity1 = world hasAn entity withComponent myComponent
 * }}}
 *
 * '''Create an Entity in a World with multiple Components:'''
 * {{{
 * val entity1 = world hasAn entity withComponents {
 *       myComponent1 &: myComponent2 &: myComponent3
 * }
 * }}}
 *
 * '''Add Components to an Entity:'''
 * {{{
 *   *  entity1 += myComponent
 *   *  entity1 withComponent myComponent
 *   *  entity1 withComponents { myComponent1 &: myComponent2 }
 * }}}
 *
 * '''Remove Components from an Entity:'''
 * {{{
 *   *  remove { myComponent } from entity1
 *   *  entity1 -= myComponent
 *   *  remove { myComponent1 &: myComponent2 &: myComponent3 } from entity1
 * }}}
 *
 * '''Add a System to a World:'''
 * {{{
 *     * world hasA system[MyComponent &: CNil] { (_,_,_) => {}}
 *     * world hasA system(mySistem)
 *     * world += mySystem
 * }}}
 *
 * '''Remove a System from a World'''
 * {{{
 *   * remove (mySystem) from world
 *   * world -= mySystem
 * }}}
 *
 * '''Get a View from a World:'''
 * {{{
 *   val view = getView[MyComponent1 &: MyComponent2 &: CNil] from world
 * }}}
 *
 * '''Get a View without certain Components'''
 * {{{
 *   val view = getView[MyComponent1 &: CNil].excluding[MyComponent2 &: CNil] from world
 * }}}
 *
 * '''Remove all Entities and their Components from a World:'''
 * {{{
 *   clearAllEntities from world
 * }}}
 */
trait ECScalaDSL extends ExtensionMethods with Conversions with Syntax {

  /**
   * Keyword that enables the use of the word "entity" in the dsl.
   */
  def entity: EntityWord = EntityWord()

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: System[L])(using clt: CListTag[L])(using world: World): Unit =
    world.addSystem(system)(using clt)

  /**
   * Keyword that enables the use of the word "getView" in the dsl.
   */
  def getView[L <: CList](using clt: CListTag[L]): ViewFromWorld[L] = ViewFromWorld(using clt)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove[L <: CList: CListTag](componentsList: L): From[Entity, Unit] = FromEntity(componentsList)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove(entities: Seq[Entity]): From[World, Unit] = EntitiesFromWorld(entities)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove[L <: CList: CListTag](system: System[L]): From[World, Unit] = SystemFromWorld(system)

  /**
   * Keyword that enables the use of the word "clearAllEntities" in the dsl.
   */
  def clearAllEntities: From[World, Unit] = ClearAllFromWorld()
}
