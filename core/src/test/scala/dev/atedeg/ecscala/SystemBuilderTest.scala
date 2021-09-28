package dev.atedeg.ecscala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.SystemBuilderFixture
import dev.atedeg.ecscala.fixtures.{ Position, Velocity }

class SystemBuilderTest extends AnyWordSpec with Matchers {
  def using = afterWord("using")

  "A SystemBuilder" should {
    "work with short update" in new SystemBuilderFixture {
      var updateExecuted = false
      world.addSystem(SystemBuilder[Position &: CNil].withUpdate { (_, c, _) =>
        updateExecuted = true; c
      })
      world.update(10)
      updateExecuted shouldBe true
    }
    "work with full update" in new SystemBuilderFixture {
      var updateExecuted = false
      world.addSystem(SystemBuilder[Position &: CNil].withUpdate { (_, c, _, _, _) =>
        updateExecuted = true; c
      })
      world.update(10)
      updateExecuted shouldBe true
    }
    "return the correct system" when using {
      "withBefore" in new SystemBuilderFixture {
        var beforeExecuted = false
        world.addSystem(
          SystemBuilder[Position &: CNil].withBefore { (_, _, _) => beforeExecuted = true }.withUpdate { (_, c, _) =>
            c
          },
        )
        world.update(10)
        beforeExecuted shouldBe true
      }
      "withAfter" in new SystemBuilderFixture {
        var afterExecuted = false
        world.addSystem(
          SystemBuilder[Position &: CNil].withAfter { (_, _, _) => afterExecuted = true }.withUpdate { (_, c, _) => c },
        )
        world.update(10)
        afterExecuted shouldBe true
      }
      "withPrecondition" in new SystemBuilderFixture {
        var updateExecuted = false
        world.addSystem(
          SystemBuilder[Position &: CNil]
            .withPrecondition(false)
            .withUpdate { (_, c, _) =>
              updateExecuted = true; c
            },
        )
        world.update(10)
        updateExecuted shouldBe false
      }
      "withBefore and withAfter" in new SystemBuilderFixture {
        var afterExecuted = false
        var beforeExecuted = false
        world.addSystem(
          SystemBuilder[Position &: CNil].withBefore { (_, _, _) => beforeExecuted = true }.withAfter { (_, _, _) =>
            afterExecuted = true
          }.withUpdate { (_, c, _) => c },
        )
        world.update(10)
        afterExecuted shouldBe true
        beforeExecuted shouldBe true
      }
    }
  }

  "A SystemBuilder" can {
    "be converted to an ExcludingSystemBuilder independently from when it is called" in new SystemBuilderFixture {
      var updateExecuted = false
      world.addSystem(
        SystemBuilder[Position &: CNil]
          .excluding[Velocity &: CNil]
          .withBefore { (_, _, _) => () }
          .withAfter { (_, _, _) => () }
          .withUpdate { (_, c, _) =>
            updateExecuted = true; c
          },
      )
      world.addSystem(
        SystemBuilder[Position &: CNil].withBefore { (_, _, _) => () }
          .excluding[Velocity &: CNil]
          .withAfter { (_, _, _) => () }
          .withUpdate { (_, c, _) =>
            updateExecuted = true; c
          },
      )
      world.addSystem(
        SystemBuilder[Position &: CNil].withBefore { (_, _, _) => () }.withAfter { (_, _, _) => () }
          .excluding[Velocity &: CNil]
          .withUpdate { (_, c, _) =>
            updateExecuted = true; c
          },
      )
      world.update(10)
      updateExecuted shouldBe false
    }
  }

  "An ExcludingSystemBuilder" should {
    "work with short update" in new SystemBuilderFixture {
      var updateExecuted = false
      world.addSystem(ExcludingSystemBuilder[Position &: CNil, Velocity &: CNil].withUpdate { (_, c, _) =>
        updateExecuted = true; c
      })
      world.update(10)
      updateExecuted shouldBe false
    }
    "work with full update" in new SystemBuilderFixture {
      var updateExecuted = false
      world.addSystem(ExcludingSystemBuilder[Position &: CNil, Velocity &: CNil].withUpdate { (_, c, _, _, _) =>
        updateExecuted = true; c
      })
      world.update(10)
      updateExecuted shouldBe false
    }
  }
}
