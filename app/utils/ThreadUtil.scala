package utils

import scala.concurrent.forkjoin.ForkJoinPool

class PMCForkJoinPool extends ForkJoinPool {

  override def execute(task: Runnable) {


    super.execute(new Runnable {

      override def run = {
        task.run
      }

    })
  }
}