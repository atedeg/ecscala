trait GameLoop {
  val fps = IntegerProperty(0)
  def start: Unit
  def stop: Unit
}

object GameLoop {
  def apply(handler: Double => Unit): GameLoop = GameLoopImpl(handler)
  private class GameLoopImpl(handler: Double => Unit) extends GameLoop {
    private val animationTimer = new JfxAnimationTimer() {
      override def handle(now: Long): Unit = {
        // calculate delta and FPS
        handler(delta)
      }
    }
    override def start: Unit = animationTimer.start()
    override def stop: Unit = animationTimer.stop()
  }
}