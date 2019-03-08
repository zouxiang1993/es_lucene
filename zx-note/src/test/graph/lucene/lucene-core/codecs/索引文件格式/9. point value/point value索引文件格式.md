k-d tree 参考资料:   
https://en.wikipedia.org/wiki/K-d_tree  
https://blog.sengxian.com/algorithms/k-dimensional-tree   

block k-d tree 参考资料:  
https://users.cs.duke.edu/~pankaj/publications/papers/bkd-sstd.pdf

索引文件格式参考资料：   
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene60/Lucene60PointsFormat.html

在一个block KD-tree中存储多维数据，可以快速支持**1维数据的范围查询**， N维形状的相交过滤。

这个数据结构写在磁盘上的一系列block中，并在内存中有一个二叉完全平衡树来指示这些block是如何切分的，
磁盘上的block是这个二叉树的叶子节点。 

1. .dim文件
- .dim文件包含每个field的 blocks以及index split values 
- Block 格式
    - count (vInt): block中文档总数
    - delta-docID (vInt^count) : 差值编码的docID列表，有序
    - packedValue^count : 所有维度的值一起编码到一个byte[]中
- Index split values格式： 
    - numDims (vInt) : 维数
    - maxPointsInLeafNode (vInt) : ??? 
    - bytesPerDim (vInt) : 每个维度占用字节数
    - count (vInt) : ???
    - packed index (byte[]) : ???

2. .dii文件
- fieldCount (vInt) ：field数目
- (fieldNumber(vInt), fieldFilePointer(vLong))^fieldCount 
    - fieldNumber : field编号
    - fieldFilePointer : 该field在.dim文件中的开始位置。
    
写入时，先写到内存缓冲区中(PointValuesWriter)，segment flush时，再flush到磁盘。  
    