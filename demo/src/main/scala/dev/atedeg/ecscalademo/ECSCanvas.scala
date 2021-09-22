package dev.atedeg.ecscalademo

import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import scalafx.scene.canvas.{ Canvas, GraphicsContext }
import dev.atedeg.ecscalademo.Coordinates

case class ECSCanvas(canvas: Canvas) {
  private var graphicsContext: GraphicsContext = canvas.graphicsContext2D
  canvas.widthProperty().addListener(_ => updateCanvas)
  canvas.heightProperty().addListener(_ => updateCanvas)

  def drawCircle(coordinates: Coordinates, radius: Double, color: Color, lineWidth: Double): Unit = {
    graphicsContext.beginPath()
    graphicsContext.arc(coordinates.x, coordinates.y, radius, radius, 0, 360)
    graphicsContext.setFill(Color.Yellow)
    graphicsContext.setStroke(Color.Black)
    graphicsContext.lineWidth = lineWidth
    graphicsContext.fill()
    graphicsContext.stroke()
  }

  def drawLine(elementPosition: Coordinates, mousePosition: Coordinates): Unit = {
    graphicsContext.beginPath()
    graphicsContext.moveTo(elementPosition.x, elementPosition.y)
    graphicsContext.lineTo(mousePosition.x, mousePosition.y)
    graphicsContext.lineWidth = 1
    graphicsContext.setStroke(Color.Red)
    graphicsContext.stroke()
  }

  private def updateCanvas: Unit = {
    graphicsContext.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    drawCircle(Coordinates(30, 40), 20, Color.Blue, 4)
    drawLine(Coordinates(30, 40), Coordinates(100, 100))
  }
}
