package dev.atedeg.ecscala

trait Component

sealed trait CList
sealed trait CNil extends CList
case object CNil extends CNil

extension [H <: Component, T <: CList](head: H)
  def &:(tail: T): H &: T = dev.atedeg.ecscala.&:(head, tail)

final case class &:[+C <: Component, +L <: CList](h: C, t: L)
