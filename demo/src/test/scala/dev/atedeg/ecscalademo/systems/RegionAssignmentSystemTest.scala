package dev.atedeg.ecscalademo.systems

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{EnvironmentState, State}
import dev.atedeg.ecscalademo.fixtures.RegionAssignmentFixture
import dev.atedeg.ecscalademo.util.{AnyValue, WritableSpacePartitionContainer, checkAllStates}
import org.scalatestplus.mockito.MockitoSugar.mock

class RegionAssignmentSystemTest extends AnyWordSpec with Matchers {

  "A RegionAssignmentSystem" should {
    "run" when {
      "in an enabled state" in
        checkAllStates((playState, _) => RegionAssignmentSystem(playState, mock[WritableSpacePartitionContainer]))(
          (State.Play, AnyValue, AnyValue, AnyValue),
        )
    }
  }

  "A RegionAssignmentSystem" should {
    "assign a region to each entity" in new RegionAssignmentFixture {
      playState.gameState = State.Play
      world.update(0)
      spacePartition get (0, 0) should contain theSameElementsAs List(entity1, entity2)
      spacePartition get (1, 1) should contain theSameElementsAs List(entity3)
      spacePartition get (2, 2) shouldBe empty
    }
  }
}
