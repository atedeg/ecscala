package dev.atedeg.ecscalademo

import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import scalafx.scene.canvas.{ Canvas, GraphicsContext }

case class ECSCanvas(canvas: Canvas) {
  private var graphicsContext: GraphicsContext = canvas.graphicsContext2D
  canvas.widthProperty().addListener(_ => updateCanvas)
  canvas.heightProperty().addListener(_ => updateCanvas)

  def drawCircle(coordinates: Point, radius: Double, color: Color, lineWidth: Double): Unit = {
    graphicsContext.beginPath()
    graphicsContext.arc(coordinates.x, coordinates.y, radius, radius, 0, 360)
    graphicsContext.setFill(color)
    graphicsContext.setStroke(Color.Black)
    graphicsContext.lineWidth = lineWidth
    graphicsContext.fill()
    graphicsContext.stroke()
  }

  def drawLine(from: Point, to: Point, color: Color, lineWidth: Double): Unit = {
    graphicsContext.beginPath()
    graphicsContext.moveTo(from.x, from.y)
    graphicsContext.lineTo(to.x, to.y)
    graphicsContext.lineWidth = lineWidth
    graphicsContext.setStroke(color)
    graphicsContext.stroke()
  }

  private def updateCanvas: Unit = {
    graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    drawCircle(Point(30, 40), 20, Color.Blue, 4)
    drawLine(Point(30, 40), Point(100, 100), Color.Red, 1)
  }
}
