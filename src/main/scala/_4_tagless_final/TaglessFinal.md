# Tagless Final

Tagless final is a style of programming where you constrain which types of effects you need 
throughout your business logic code, then supply an implementation of a class with those effects at the end 
when you wish to run your program. 

For example: `def gameLoop[F[_]: Program :Random :Console](name: String): F[Unit]`

In this signature we can see that our method, gameLoop will need 3 effects, Program, Random, and 
Console. Whereas before we only knew the concrete implementation IO, here our type constraints
make it clear what this method is using. 

### Why might we use Tagless Final? 

Testability: Instead of having to, say, import and use actual Tasks or Futures or IO's in our 
testing code, we can supply a much simpler, lighter-weight concrete implementation for our constraints. 

Types express more logic: Because we can see what the constraints are in the type signature
of our methods, understanding what the code is doing becomes easier. 

Compare our last two gameLoop signatures. 

`def gameLoop(name: String): IO[Unit]`

versus

`def gameLoop[F[_]: Program :Random :Console](name: String): F[Unit]`

One of these gives you much more of a hint about what is happening in the game than the other.
