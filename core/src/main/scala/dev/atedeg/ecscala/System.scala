package dev.atedeg.ecscala

import scala.annotation.tailrec
import dev.atedeg.ecscala.{ CList, Entity, View }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.util.types.{ CListTag, ComponentTag }

type DeltaTime = Double

/**
 * Represent a way to iterate over entities with specific components (given by the type parameter L) and manupulate
 * them.
 * @tparam L
 *   a [[CList]] representing the Components available to the [[System]].
 */
trait System[L <: CList](using private val clt: CListTag[L]) {

  private[ecscala] final def apply(world: World, deltaTime: DeltaTime): Unit = {
    if (shouldRun) {
      val view = getView(world)
      before(deltaTime, world, view)
      view foreach { (entity, components) =>
        val updatedComponents = this.update(entity, components)(deltaTime, world, view)
        updateComponents(updatedComponents)(entity)(using clt)
      }
      after(deltaTime, world, view)
    }
  }

  /**
   * @return
   *   wether this [[System]] should be executed or not.
   */
  def shouldRun: Boolean = true

  /**
   * This method is executed before each iteration of the [[System]].
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which the [[System]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[System]] type.
   */
  def before(deltaTime: DeltaTime, world: World, view: View[L]): Unit = {}

  /**
   * This method is executed after each iteration of the [[System]].
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which the [[System]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[System]] type.
   */
  def after(deltaTime: DeltaTime, world: World, view: View[L]): Unit = {}

  /**
   * Describes how this [[System]] updates the components (described by the type of the System) of an [[Entity]].
   * @param entity
   *   the [[Entity]] whose components are being updated.
   * @param components
   *   the [[CList]] of Components that are being updated.
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which this [[System]] is being used.
   * @param view
   *   the [[View]] of all entities with the components described by L.
   * @return
   *   a new [[CList]] with the updated components; it could also contain a special component - Deleted - that is used
   *   to delete the corresponding component: e.g. If the expected return type is a {{{Position &: Velocity &: CNil}}}
   *   one could also return {{{Position(1, 2) &: Deleted &: CNil}}} Resulting in the removal of the Velocity Component
   *   from the given Entity.
   */
  def update(entity: Entity, components: L)(deltaTime: DeltaTime, world: World, view: View[L]): Deletable[L]

  protected def getView(world: World): View[L] = world.getView(using clt)

  private def updateComponents[L <: CList](components: Deletable[L])(entity: Entity)(using clt: CListTag[L]): Unit = {
    val taggedComponents = clt.tags.asInstanceOf[Seq[ComponentTag[Component]]] zip components
    taggedComponents foreach { taggedComponent =>
      val (ct, component) = taggedComponent
      component match {
        case Deleted => entity.removeComponent(using ct)
        case _ => entity.addComponent(component)(using ct)
      }
    }
  }
}

object System {
  def apply[L <: CList: CListTag](f: (Entity, L, DeltaTime) => Deletable[L]): System[L] = SystemBuilder[L].withUpdate(f)
  def apply[L <: CList: CListTag](f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]): System[L] = SystemBuilder[L].withUpdate(f)
  def withComponents[L <: CList: CListTag]: SystemBuilder[L] = SystemBuilder[L]
  def empty(f: (DeltaTime, World) => Unit) = EmptySystem(f)
}

/**
 * Represent a way to iterate over entities with specific components (given by the type parameter LIncluded) and without
 * specific components (given by the type parameter LExcluded) and manipulate them.
 * @tparam LIncluded
 *   a [[CList]] representing the Components available to the [[System]].
 * @tparam LExcluded
 *   a [[CList]] representing the Components to filter out from the selected entities.
 */
trait ExcludingSystem[LIncluded <: CList, LExcluded <: CList](using
    private val cltIncl: CListTag[LIncluded],
    private val cltExcl: CListTag[LExcluded],
) extends System[LIncluded] {
  override protected def getView(world: World): View[LIncluded] = world.getView(using cltIncl, cltExcl)
}

/**
 * A [[System]] that does not iterate over any [[Component]].
 */
trait EmptySystem extends System[CNil] {
  def update(deltaTime: DeltaTime, world: World): Unit

  override final def update(entity: Entity, components: CNil)(deltaTime: DeltaTime, world: World, view: View[CNil]) =
    throw new IllegalStateException("An EmptySystem's update method should never be called.")

  override final def before(deltaTime: DeltaTime, world: World, view: View[CNil]): Unit = update(deltaTime, world)
  override final def after(deltaTime: DeltaTime, world: World, view: View[CNil]): Unit = ()
}

object EmptySystem {

  /**
   * Create an [[EmptySystem]] from a lambda that specity the behaviuor of this [[System]].
   * @param f
   *   the behaviuor of the [[EmptySystem]] that takes [[DeltaTime]] and the [[World]] and return Unit.
   * @return
   *   the [[EmptySystem]]
   */
  def apply(f: (DeltaTime, World) => Unit): EmptySystem = new EmptySystem {
    override def update(deltaTime: DeltaTime, world: World): Unit = f(deltaTime, world)
  }
}
