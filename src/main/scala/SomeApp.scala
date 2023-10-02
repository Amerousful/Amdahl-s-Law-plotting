import java.util.concurrent.Semaphore
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object SomeApp extends App {

  def time[T](f: => T)(name: String): T = {
    val start = System.nanoTime()
    val ret = f
    val end = System.nanoTime()
    println(s"Time taken [$name]: ${(end - start) / 1000 / 1000} ms")
    ret
  }

  def parallelWorkload() = {
    Thread.sleep(1)
  }

  val numThreads = 10

  time {
    val numElements = 10000

    val semaphore = new Semaphore(numThreads)
    val tasks = (1 to numElements).map { i =>
      Future {
        semaphore.acquire()
        parallelWorkload()
        semaphore.release()
      }
    }
    val result = Future.sequence(tasks)
    Await.result(result, Duration.Inf)
  }(s"Parallel work, threads count $numThreads")

}
