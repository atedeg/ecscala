package dev.atedeg.ecscala

import scala.annotation.tailrec
import dev.atedeg.ecscala.given

type DeltaTime = Double

/**
 * This trait represents a system, which can update the [[World]] 's state.
 */
trait System {

  private[ecscala] final def apply(deltaTime: DeltaTime, world: World): Unit = {
    if (shouldRun) {
      update(deltaTime, world)
    }
  }

  /**
   * @return
   *   whether this [[System]] should be executed or not.
   */
  def shouldRun: Boolean = true

  /**
   * This method is executed every time the [[World]] is updated.
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which the [[System]] is being executed.
   */
  def update(deltaTime: DeltaTime, world: World): Unit
}

object System {

  /**
   * Create a [[System]] from a lambda that specifies its behaviour.
   * @param f
   *   the behaviour of the [[System]] that takes [[DeltaTime]] and the [[World]] and returns Unit.
   * @return
   *   the created [[System]].
   */
  def apply(f: (DeltaTime, World) => Unit): System = new System {
    override def update(deltaTime: DeltaTime, world: World): Unit = f(deltaTime, world)
  }

  /**
   * Create a [[SystemBuilder]].
   * @tparam L
   *   a [[CList]] representing the Components to be iterated.
   * @return
   *   the [[SystemBuilder]].
   */
  def apply[L <: CList: CListTag]: SystemBuilder[L] = SystemBuilder[L]
}

/**
 * Represent a way to iterate over entities with specific components (given by the type parameter L) and manupulate
 * them.
 * @tparam L
 *   a [[CList]] representing the Components available to the [[IteratingSystem]].
 */
trait IteratingSystem[L <: CList](using private val clt: CListTag[L]) extends System {

  /**
   * This method is executed before each iteration of the [[IteratingSystem]].
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which the [[IteratingSystem]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[IteratingSystem]] type.
   */
  def before(deltaTime: DeltaTime, world: World, view: View[L]): Unit = {}

  /**
   * This method is executed after each iteration of the [[IteratingSystem]].
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which the [[IteratingSystem]] is being executed.
   * @param view
   *   a [[View]] with the Components specified by the [[IteratingSystem]] type.
   */
  def after(deltaTime: DeltaTime, world: World, view: View[L]): Unit = {}

  /**
   * Describes how this [[IteratingSystem]] updates the components (described by the type of the System) of an
   * [[Entity]].
   * @param entity
   *   the [[Entity]] whose components are being updated.
   * @param components
   *   the [[CList]] of Components that are being updated.
   * @param deltaTime
   *   the delta time used to update.
   * @param world
   *   the [[World]] in which this [[IteratingSystem]] is being used.
   * @param view
   *   the [[View]] of all entities with the components described by L.
   * @return
   *   a new [[CList]] with the updated components; it could also contain a special component - Deleted - that is used
   *   to delete the corresponding component: e.g. If the expected return type is a {{{Position &: Velocity &: CNil}}}
   *   one could also return {{{Position(1, 2) &: Deleted &: CNil}}} Resulting in the removal of the Velocity Component
   *   from the given Entity.
   */
  def update(entity: Entity, components: L)(deltaTime: DeltaTime, world: World, view: View[L]): Deletable[L]

  override final def update(deltaTime: DeltaTime, world: World): Unit = {
    val view = getView(world)
    before(deltaTime, world, view)
    view foreach { (entity, components) =>
      val updatedComponents = update(entity, components)(deltaTime, world, view)
      updateComponents(updatedComponents)(entity)(using clt)
    }
    after(deltaTime, world, view)
  }

  protected def getView(world: World): View[L] = world.getView(using clt)

  private def updateComponents[L <: CList](components: Deletable[L])(entity: Entity)(using clt: CListTag[L]): Unit = {
    components.taggedWith(clt) foreach { taggedComponent =>
      val (component, ct) = taggedComponent
      component match {
        case Deleted => entity.removeComponent(using ct)
        case _ => entity.setComponent(component)(using ct)
      }
    }
  }
}

object IteratingSystem {

  def apply[L <: CList: CListTag](f: (Entity, L, DeltaTime) => Deletable[L]): IteratingSystem[L] =
    SystemBuilder[L].withUpdate(f)

  def apply[L <: CList: CListTag](f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L]): IteratingSystem[L] =
    SystemBuilder[L].withUpdate(f)
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
) extends IteratingSystem[LIncluded] {
  override protected def getView(world: World): View[LIncluded] = world.getView(using cltIncl, cltExcl)
}

object ExcludingSystem {

  def apply[L <: CList: CListTag, E <: CList: CListTag](
      f: (Entity, L, DeltaTime) => Deletable[L],
  ): ExcludingSystem[L, E] =
    SystemBuilder[L].excluding[E].withUpdate(f)

  def apply[L <: CList: CListTag, E <: CList: CListTag](
      f: (Entity, L, DeltaTime, World, View[L]) => Deletable[L],
  ): ExcludingSystem[L, E] = SystemBuilder[L].excluding[E].withUpdate(f)
  def apply[L <: CList: CListTag, E <: CList: CListTag]: ExcludingSystemBuilder[L, E] = ExcludingSystemBuilder[L, E]
}
