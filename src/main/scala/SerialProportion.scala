import Functions.calculateSerialProportion

object SerialProportion extends App {

  val executionTime = Seq(14199, 6922, 4568, 3527, 2904, 2409, 2102, 1886, 1892, 1871).zipWithIndex

  calculateSerialProportion(executionTime)
}
