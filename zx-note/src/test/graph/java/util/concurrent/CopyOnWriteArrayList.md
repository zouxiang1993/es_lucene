### CopyOnWriteArrayList 

一种线程安全的List。使用“写时复制”的方法，当有新元素添加时，先从原有的数组中拷贝一份出来，
然后在新的数组做写操作，写完之后，再将原来的数组引用指向到新数组。

所有的写操作都是在锁的保护下进行的。这样就避免了多线程并发写时可能出现的问题。

所有的读操作都是不用加锁的。 

缺点： 
- 如果原数组很大，拷贝数组很消耗内存
- 不能用于实时读的场景。 只能保证最终一致性。 

使用场景： 
- 适合读多写少的场景。 
