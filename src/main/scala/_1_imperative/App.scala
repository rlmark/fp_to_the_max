package _1_imperative

import scala.io.StdIn
import scala.util.Random

object App {
  def main(args: Array[String]): Unit = {
    println("Hey there, let's play a guessing game. What is your name?")
    val name = StdIn.readLine()
    println(s"Great, $name, let's begin")
    var continue = true;
    while(continue) {
      println("I'm thinking of a number between 1 and 5. What's your guess")
      val targetNum = generateNumber(5)
      val playerGuess = StdIn.readLine()
      if (targetNum == playerGuess.toInt) {
        println(s"Good job, $name, you win!")
      } else println(s"Sorry! My number was $targetNum")

      continue = checkPlayerContinue()
    }
  }

  def generateNumber(upper: Int): Int = {
    Random.nextInt(upper) + 1
  }
  def checkPlayerContinue(): Boolean = {
    println("Do you want to continue?")
    val playerContinue = StdIn.readLine()
    playerContinue match {
      case "y" => true
      case "n" => false
    }
  }

}
