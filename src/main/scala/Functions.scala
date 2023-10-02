import plotly._, element._, layout._, Plotly._

object Functions {

  def speedUpRate(parallelPercentage: Int, numberOfProcesses: Int): Float = {
    require(parallelPercentage > 0 && parallelPercentage < 100)
    require(numberOfProcesses > 0)

    val parallelPart: Float = parallelPercentage / 100.0f
    val serialPart: Float = 1.0f - parallelPart

    val originalValue = 1 / (serialPart + parallelPart / numberOfProcesses)

    val roundedValue = Math.round(originalValue * 100.0) / 100.0
    roundedValue.toFloat
  }

  def drawPlot(parallelPercentages: Seq[Int], numberOfProcesses: Int) = {
    val calculateAllOfValues = (percentage: Int) =>
      Seq.tabulate(numberOfProcesses)(i => speedUpRate(percentage, i + 1))

    val x = 0 to numberOfProcesses

    val plot = parallelPercentages.map(p =>
      Scatter(x, calculateAllOfValues(p))
        .withName(s"$p%")
        .withFill(Fill.ToNextY)
    )

    val lay = Layout().withTitle("Amdahl's law")
      .withXaxis(Axis().withTitle("Number of processes"))
      .withYaxis(Axis().withTitle("Speed-up"))

    plot.plot("results/plot.html", lay)
  }

  def calculateSerialProportion(timeAndNumber: Seq[(Int, Int)]) = {
    require(timeAndNumber.size > 2, "You have to provide more then 2 values. The more, the better.")
    val t1 :: tail = timeAndNumber

    val serialPart: Seq[(Double, Int)] = tail.map {
      case (time, number) => (1.0 - time.toDouble / t1._1, number)
    }

    // magic number means that we are too close to asymptote
    val coefficient = 1.5

    val difference: (Double, Double) => Double = (a, b) =>
      Math.abs((a - b) / a * 100.0).toFloat

    val q = serialPart.sliding(2).map {
      case head +: _ :+ last => difference(head._1, last._1)
    }.toSeq

    val indexOfLastMostEffectiveValue: Int = q
      .zipWithIndex
      .find(percent => percent._1 < coefficient)
      .map(percentIndex => percentIndex._2)
      .getOrElse(0)

    if (indexOfLastMostEffectiveValue > 0) {
      val asymptoteClosingBound: Int = tail(indexOfLastMostEffectiveValue)._1

      val resultOriginalValue = (asymptoteClosingBound.toFloat / t1._1) * 100.0
      val serialPercent = Math.round(resultOriginalValue * 100.0) / 100.0

      println(s"Serial proportion: $serialPercent%.\nThe most optimal number of process: $indexOfLastMostEffectiveValue")

      serialPercent
    } else {
      println("It couldn't figure out the value. You should try add more data.")
      0
    }

  }

}
