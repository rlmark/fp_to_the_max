package _4_tagless_final

import scala.io.StdIn
import scala.util.Try

// Sequential composition effect
trait Program[F[_]] {
  def finish[A](a: => A): F[A]

  def chain[A,B](fa: F[A], atb: A => F[B]): F[B]

  def map[A,B](fa: F[A], f: A => B): F[B]
}

object Program {
  def apply[F[_]](implicit F: Program[F]) = F

  // You make nice syntax via implicit classes in Scala
  // Implicit classes must have a primary constructor with exactly 1 argument
  // HERE's WHY: So if we have an instance of Program in scope, any F[_] will have map and flatmap for free!
  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit programF: Program[F]) = {
      programF.map(fa, f)
    }
    def flatMap[B](f: A => F[B])(implicit programF: Program[F]) = {
      programF.chain(fa, f)
    }
  }
  def pure[A, F[_]](a: A)(implicit programF: Program[F]) = {
    programF.finish(a)
  }
}

// Effect for printing / reading
trait Console[F[_]] {
  def putStrLine(s: String):F[Unit]

  def getStrLine(): F[String]
}
object Console {
  def apply[F[_]](implicit F: Console[F]) = F
}
object ConsoleHelpers {
  def putLine[F[_]: Console](s: String): F[Unit] = Console[F].putStrLine(s)
  def getLine[F[_]: Console](): F[String] = Console[F].getStrLine()
}

// Effect for creating random numbers
trait Random[F[_]] {
  def nextInt(upper: Int): F[Int]
}
object Random {
  def apply[F[_]](implicit random: Random[F]): Random[F] = random
}
object RandomHelpers {
  def nextInt[F[_]](upper: Int)(implicit F: Random[F]): F[Int] = Random[F].nextInt(upper)
}

case class IO[A](unsafeRun: () => A) { self =>
  def map[B](f: A => B): IO[B] = {
    IO[B](() => f(self.unsafeRun()))
  }

  def flatMap[B](f: A => IO[B]): IO[B] = {
    /* note: while this flatMap implementation compiles: f(this.unsafeRun), this is evaluated immediately.
    This doesn't give us the ability to separate program definition from its execution. */
    IO[B](() => f(self.unsafeRun()).unsafeRun())
  }
}

object IO {
  def pure[A](a: A): IO[A] = IO(() => a)

  implicit val ioProgram: Program[IO] = new Program[IO] {
    override def finish[A](a: => A): IO[A] = pure(a)

    override def chain[A, B](fa: IO[A], atb: A => IO[B]): IO[B] = fa.flatMap(atb)

    override def map[A, B](fa: IO[A], f: A => B): IO[B] = fa.map(f)
  }

  implicit val consoleIO: Console[IO] = new Console[IO] {
    override def putStrLine(s: String): IO[Unit] = IO(() => println(s))

    override def getStrLine(): IO[String] = IO(() => StdIn.readLine())
  }

  implicit val randomIO: Random[IO] = new Random[IO] {
    override def nextInt(upper: Int): IO[Int] =  IO(() => scala.util.Random.nextInt(upper) + 1)
  }
}

object Main {
  import Program._
  import ConsoleHelpers._
  import RandomHelpers._

  def main(args: Array[String]): Unit = {
    main[IO](args).unsafeRun()
  }

  def main[F[_]: Console: Random: Program](args: Array[String]): F[Unit] = {
    val program = for {
      _ <- putLine("Hey there, let's play a guessing game. What is your name?")
      name <- getLine()
      _ <- putLine(s"Great, $name, let's begin")
      _ <- gameLoop(name)
    } yield ()

    program
  }

  def parseInt(s: String): Option[Int] = {
    Try(s.toInt).toOption
  }

  def gameLoop[F[_]: Program :Random :Console](name: String): F[Unit] = {
    for {
      _ <- putLine("I'm thinking of a number between 1 and 5. What's your guess")
      targetNum <- nextInt(5)
      playerGuess <- getLine()
      _ <- scoreGame(parseInt(playerGuess), targetNum, name)
      continue <- checkPlayerContinue()
      _ <- if(continue) gameLoop(name) else Program[F].finish(())
    } yield ()
  }

  def scoreGame[F[_]: Program :Random :Console](maybeGuess: Option[Int], targetNum: Int, name: String): F[Unit] = {
    maybeGuess match {
      case None => putLine("Please enter a number.")
      case Some(int) =>
        if (targetNum == int) {
          putLine(s"Good job, $name, you win!")
        } else putLine(s"Sorry! My number was $targetNum")
    }
  }

  def checkPlayerContinue[F[_]: Program :Console](): F[Boolean] = {
    for {
      _ <- putLine("Do you want to continue? y/n")
      playerContinue <- getLine()
      continue = playerContinue match {
        case "y" => true
        case "n" => false
        case _ => false
      }
    } yield continue
  }

}

