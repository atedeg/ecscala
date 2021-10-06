package dev.atedeg.ecscalademo.systems

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, PlayState, Position, State, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.CollisionsFixture
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue, WritableSpacePartitionContainer }

class CollisionSystemTest extends AnyWordSpec with Matchers with ECScalaDSL {
  private val black = Color(0, 0, 0)

  "A CollisionSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates((playState, _) => CollisionSystem(playState, mock[WritableSpacePartitionContainer]))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
    }
  }

  "The CollisionSystem" should {
    "keep entities separated" in new CollisionsFixture {
      val stuckEntity1 = world hasAn entity withComponents {
        Position(0, 0) &: Velocity(0, 0) &: Circle(10, black) &: Mass(1)
      }
      val stuckEntity2 = world hasAn entity withComponents {
        Position(5, 0) &: Velocity(0, 0) &: Circle(10, black) &: Mass(1)
      }

      playState.gameState = State.Play
      world.update(1)
      val stuckPosition1 = stuckEntity1.getComponent[Position].get
      val stuckPosition2 = stuckEntity2.getComponent[Position].get
      val stuckRadius1 = stuckEntity1.getComponent[Circle].get.radius
      val stuckRadius2 = stuckEntity2.getComponent[Circle].get.radius

      val distanceBetweenStuckBalls = (stuckPosition1.position - stuckPosition2.position).norm
      distanceBetweenStuckBalls shouldBe (stuckRadius1 + stuckRadius2) +- 0.001
    }
    "compute the new velocities" in new CollisionsFixture {
      val collidingEntity1 = world hasAn entity withComponents {
        Position(20, 20) &: Velocity(100, 0) &: Circle(10, black) &: Mass(1)
      }
      val collidingEntity2 = world hasAn entity withComponents {
        Position(40, 20) &: Velocity(0, 0) &: Circle(10, black) &: Mass(1)
      }

      playState.gameState = State.Play
      world.update(1)
      val velocity1 = collidingEntity1.getComponent[Velocity].get
      val velocity2 = collidingEntity2.getComponent[Velocity].get

      velocity1.velocity shouldBe Vector(0, 0)
      velocity2.velocity shouldBe Vector(100, 0)
    }
  }
}
