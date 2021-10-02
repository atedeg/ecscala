package dev.atedeg.ecscalademo.util

import dev.atedeg.ecscalademo.{ MouseState, State }

trait Both
case object Both extends Both

extension (ternaryBoolean: Boolean | Both) {
  def combineWith[T](value: T): Seq[(T, Boolean)] = ternaryBoolean match {
    case _: Both => Seq((value, true), (value, false))
    case other: Boolean => Seq((value, other))
  }
  def toBooleans: Seq[Boolean] = ternaryBoolean match {
    case _: Both => Seq(true, false)
    case other: Boolean => Seq(other)
  }
}

extension[A, B, C] (nestedTuple: ((A, B), C)) {
  def flatten: (A, B, C) = (nestedTuple._1._1, nestedTuple._1._2, nestedTuple._2)
}

object PreconditionTesting {
  def respectItsPreconditions(allowedStates: (State, Boolean | Both, Boolean | Both, Boolean | Both)*) = {
    val allAllowedStates = allowedStates flatMap { allowedState =>
      val (state, clicked, down, up) = allowedState
      clicked.toBooleans flatMap(down combineWith _) flatMap(up combineWith _) map flatten map { triple =>
        (state, MouseState(clicked = triple._1, down = triple._2, up = triple._3))
      }
    }
    ???
  }

  private def allStates: Seq[(State, MouseState)] =
    for {
      clicked <- Seq(true, false)
      down <- Seq(true, false)
      up <- Seq(true, false)
      state <- State.values
    } yield (state, MouseState(clicked = clicked, down = down, up = up))
}
