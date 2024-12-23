# invaders - detect space invaders

Plan:

1. Parse space invaders and radar samples.
2. Create tests.
3. Create a simple linear filter to locate invaders.

We will use clojure data structures for linear algebra here - we are supposed to showcase ours skills 
and not optimise performance.

# How to run?

lein run "resources/simple-1.txt" 0.75

for swiping the simple-1 radar input with threshold probability 0.75

It has been experimentally determined that 0.75 threshold works well for sample-1.


## License

Copyright © 2024 Marek Lipert

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
