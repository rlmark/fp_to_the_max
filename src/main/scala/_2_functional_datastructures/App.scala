package _2_functional_datastructures

import scala.io.StdIn
import scala.util.{Random, Try}

object App {
  def main(args: Array[String]): Unit = {
    println("Hey there, let's play a guessing game. What is your name?")
    val name = StdIn.readLine()
    println(s"Great, $name, let's begin")
    var continue = true;
    while(continue) {
      println("I'm thinking of a number between 1 and 5. What's your guess")
      val targetNum = generateNumber(5)
      val playerGuess = parseInt(StdIn.readLine())

      playerGuess match {
        case None => println("Please enter a number.")
        case Some(int) =>
          if (targetNum == int) {
            println(s"Good job, $name, you win!")
          } else println(s"Sorry! My number was $targetNum")
      }

      continue = checkPlayerContinue()
    }
  }

  def parseInt(s: String): Option[Int] = {
    Try(s.toInt).toOption
  }

  def generateNumber(upper: Int): Int = {
    Random.nextInt(upper) + 1
  }
  def checkPlayerContinue(): Boolean = {
    println("Do you want to continue? y/n")
    val playerContinue = StdIn.readLine()
    playerContinue match {
      case "y" => true
      case "n" => false
      case _ => false
    }
  }
}
