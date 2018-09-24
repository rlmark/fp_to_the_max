# Making use of Scala built ins

The second iteration of this code makes use of Scala data structures like Option and Try.

The improvement from before is that our functions are *total*.

A total function means that given any input value, there is an output value that is expressed in the function's return type.

We also replaced the while code block with a recursive call.

This buys us more safety, but the code is still imperative, and full of side effects.

