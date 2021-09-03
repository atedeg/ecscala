package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ Mass, Position, Velocity, ViewFixture }
import dev.atedeg.ecscala.util.types.given
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SystemTest extends AnyWordSpec with Matchers {

  "A System" when {
    "executing its update" should {
      "be able to update components" in new ViewFixture {
        world.addSystem[Position &: Velocity &: CNil]((_, components, _) => {
          val Position(x, y) &: Velocity(vx, vy) &: CNil = components
          Position(x + 1, y + 1) &: Velocity(vx + 1, vy + 1) &: CNil
        })
        world.update()

        world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
          (entity1, Position(2, 2) &: Velocity(2, 2)),
          (entity3, Position(2, 2) &: Velocity(2, 2)),
          (entity4, Position(2, 2) &: Velocity(2, 2)),
        )
      }
      "be able to remove components" in new ViewFixture {
        world.addSystem[Position &: Velocity &: CNil]((_, components, _) => Deleted &: Deleted &: CNil)
        world.update()
        world.getView[Position &: Velocity &: CNil] shouldBe empty
      }
      "be able to add components" in new ViewFixture {
        val v = world.getView[Mass &: CNil]
        println(v)
        println(v.iterator.hasNext)
        world.addSystem[Position &: Velocity &: CNil]((entity, components, _) => {
          entity addComponent Mass(10)
          components
        })
        world.update()

        world.getView[Mass &: CNil] should contain theSameElementsAs List(
          (entity1, Mass(10) &: CNil),
          (entity2, Mass(1) &: CNil),
          (entity3, Mass(10) &: CNil),
          (entity4, Mass(10) &: CNil),
        )
      }
    }
  }

  "Systems" should {

    "execute their before and after handlers in the correct order" in new ViewFixture {
      type Comps = Position &: Velocity &: CNil
      val testSystem = new System[Comps] {
        override def before(world: World, view: View[Comps]) =
          view foreach (entityComponentsPair => {
            val (entity, Position(px, py) &: _) = entityComponentsPair
            entity.addComponent(Position(px * 2, py * 2))
          })

        override def after(world: World, view: View[Comps]) =
          view foreach (entityComponentsPair => {
            val (entity, Position(px, py) &: _) = entityComponentsPair
            entity.addComponent(Position(px + 1, py + 1))
          })

        override def apply(entity: Entity, components: Comps, view: View[Comps]) = components
      }
      world.addSystem(testSystem)
      world.update()

      world.getView[Comps] should contain theSameElementsAs List(
        (entity1, Position(3, 3) &: Velocity(1, 1)),
        (entity3, Position(3, 3) &: Velocity(1, 1)),
        (entity4, Position(3, 3) &: Velocity(1, 1)),
      )
    }
  }
}
