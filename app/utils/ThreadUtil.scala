package utils

import security.{LoggedInUser_1, LoggedInUser}
import scala.concurrent.forkjoin.ForkJoinPool

class PMCForkJoinPool extends ForkJoinPool {

  override def execute(task: Runnable) {

    val copyValue:LoggedInUser = LoggedInUser_1()

    super.execute(new Runnable {

      override def run = {
        LoggedInUser_1.set(copyValue)
        task.run
        println("resettttt")
        LoggedInUser_1.reset()
      }

    })
  }
}