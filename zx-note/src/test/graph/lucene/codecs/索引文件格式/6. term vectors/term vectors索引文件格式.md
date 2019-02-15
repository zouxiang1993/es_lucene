参考资料：  
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene50/Lucene50TermVectorsFormat.html

对于一个文档，每个字段都可以存储term vector(词向量，有时也称为document vector,文档向量)。  
词向量由<Term, TermFrequency>构成。此外term vector还可以存储position, offset, payload等信息。

Term vectors的存储格式与stored fields十分相似，都是存储在压缩的数据块中，
并保证一个文档的数据不会跨越多个块。

Terv vectors用两个文件来存储：
- .tvd文件。用来存储Term, frequency, position, offset 和 payload等信息
- .tvx文件。运行时被全部加载到内存，用来加速.tvd文件的寻址。

一次term vector查询最多只需要一次磁盘寻道。 