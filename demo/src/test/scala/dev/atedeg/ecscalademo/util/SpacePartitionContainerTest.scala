package dev.atedeg.ecscalademo.util

import dev.atedeg.ecscalademo.fixtures.SpacePartitionContainerFixture
import dev.atedeg.ecscalademo.util.WritableSpacePartitionContainer
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SpacePartitionContainerTest extends AnyWordSpec with Matchers {

  "The space partition container" should {
    "add entities with the required components" in new SpacePartitionContainerFixture {
      val container = WritableSpacePartitionContainer()
      container add entity1
      container add entity2
      container add entity3
      noException should be thrownBy container.build()
    }
    "not add entities which don't have all the required components" in new SpacePartitionContainerFixture {
      val container = WritableSpacePartitionContainer()
      an[IllegalArgumentException] should be thrownBy (container add entity4)
      an[IllegalArgumentException] should be thrownBy (container add entity5)
    }
    "get added entities by their region" in new SpacePartitionContainerFixture {
      val container = WritableSpacePartitionContainer()
      container add entity1
      container add entity2
      container add entity3
      container.build()
      container.regionSize shouldBe 15
      container get (0, 0) should contain theSameElementsAs List(entity1, entity2)
      container get (1, 1) should contain theSameElementsAs List(entity3)
      container get (2, 2) shouldBe empty
    }
  }
}
