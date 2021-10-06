package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.{
  CList,
  CListTag,
  CNil,
  Component,
  ComponentTag,
  Deletable,
  DeltaTime,
  Entity,
  IteratingSystem,
  System,
  View,
  World,
}
import dev.atedeg.ecscala.dsl.Syntax
import dev.atedeg.ecscala.dsl.Words.EntityWord

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
 *   *  remove (myComponent) from entity1
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
   * Keyword that enables the use of the word "entity".
   */
  def entity: EntityWord = EntityWord()

  /**
   * Keyword that enables the use of the word "system".
   */
  def system(system: System)(using world: World): Unit = world.addSystem(system)

  /**
   * Keyword that enables the use of the word "system".
   */
  def system[L <: CList](system: IteratingSystem[L])(using clt: CListTag[L])(using world: World): Unit =
    world.addSystem(system)

  /**
   * Keyword that enables the use of the word "getView".
   */
  def getView[L <: CList](using clt: CListTag[L]): ViewFromWorld[L] = ViewFromWorld(using clt)

  /**
   * Keyword that enables the use of the word "remove" for the removal of a [[Clist]] of [[Component]] from an
   * [[Entity]].
   */
  def remove[L <: CList: CListTag](componentsList: L): From[Entity, Unit] = ComponentsFromEntity(componentsList)

  /**
   * Keyword that enables the use of the word "remove" for the removal of a [[CList]] of [[Component]] specifing their
   * type from an [[Entity]].
   */
  def remove[L <: CList: CListTag]: ComponentsTypeFromEntity[L] = ComponentsTypeFromEntity()

  /**
   * Keyword that enables the use of the word "remove" for the removal of a [[Component]] specifing its type from an
   * [[Entity]].
   */
  def remove[C <: Component: ComponentTag]: From[Entity, Unit] = ComponentTypeFromEntity()

  /**
   * Keyword that enables the use of the word "remove" for the removal of an [[Entity]] from a [[World]].
   */
  def remove(entities: Seq[Entity]): From[World, Unit] = EntitiesFromWorld(entities)

  /**
   * Keyword that enables the use of the word "remove" for the removal of a [[System]] from a [[World]].
   */
  def remove(system: System): From[World, Unit] = SystemFromWorld(system)

  /**
   * Keyword that enables the use of the word "clearAllEntities" in the dsl.
   */
  def clearAllEntities: From[World, Unit] = ClearAllFromWorld()
}
