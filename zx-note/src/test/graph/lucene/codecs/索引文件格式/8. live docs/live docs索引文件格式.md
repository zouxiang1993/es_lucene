参考资料：   
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene50/Lucene50LiveDocsFormat.html

用.liv文件来存储哪些文档已经被删除，哪些没有。 

.liv文件不是一定存在的，只有当segment中有文档被删除时才会存在。 

.liv文件格式：
- .liv --> Int64^LongCount 

用一连串的8字节的Int64来表示的一个位集。位=1表示该文档存在，位=0表示该文档已经被删除。

加入一个segment中总共5个文档，doc0, doc1, doc2, doc3, doc4。  
其中doc1和doc2被删除了，那么它的.liv文件内容为：  
00000000 00000000 00000000 00000000 00000000 00000000 00000000 00011001    