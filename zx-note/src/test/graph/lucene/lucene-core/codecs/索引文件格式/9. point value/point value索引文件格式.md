k-d tree 参考资料:   
https://en.wikipedia.org/wiki/K-d_tree  
https://blog.sengxian.com/algorithms/k-dimensional-tree   

block k-d tree 参考资料:  
https://users.cs.duke.edu/~pankaj/publications/papers/bkd-sstd.pdf

索引文件格式参考资料：   
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene60/Lucene60PointsFormat.html



### 参考资料： https://cloud.tencent.com/developer/article/1366830  

注：绿色箭头代表数据结构展开，红色箭头代表文件偏移（指针）

- .dii：记录每个字段的Point索引在.dim文件中的偏移，可理解为索引的索引。
    - count：字段个数。
    - offset：某字段Point Index的偏移。
- .dim：存储Point索引（BKD-Tree）的持久化数据。
    - field point index：每个字段的Point索引，多个字段顺序存储。
        - leaf block：BKD-Tree的叶子节点，存储point value到doc id的映射。
            - doc ids：每个叶子节点中doc id的集合。
            - dim prefix：存储Point Value各维度的公共前缀，降低存储成本。
            - point values：按doc id顺序，存储叶子节点point value的集合
        - packed index：按**中序遍历**方式存储BKD-Tree的非叶子节点，每个节点包含切分维度、偏移等信息。
            - left block offset：当前节点的最左叶子节点的偏移，用于快速跳至叶子节点起始位置。
            - split dim：切分维度的下标。
            - split value：切分维度的值。
            - left total bytes：当前节点左子树占用的总字节数，结合left block offset，可快速跳至右子树的起始位置。
     
当用户对某字段进行条件查询时，可以先通过.dii获取该字段的Point索引（BKD-Tree）偏移，
然后在.dim中定位BKD-Tree的非叶子节点（packed index），按照切分维度信息遍历BKD-Tree
得到符合条件的叶子节点，最后读取叶子节点过滤得到最终的doc id集合。


在实际场景中，我们更多的接触的是一维场景，即便是ES中整形字段包含多值的情况，
也是被按值拆分为多个point，因此仍属于一维场景。在实际实现中，Lucene对一维场景做了优化，但总体思路不变