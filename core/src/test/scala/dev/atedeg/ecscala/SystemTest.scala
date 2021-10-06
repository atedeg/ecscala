package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.fixtures.{ Mass, Position, Velocity, ViewFixture }

class SystemTest extends AnyWordSpec with Matchers {
  def beAbleTo = afterWord("be able to")

  "A System" when {
    "with a false precondition" should {
      "not execute" in new ViewFixture {
        var success = true
        world.addSystem(new System {
          override def shouldRun: Boolean = false

          override def update(deltaTime: DeltaTime, world: World): Unit = success = false
        })
        world.update(10)
        success shouldBe true
      }
    }
    "execute when updating" in new ViewFixture {
      var success = false
      world.addSystem(System((_, _) => success = true))
      world.update(10)
      success shouldBe true
    }
  }

  "An IteratingSystem" when {
    "with a false precondition" should {
      "not execute" in new ViewFixture {
        var success = true
        world.addSystem(
          System[Position &: CNil]
            .withPrecondition(false)
            .withBefore { (_, _, _) => success = false }
            .withAfter { (_, _, _) => success = false }
            .withUpdate { (_, components, _) =>
              success = false
              components
            },
        )
        world.update(10)
        success shouldBe true
      }
    }
    "executing its update" should beAbleTo {
      "update components" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: Velocity &: CNil]((_, comps, _) => {
          val Position(x, y) &: Velocity(vx, vy) &: CNil = comps
          Position(x + 1, y + 1) &: Velocity(vx + 1, vy + 1) &: CNil
        }))
        world.update(10)

        world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
          (entity1, Position(2, 2) &: Velocity(2, 2)),
          (entity3, Position(2, 2) &: Velocity(2, 2)),
          (entity4, Position(2, 2) &: Velocity(2, 2)),
        )
      }
      "remove components" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: Velocity &: CNil]((_, _, _) => Deleted &: Deleted &: CNil))
        world.update(10)
        world.getView[Position &: Velocity &: CNil] shouldBe empty
      }
      "add components" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: Velocity &: CNil]((entity, comps, _) => {
          entity setComponent Mass(10)
          comps
        }))
        world.update(10)

        world.getView[Mass &: CNil] should contain theSameElementsAs List(
          (entity1, Mass(10) &: CNil),
          (entity2, Mass(1) &: CNil),
          (entity3, Mass(10) &: CNil),
          (entity4, Mass(10) &: CNil),
          (entity5, Mass(1) &: CNil),
        )
      }
      "add entities to its world" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: CNil]((_, comps, _, w, _) => {
          w.createEntity()
          comps
        }))
        world.update(10)
        world.entitiesCount shouldBe 9
      }
      "remove entities from its world" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: CNil]((entity, comps, _, w, _) => {
          w.removeEntity(entity)
          comps
        }))
        world.update(10)
        world.entitiesCount shouldBe 1
      }
    }
    "executing its update" should {
      "have the correct delta time" in new ViewFixture {
        world.addSystem(IteratingSystem[Position &: CNil]((_, comps, dt) => {
          dt shouldBe 10
          comps
        }))
        world.update(10)
      }
      "execute its before and after handlers in the correct order" in new ViewFixture {
        type Comps = Position &: Velocity &: CNil
        val testSystem = System[Comps].withBefore { (deltaTime, world, view) =>
          view foreach (entityComponentsPair => {
            val (entity, Position(px, py) &: _) = entityComponentsPair
            entity setComponent Position(px * 2, py * 2)
          })
        }.withAfter { (deltaTime, world, view) =>
          view foreach (entityComponentsPair => {
            val (entity, Position(px, py) &: _) = entityComponentsPair
            entity setComponent Position(px + 1, py + 1)
          })
        }.withUpdate { (_, components, _) => components }

        world.addSystem(testSystem)
        world.update(10)

        world.getView[Comps] should contain theSameElementsAs List(
          (entity1, Position(3, 3) &: Velocity(1, 1)),
          (entity3, Position(3, 3) &: Velocity(1, 1)),
          (entity4, Position(3, 3) &: Velocity(1, 1)),
        )
      }
    }
  }

  "An ExcludingSystem" when {
    "executing its update" should beAbleTo {
      "update components" in new ViewFixture {
        world.addSystem(ExcludingSystem[Position &: Velocity &: CNil, Mass &: CNil]((_, comps, _, _, _) => {
          val Position(x, y) &: Velocity(vx, vy) &: CNil = comps
          Position(x + 1, y + 1) &: Velocity(vx + 1, vy + 1) &: CNil
        }))
        world.update(10)

        world.getView[Position &: Velocity &: CNil, Mass &: CNil] should contain theSameElementsAs List(
          (entity1, Position(2, 2) &: Velocity(2, 2)),
          (entity4, Position(2, 2) &: Velocity(2, 2)),
        )
      }
    }
    "excludes included components" should {
      "not run its update" in new ViewFixture {
        var updateExecuted = false
        world.addSystem(
          System[Position &: CNil].excluding[Position &: CNil].withUpdate { (_, cs, _) =>
            updateExecuted = true; cs
          },
        )
        updateExecuted shouldBe false
      }
    }
  }
}
