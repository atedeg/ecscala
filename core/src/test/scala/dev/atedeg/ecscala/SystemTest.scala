package dev.atedeg.ecscala

import dev.atedeg.ecscala.fixtures.{ Position, Velocity, ViewFixture }
import dev.atedeg.ecscala.util.types.given
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SystemTest extends AnyWordSpec with Matchers {

  "Systems" should {
    "be able tu update entities" in new ViewFixture {
      world.addSystem[Position &: Velocity &: CNil]((e, cl, v) => {
        val Position(px, py) &: Velocity(vx, vy) &: CNil = cl
        Position(px + 1, py + 1) &: Velocity(vx + 1, vy + 1)
      })

      world.update()

      world.getView[Position &: Velocity &: CNil] should contain theSameElementsAs List(
        (entity1, Position(2, 2) &: Velocity(2, 2)),
        (entity3, Position(2, 2) &: Velocity(2, 2)),
        (entity4, Position(2, 2) &: Velocity(2, 2)),
      )
    }
  }
}
