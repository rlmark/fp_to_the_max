# Imperative Style

A toy number guessing app that reads from and prints to the console. Done in an imperative style.

Exhibits some cardinal sins of functional programming:

* Mutability: we've got state contained in a var
* Non-determinism: calls to the same function with the same input return different results
* Not referentially transparent: Calls to some methods can fail in unexpected ways,
breaking equational reasoning about the program. Side effects are not reflected in types.

Brittle, but simple.