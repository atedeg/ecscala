package dev.atedeg.ecscalademo

import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import scalafx.scene.canvas.{ Canvas, GraphicsContext }

trait ECSCanvas {
  def drawCircle(coordinates: Point, radius: Double, color: Color, lineWidth: Double): Unit
  def drawLine(from: Point, to: Point, color: Color, lineWidth: Double): Unit
  def clear(): Unit
}

object ScalaFXCanvas {
  def apply(canvas: Canvas): ECSCanvas = new ScalaFXCanvasImpl(canvas)

  private class ScalaFXCanvasImpl(canvas: Canvas) extends ECSCanvas {
    private var graphicsContext: GraphicsContext = canvas.graphicsContext2D

    override def drawCircle(coordinates: Point, radius: Double, color: Color, lineWidth: Double): Unit = {
      graphicsContext.beginPath()
      graphicsContext.arc(coordinates.x, coordinates.y, radius, radius, 0, 360)
      graphicsContext.setFill(color)
      graphicsContext.setStroke(Color.Black)
      graphicsContext.lineWidth = lineWidth
      graphicsContext.fill()
      graphicsContext.stroke()
    }

    override def drawLine(from: Point, to: Point, color: Color, lineWidth: Double): Unit = {
      graphicsContext.beginPath()
      graphicsContext.moveTo(from.x, from.y)
      graphicsContext.lineTo(to.x, to.y)
      graphicsContext.lineWidth = lineWidth
      graphicsContext.setStroke(color)
      graphicsContext.stroke()
    }

    override def clear(): Unit = graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
  }
}
