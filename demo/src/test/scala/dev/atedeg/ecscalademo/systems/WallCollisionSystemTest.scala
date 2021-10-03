package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.dsl.ECScalaDSL

import scala.language.implicitConversions
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.Inspectors.forAll
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.when
import dev.atedeg.ecscala.{ &:, CNil, World }
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ ECSCanvas, EnvironmentState, PlayState, Point, Position, State, Vector, Velocity }
import dev.atedeg.ecscalademo.fixtures.WallCollisionsFixture
import dev.atedeg.ecscalademo.util.{ checkAllStates, AnyValue, WritableSpacePartitionContainer }
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import scalafx.scene.paint.Color

class WallCollisionSystemTest
    extends AnyWordSpec
    with BeforeAndAfterEach
    with Matchers
    with ECScalaDSL
    with MockitoSugar
    with WallCollisionsFixture {

  override protected def beforeEach(): Unit = {
    playState.gameState = State.Play
    when(canvas.width) thenReturn 100.0
    when(canvas.height) thenReturn 100.0
    world.update(1)
  }

  "A WallCollisionSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates((playState, _) => WallCollisionSystem(playState, mock[EnvironmentState], mock[ECSCanvas]))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
    }

    "keep entities inside the canvas's borders" in
      checkViewElements { (position, _) =>
        position.x should (be >= 10.0 and be <= 90.0)
        position.y should (be >= 10.0 and be <= 90.0)
      }
    "change velocities to entities that collide with the canvas's borders" in
      checkViewElements { (position, velocity) =>
        velocity shouldBe Velocity(if position.x == 90.0 then -1 else 1, if position.y == 90.0 then -1 else 1)
      }
  }

  private def checkViewElements(f: (Position, Velocity) => Unit): Unit = {
    val view = getView[Position &: Velocity &: CNil] from world
    forAll(view map (_._2)) { comps =>
      val position &: velocity &: CNil = comps
      f(position, velocity)
    }
  }
}
