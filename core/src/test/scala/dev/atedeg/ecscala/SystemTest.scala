package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ Mass, Position, Velocity, ViewFixture }
import dev.atedeg.ecscala.util.types.given
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SystemTest extends AnyWordSpec with Matchers {
  def beAbleTo = afterWord("be able to")

  "A System" when {
    "with a false precondition" should {
      "not execute" in new ViewFixture {
        var success = true
        world.addSystem(new System[Position &: CNil] {
          override def shouldRun = false
          override def before(deltaTime: DeltaTime, world: World, view: View[Position &: CNil]): Unit = success = false
          override def after(deltaTime: DeltaTime, world: World, view: View[Position &: CNil]): Unit = success = false
          override def update(entity: Entity, components: Position &: CNil)(deltaTime: DeltaTime, world: World, view: View[Position &: CNil]) = {
            success = false
            components
          }
        })
        world.update(10)
        success shouldBe true
      }
    }
    "executing its update" should beAbleTo {
      "update components" in new ViewFixture {
        world.addSystem[Position &: Velocity &: CNil]((_, comps, _) => {
          val Position(x, y) &: Velocity(vx, vy) &: CNil = comps
          Position(x + 1, y + 1) &: Velocity(vx + 1, vy + 1) &: CNil
        })
        world.update(10)

        world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
          (entity1, Position(2, 2) &: Velocity(2, 2)),
          (entity3, Position(2, 2) &: Velocity(2, 2)),
          (entity4, Position(2, 2) &: Velocity(2, 2)),
        )
      }
      "remove components" in new ViewFixture {
        world.addSystem[Position &: Velocity &: CNil]((_, _, _) => Deleted &: Deleted &: CNil)
        world.update(10)
        world.getView[Position &: Velocity &: CNil] shouldBe empty
      }
      "add components" in new ViewFixture {
        world.addSystem[Position &: Velocity &: CNil]((entity, comps, _) => {
          entity addComponent Mass(10)
          comps
        })
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
        world.addSystem[Position &: CNil]((_, comps, _, w, _) => {
          w.createEntity()
          comps
        })
        world.update(10)
        world.entitiesCount shouldBe 9
      }
      "remove entities from its world" in new ViewFixture {
        world.addSystem[Position &: CNil]((entity, comps, _, w, _) => {
          w.removeEntity(entity)
          comps
        })
        world.update(10)
        world.entitiesCount shouldBe 1
      }
    }
    "executing its update" should {
      "have the correct delta time" in new ViewFixture {
        world.addSystem[Position &: CNil]((_, comps, dt) => {
          dt shouldBe 10
          comps
        })
        world.update(10)
      }
      "execute its before and after handlers in the correct order" in new ViewFixture {
        type Comps = Position &: Velocity &: CNil
        val testSystem = new System[Comps] {
          override def before(deltaTime: DeltaTime, world: World, view: View[Comps]) =
            view foreach (entityComponentsPair => {
              val (entity, Position(px, py) &: _) = entityComponentsPair
              entity.addComponent(Position(px * 2, py * 2))
            })

          override def after(deltaTime: DeltaTime, world: World, view: View[Comps]) =
            view foreach (entityComponentsPair => {
              val (entity, Position(px, py) &: _) = entityComponentsPair
              entity.addComponent(Position(px + 1, py + 1))
            })

          override def update(
              entity: Entity,
              components: Comps,
          )(deltaTime: DeltaTime, world: World, view: View[Comps]) = components
        }
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
        world.addSystem[Position &: Velocity &: CNil, Mass &: CNil]((_, comps, _) => {
          val Position(x, y) &: Velocity(vx, vy) &: CNil = comps
          Position(x + 1, y + 1) &: Velocity(vx + 1, vy + 1) &: CNil
        })
        world.update(10)

        world.getView[Position &: Velocity &: CNil, Mass &: CNil] should contain theSameElementsAs List(
          (entity1, Position(2, 2) &: Velocity(2, 2)),
          (entity4, Position(2, 2) &: Velocity(2, 2)),
        )
      }
    }
  }

  "An EmptySystem" should {
    "execute its update" in new ViewFixture {
      var success = false
      world.addSystem(EmptySystem(() => success = true))
      world.update(10)
      success shouldBe true
    }
    }
  }
