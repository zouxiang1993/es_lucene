Lucene的FST(有限状态传感器)有以下特性: 
- 构建最小FST的过程快速且内存消耗低(但是输入必须有序)
- 构建完毕之后，整个FST全部用byte[]表示，反序列化快速且 Object的内存额外消耗低
- 支持按Output查找,如果Output是有序的话
- 可插拔的Outputs表示法
- N-最短路径
- 可以顺序迭代每一对<输入, 输出>  


本质上，FST是一个SortedMap<字节序列, 输出>,   
如果arc是有序的,则FST需要的内存比SortedMap少得多, 但是查询时更耗CPU  
FST的内存消耗少，这对Lucene是至关重要的，因为索引中可能有大量的Term   


#### FST Builder API
Builder类用于构造一个FST, 一个示例如下:
```
String inputValues[] = {"cat", "deep", "do", "dog", "dogs"};
long outputValues[] = {5, 10, 15, 2, 8};

PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
Builder<Long> builder = new Builder<Long>(FST.INPUT_TYPE.BYTE1, outputs);
IntsRefBuilder scratchInts = new IntsRefBuilder();
for (int i = 0; i < inputValues.length; i++) {
    BytesRef scratchBytes = new BytesRef(inputValues[i]);
    builder.add(Util.toIntsRef(scratchBytes, scratchInts), outputValues[i]);
}
FST<Long> fst = builder.finish();
```

1. 构造一个Builder对象，Builder(INPUT_TYPE inputType, Outputs<T> outputs)。  
其中inputType指明每一个输入符号占几个字节，有BYTE1, BYTE2, BYTE4三个可选值  
outputs则是FST的输出的运算抽象 
2. 依次加入每一对<输入，输出>对(输入必须有序)。调用add(IntsRef input, T output)。  
这里将输入串看作一个int[]， 但实际每一个输入符号占几个字节还是由INPUT_TYPE决定。 
3. 调用builder.finish()，生成FST

#### FST API 

#### FST Util API 
##### 1. 根据input查询output
```
BytesRef bytesRef = new BytesRef("dog");
IntsRef intsRef = Util.toIntsRef(bytesRef, new IntsRefBuilder());
Long result = Util.get(fst, intsRef);
```
##### 2. 查询第k小的key
在FST上用Dijikstra求最短路

##### 3. 根据output反向查找input(output必须也有序)