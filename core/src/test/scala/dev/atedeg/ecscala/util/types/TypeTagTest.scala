package dev.atedeg.ecscala.util.types

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import dev.atedeg.ecscala.fixtures.{ ComponentsFixture, Position, Velocity }

class TypeTagTest extends AnyWordSpec with Matchers {

  "A TypeTag[Position]" should new ComponentsFixture {
    "be equal to TypeTag[Position]" in assertTagsEqual[Position, Position]
    "not be equal to TypeTag[Velocity]" in assertTagsNotEqual[Position, Velocity]
  }

  def assertTagsEqual[A, B](using ttA: TypeTag[A], ttB: TypeTag[B]): Unit = {
    ttA shouldEqual ttB
    ttB shouldEqual ttA
  }

  def assertTagsNotEqual[A, B](using ttA: TypeTag[A], ttB: TypeTag[B]): Unit = {
    ttA should not equal ttB
    ttB should not equal ttA
  }

  "The compiler" should {
    "not derive a TypeTag[List[Position]]" in assertTypeTagIsNotDerived("List[Position]")
    "not derive a TypeTag[Nothing]" in assertTypeTagIsNotDerived("Nothing")
    "not derive a TypeTag[Component]" in assertTypeTagIsNotDerived("Component")
    "derive a TypeTag[Position]" in assertTypeTagIsDerived("Position")
    "derive a TypeTag[Velocity]" in assertTypeTagIsDerived("Velocity")
  }

  inline def assertTypeTagIsNotDerived(inline tagType: String): ComponentsFixture =
    new ComponentsFixture { "summon[TypeTag[" + tagType + "]]" shouldNot typeCheck }

  inline def assertTypeTagIsDerived(inline tagType: String): ComponentsFixture =
    new ComponentsFixture { "summon[TypeTag[" + tagType + "]]" should compile }
}
