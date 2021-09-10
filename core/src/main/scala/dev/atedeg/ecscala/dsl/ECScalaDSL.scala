package dev.atedeg.ecscala.dsl

import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }
import dev.atedeg.ecscala.{ CList, Component, Deletable, Entity, System, View, World }
import dev.atedeg.ecscala.dsl.Words.EntityWord
import dev.atedeg.ecscala.DeltaTime

/**
 * This trait provides a domain specific language (DSL) for expressing the Ecscala framework operation using an
 * english-like language. Here's the things you can do:
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
 *   *  remove (entity1, entity2, entity3) from world
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
 *       MyComponent1() and MyComponent2() and MyComponent3()
 * }
 * }}}
 *
 * '''Add components to an entity:'''
 * {{{
 *   *  entity1 + MyComponent()
 *   *  entity1 withComponent MyComponent()
 *   *  entity1 withComponents { MyComponent1() and MyComponent2() }
 * }}}
 *
 * '''Remove a component from an entity:'''
 * {{{
 *   *  remove MyComponent() from entity1
 *   *  entity1 - MyComponent()
 * }}}
 *
 * '''Add a system to a world:'''
 * {{{
 * world hasA system[MyComponent() &: CNil] { (_,_,_) => {}}
 * }}}
 *
 * '''Get a view from a world:'''
 * {{{
 *   val view = getView[MyComponent1() &: MyComponent2() &: CNil] from world
 * }}}
 */
trait ECScalaDSL extends ExtensionMethodsDSL with FromSyntax {

  /**
   * Keyword that enables the use of the word "entity" in the dsl.
   */
  def entity: EntityWord = EntityWord()

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: System[L])(using ct: CListTag[L])(using world: World): Unit =
    world.addSystem(system)(using ct)

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: (Entity, L, DeltaTime) => Deletable[L])(using ct: CListTag[L])(using
      world: World,
  ): Unit =
    world.addSystem(system)(using ct)

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[LIncluded <: CList, LExcluded <: CList](
      system: (Entity, LIncluded, DeltaTime) => Deletable[LIncluded],
  )(using cltIncl: CListTag[LIncluded], cltExcl: CListTag[LExcluded])(using world: World): Unit =
    world.addSystem(system)(using cltIncl)

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[L <: CList](system: (Entity, L, DeltaTime, World, View[L]) => Deletable[L])(using
      clt: CListTag[L],
  )(using world: World): Unit = world.addSystem(system)(using clt)

  /**
   * Keyword that enables the use of the word "system" in the dsl.
   */
  def system[LIncluded <: CList, LExcluded <: CList](
      system: (Entity, LIncluded, DeltaTime, World, View[LIncluded]) => Deletable[LIncluded],
  )(using cltIncl: CListTag[LIncluded], cltExcl: CListTag[LExcluded])(using world: World): Unit =
    world.addSystem(system)(using cltIncl)

  /**
   * Keyword that enables the use of the word "getView" in the dsl.
   */
  def getView[L <: CList](using clt: CListTag[L]) = ViewFromWorld(using clt)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove(entities: Entity*) = FromWorld(entities)

  /**
   * Keyword that enables the use of the word "remove" in the dsl.
   */
  def remove(map: => Map[ComponentTag[? <: Component], Component]): FromEntity = FromEntity(map)

  def remove[C <: Component](component: C)(using ct: ComponentTag[C]): FromEntity = FromEntity(Map(ct -> component))
}

private[dsl] trait FromSyntax {

  class FromWorld(entities: Seq[Entity]) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   remove (entity1) from world
     * }}}
     */
    def from(world: World): Unit = entities foreach { world.removeEntity(_) }
  }

  class FromEntity(map: => Map[ComponentTag[? <: Component], Component]) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   remove (myComponent) from entity1
     * }}}
     */
    def from(entity: Entity): Unit = map foreach { (ct, component) =>
      entity.removeComponent(component)(using ct.asInstanceOf[ComponentTag[Component]])
    }
  }

  class ViewFromWorld[L <: CList](using clt: CListTag[L]) {

    /**
     * This method enables the following syntax:
     *
     * {{{
     *   getView[MyComponent1() &: MyComponent2() &: CNil] from world
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
}
