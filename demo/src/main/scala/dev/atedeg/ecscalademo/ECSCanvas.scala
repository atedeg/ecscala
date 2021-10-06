package dev.atedeg.ecscalademo

import scalafx.scene.canvas.{ Canvas, GraphicsContext }
import scalafx.scene.paint.Color as ColorFx
import scalafx.scene.shape.ArcType
import dev.atedeg.ecscalademo.Color

/**
 * This trait is an abstraction of a Canvas that can be used to draw the demo components.
 */
trait ECSCanvas {

  /**
   * @param coordinates
   *   The point where to draw the ball.
   * @param radius
   *   The ball radius.
   * @param color
   *   The ball color.
   * @param lineWidth
   *   The thickness of the ball's border.
   */
  def drawCircle(coordinates: Point, radius: Double, color: Color, lineWidth: Double): Unit

  /**
   * @param from
   *   The starting point of the line.
   * @param to
   *   The end point of the line.
   * @param color
   *   The line color.
   * @param lineWidth
   */
  def drawLine(from: Point, to: Point, color: Color, lineWidth: Double): Unit

  /**
   * Remove all the elements from the Canvas.
   */
  def clear(): Unit

  /**
   * @return
   *   the width of the canvas.
   */
  def width: Double

  /**
   * @return
   *   the height of the canvas.
   */
  def height: Double
}

/**
 * Object that uses the ScalaFX Canvas to draw the elements.
 */
object ScalaFXCanvas {
  def apply(canvas: Canvas): ECSCanvas = new ScalaFXCanvasImpl(canvas)

  private class ScalaFXCanvasImpl(canvas: Canvas) extends ECSCanvas {
    private val graphicsContext: GraphicsContext = canvas.graphicsContext2D
    private val defaultCanvasWidth = 760.0
    private val defaultCanvasHeight = 467.0

    override def drawCircle(coordinates: Point, radius: Double, color: Color, lineWidth: Double): Unit = {
      graphicsContext.beginPath()
      graphicsContext.arc(coordinates.x, coordinates.y, radius, radius, 0, 360)
      graphicsContext.setFill(ColorFx.rgb(color.r, color.g, color.b))
      graphicsContext.setStroke(ColorFx.Black)
      graphicsContext.lineWidth = lineWidth
      graphicsContext.fill()
      graphicsContext.stroke()
    }

    override def drawLine(from: Point, to: Point, color: Color, lineWidth: Double): Unit = {
      graphicsContext.beginPath()
      graphicsContext.moveTo(from.x, from.y)
      graphicsContext.lineTo(to.x, to.y)
      graphicsContext.lineWidth = lineWidth
      graphicsContext.setStroke(ColorFx.rgb(color.r, color.g, color.b))
      graphicsContext.stroke()
    }

    override def clear(): Unit = graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)

    override def width: Double = if canvas.getWidth == 0.0 then defaultCanvasWidth else canvas.getWidth

    override def height: Double = if canvas.getHeight == 0.0 then defaultCanvasHeight else canvas.getHeight
  }
}
