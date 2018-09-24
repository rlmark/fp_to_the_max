package _3_custom_effects_wrapper

import scala.io.StdIn
import scala.util.{Random, Try}

case class IO[A](unsafeRun: () => A) { self =>
  def map[B](f: A => B): IO[B] = {
    IO[B](() => f(self.unsafeRun()))
  }

  def flatMap[B](f: A => IO[B]): IO[B] = {
    /* note: while this flatMap implementation compiles f(this.unsafeRun), this is evaluated immediately.
    This doesn't give us the ability to separate program definition from its execution. */
    IO[B](() => f(self.unsafeRun()).unsafeRun())
  }
}
object IO {
  def pure[A](a: A): IO[A] = IO(() => a)
}

object App {
  def main(args: Array[String]): Unit = {
    val program = for {
      _ <- printlnIO("Hey there, let's play a guessing game. What is your name?")
      name <- readlnIO()
      _ <- printlnIO(s"Great, $name, let's begin")
      _ <- gameloop(name)
    } yield ()

    program.unsafeRun()
  }

  def printlnIO(s: String): IO[Unit] = {
    IO(() => println(s))
  }

  def readlnIO(): IO[String] = {
    IO(() => StdIn.readLine())
  }

  def parseInt(s: String): Option[Int] = {
    Try(s.toInt).toOption
  }

  def generateNumber(upper: Int): IO[Int] = {
    IO(() => Random.nextInt(upper) + 1)
  }

  def gameloop(name: String): IO[Unit] = {
    for {
      _ <- printlnIO("I'm thinking of a number between 1 and 5. What's your guess")
      targetNum <- generateNumber(5)
      playerGuess <- readlnIO()
      validGuess = parseInt(playerGuess)
      _ <- validGuess match {
        case None => printlnIO("Please enter a number.")
        case Some(int) =>
          if (targetNum == int) {
            printlnIO(s"Good job, $name, you win!")
          } else printlnIO(s"Sorry! My number was $targetNum")
      }
      continue <- checkPlayerContinue()
      _ <- if(continue) gameloop(name) else IO.pure(())
    } yield ()
  }

  def checkPlayerContinue(): IO[Boolean] = {
    for {
      _ <- printlnIO("Do you want to continue? y/n")
      playerContinue <- readlnIO()
      continue = playerContinue match {
        case "y" => true
        case "n" => false
        case _ => false
      }
    } yield continue
  }

}
