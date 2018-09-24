# Custom Effects

Here we have written our own class IO which allows us finer grained control over when our program gets run.
A common functional pattern is to separate the definition of a program from its evaluation.

The problem with this approach is that we now have this one monad, IO, even in places where we might have chosen a different monad.
Our entire program is expressed in terms of IO, when often we really just need flatmap, pure, and map, i.e. we need an effectful context
but we actually might not care which one.



