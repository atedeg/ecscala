package dev.atedeg.ecscalademo.util

import dev.atedeg.ecscalademo.fixtures.RegionAssignmentFixture
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SpacePartitionContainerTest extends AnyWordSpec with Matchers {

  "The space partition container" should {
    "add entities with the required components" in new RegionAssignmentFixture {
      val container = WritableSpacePartitionContainer()
      container add (entity1, entity1Components)
      container add (entity2, entity2Components)
      container add (entity3, entity3Components)
      noException should be thrownBy container.build()
    }
    "get added entities by their region" in new RegionAssignmentFixture {
      val container = WritableSpacePartitionContainer()
      container add (entity1, entity1Components)
      container add (entity2, entity2Components)
      container add (entity3, entity3Components)
      container.build()
      container.regionSize shouldBe 20
      container get (0, 0) map (_._1) should contain theSameElementsAs List(entity1, entity2)
      container get (1, 1) map (_._1) should contain theSameElementsAs List(entity3)
      container get (2, 2) shouldBe empty
    }
  }
}
