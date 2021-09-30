package dev.atedeg.ecscalademo.systems

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.fixtures.RegionAssignmentFixture
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer

class RegionAssignmentSystemTest extends AnyWordSpec with Matchers {

  "The RegionAssignmentSystem" should {
    "assign a region to each entity" in new RegionAssignmentFixture {
      world.update(0)
      spacePartition get (0, 0) should contain theSameElementsAs List(entity1, entity2)
      spacePartition get (1, 1) should contain theSameElementsAs List(entity3)
      spacePartition get (2, 2) shouldBe empty
    }
  }
}
