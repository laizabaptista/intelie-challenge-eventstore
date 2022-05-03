## About

For that Implementation, I chose to represent the Event Store 
as a HashMap structure, in order to improve queries by event type.
In my reseachs I read about ```ConcurrentHashMap``` be appropriate class to Thread-Security, it's a enhancement of HashMap.

In that HashMap, the key is the event type and the value a list of
events. The list structure I used is an ArrayList, and for thread-safety
I used it with a ```Collections.synchronizedList```.

I could choose a different kind of list in the substructure, but I didn't because that choice would depend on the
principal usage of the EventStore.

For example,I thought to use TreeSet , but in this case duplications are allowed, for a concurrent approach,
the usage of ```ConcurrentSkipListSet``` would be the most suitable. TreeSet structure implements a Red-Black Tree,
what in other words means that add, contains and remove operations has a log(n) cost.

Some project choices:
- I've created an implementation for the EventIterator, what would help me if I chose to change between the usage of
ArrayList structure and another one;
- Some parts of the code were wrapped by a synchronized operation, in order to garantee thread-safety
- The tests don't have comments, because their namer are self explanatory
- As we could consider the in memory store approach as a hotspot, I chose to create a package for it. In that way,
another approaches could be created in different packages;
- The Tests organization, also had respected that hierarchical structure.


