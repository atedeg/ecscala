package dev.atedeg.ecscala.util.types

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Position, Velocity }

class ComponentTagTest extends AnyWordSpec with Matchers {

  "A ComponentTag[Position]" should new ComponentsFixture {
    "be equal to ComponentTag[Position]" in assertTagsEqual[Position, Position]
    "not be equal to ComponentTag[Velocity]" in assertTagsNotEqual[Position, Velocity]
  }

  def assertTagsEqual[A, B](using ttA: ComponentTag[A], ttB: ComponentTag[B]): Unit = {
    ttA shouldEqual ttB
    ttB shouldEqual ttA
  }

  def assertTagsNotEqual[A, B](using ttA: ComponentTag[A], ttB: ComponentTag[B]): Unit = {
    ttA should not equal ttB
    ttB should not equal ttA
  }

  "The compiler" should {
    "not derive a ComponentTag[List[Position]]" in assertComponentTagIsNotDerived("List[Position]")
    "not derive a ComponentTag[Nothing]" in assertComponentTagIsNotDerived("Nothing")
    "not derive a ComponentTag[Component]" in assertComponentTagIsNotDerived("Component")
    "derive a ComponentTag[Position]" in assertComponentTagIsDerived("Position")
    "derive a ComponentTag[Velocity]" in assertComponentTagIsDerived("Velocity")
  }

  inline def assertComponentTagIsNotDerived(inline tagType: String): ComponentsFixture =
    new ComponentsFixture { "summon[ComponentTag[" + tagType + "]]" shouldNot typeCheck }

  inline def assertComponentTagIsDerived(inline tagType: String): ComponentsFixture =
    new ComponentsFixture { "summon[ComponentTag[" + tagType + "]]" should compile }
}