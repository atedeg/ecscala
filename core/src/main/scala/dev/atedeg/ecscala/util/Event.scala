package dev.atedeg.ecscala.util

/**
 * This trait represents an event.
 * @tparam T
 *   the type of the parameters that need to be specified when the event is emitted.
 */
trait Event[T] {

  /**
   * When this method is called it represents the event taking place; it will result in executing all the registered
   * handlers.
   * @param eventParam
   *   the parameters that will be passed to all the registered handlers.
   */
  def apply(eventParam: T): Unit

  /**
   * @param handler
   *   an handler that will be executed when the event takes place.
   * @return
   *   a new [[Event]] with the added handler.
   */
  def registerHandler(handler: T => Unit): Event[T]

  /**
   * An alias for [[registerHandler]].
   */
  def +(handler: T => Unit) = registerHandler(handler)

  /**
   * @param handler
   *   the handler that will be removed.
   * @return
   *   a new [[Event]] with the removed handler.
   */
  def removeHandler(handler: T => Unit): Event[T]

  /**
   * An alias for [[removeHandler()]].
   */
  def -(handler: T => Unit) = removeHandler(handler)
}

object Event {
  def apply[T](): Event[T] = new EventImpl(Seq())
  private def apply[T](handlers: Seq[T => Unit]): Event[T] = new EventImpl(handlers)

  private class EventImpl[T](private val handlers: Seq[T => Unit]) extends Event[T] {
    override def apply(eventParam: T): Unit = handlers map (_(eventParam))
    override def registerHandler(handler: T => Unit): Event[T] = Event(handlers :+ handler)
    override def removeHandler(handler: T => Unit): Event[T] = Event(handlers filterNot (_ == handler))
  }
}
