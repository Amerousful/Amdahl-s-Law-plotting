import Functions.drawPlot

object DrawPlot extends App {

  val numberOfProcesses = 25
  val percentageValues = Seq(50, 75)

  drawPlot(percentageValues, numberOfProcesses)
}
