package _4_tagless_final

trait Program[F[_]] {
  def finish[A](a: => A): F[A]

  def chain[A,B](fa: F[A], atb: A => F[B]): F[B]

  def map[A,B](fa: F[A], f: A => B): F[B]
}
object Program{
  def apply[F[_]](implicit F: Program[F]) = F

  // You make nice syntax via implicit classes in Scala
  // Implicit classes must have a primary constructor with exactly 1 argument
  // HERE's WHY: So if we have an instance of Program in scope, any F[_] will have map and flatmap for free!
  implicit class ProgramSyntax[F[_], A](fa: F[A]) {
    def map[B](f: A => B)(implicit F: Program[F]) = {
      F.map(fa, f)
    }
    def flatMap[B](f: A => F[B])(implicit F: Program[F]) = {
      F.chain(fa, f)
    }
  }

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

  implicit val ioProgram = new Program[IO] {
    override def finish[A](a: => A): IO[A] = pure(a)

    override def chain[A, B](fa: IO[A], atb: A => IO[B]): IO[B] = fa.flatMap(atb)

    override def map[A, B](fa: IO[A], f: A => B): IO[B] = fa.map(f)
  }
}



object App {
  def main(args: Array[String]): Unit = {
    val program = for {
      _ <- printlnIO("Hey there, let's play a guessing game. What is your name?")
      name <- readlnIO()
      _ <- printlnIO(s"Great, $name, let's begin")
      _ <- gameLoop(name)
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

  def gameLoop(name: String): IO[Unit] = {
    for {
      _ <- printlnIO("I'm thinking of a number between 1 and 5. What's your guess")
      targetNum <- generateNumber(5)
      playerGuess <- readlnIO()
      _ <- scoreGame(parseInt(playerGuess), targetNum, name)
      continue <- checkPlayerContinue()
      _ <- if(continue) gameLoop(name) else IO.pure(())
    } yield ()
  }

  def scoreGame(maybeGuess: Option[Int], targetNum: Int, name: String): IO[Unit] = {
    maybeGuess match {
      case None => printlnIO("Please enter a number.")
      case Some(int) =>
        if (targetNum == int) {
          printlnIO(s"Good job, $name, you win!")
        } else printlnIO(s"Sorry! My number was $targetNum")
    }
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

