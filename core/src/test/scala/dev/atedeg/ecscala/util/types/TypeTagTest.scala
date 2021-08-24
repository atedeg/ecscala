package dev.atedeg.ecscala.util.types

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TypeTagTest extends AnyWordSpec with Matchers {

  def assertTagsEqual[A, B](using ttA: TypeTag[A], ttB: TypeTag[B]): Unit = {
    ttA shouldEqual ttB
    ttB shouldEqual ttA
  }

  def assertTagsNotEqual[A, B](using ttA: TypeTag[A], ttB: TypeTag[B]): Unit = {
    ttA should not equal ttB
    ttB should not equal ttA
  }

  "A TypeTag[Int]" should {
    "be equal to TypeTag[Int]" in assertTagsEqual[Int, Int]
    "not be equal to TypeTag[String]" in assertTagsNotEqual[Int, String]
    "not be equal to TypeTag[Any]" in assertTagsNotEqual[Int, Any]
  }

  "A TypeTag[List[Int]]" should {
    "be equal to TypeTag[List[Int]]" in assertTagsEqual[List[Int], List[Int]]
    "not be equal to TypeTag[List[String]]" in assertTagsNotEqual[List[Int], List[String]]
    "not be equal to TypeTag[List[Any]]" in assertTagsNotEqual[List[Int], List[Any]]
  }
}
