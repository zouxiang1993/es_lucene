参考资料：   
    http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene50/Lucene50StoredFieldsFormat.html  
    http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/compressing/CompressingStoredFieldsIndexWriter.html
    
stored fields 总共分两个文件存储。   
1.  数据文件: xxx.fdt  
当写入文档时，stored fields 先被写入到一个内存中的byte[]缓冲区，每当缓冲区达到16KB或者
其中文档数目达到128时，缓冲区的数据使用LZ4压缩后flush到磁盘，形成一个chuck。  

.fdt文件格式:
- FieldData (.fdt) --> Header, PackedIntsVersion, Chunk^ChunkCount, ChunkCount, DirtyChunkCount, Footer  
其中比较重要的3个部分: 
- ChunkCount : 文件中chuck的总数
- DirtyChunkCount : 过早的flush到磁盘的chuck总数
- Chunk --> DocBase, ChunkDocs, DocFieldCounts, DocLengths, CompressedDocs
- DocBase : 第一个文档的doc ID 
- ChunkDocs : chuck中的doc总数 
- DocFieldCounts : 每一个doc的field数目
- DocLengths : 压缩后每一个doc的byte[]长度，用于快速定位到一个doc
- CompressedDocs --> 多个文档Docs**压缩**后的形式
- Docs --> Doc^ChunkDocs
- Doc --> <FieldNumAndType, Value>^DocFieldCount
- FieldNumAndType : 后3位代表字段类型，其他的代表字段编号
- 字段类型 :  
0: Value is String  
1: Value is BinaryValue  
2: Value is Int  
3: Value is Float  
4: Value is Long  
5: Value is Double  
6, 7: unused
- 如果文档超过16KB，那么chuck可能只包含一个文档。一个文档不可能分布在多个chuck中，一个文档的所有字段都在同一个chunk中
- 一个chuck中，如果至少有1个文档过大，导致chuck超过了32KB,这个chuck可能会被压缩成多个16KB的LZ4 blocks。**这可以使StoredFieldVisitors解压最少的数据（如果它只需要靠前的field）**。
- 每个文档的原始长度被写入到chunk的元数据中。**一旦解压出了足够的信息，解压工具可以利用这些信息及时的停止解压**

2   索引文件: xxx.fdx 
- FieldsIndex (.fdx) --> Header, ChunkIndex, Footer  
读数据时， .fdx文件被完全加载到内存。  
其中主要记录了每个chuck中第一个文档的doc ID和 每个chuck的开始位置。用于快速定位文档所在的chuck  
详见: http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/compressing/CompressingStoredFieldsIndexWriter.html  

- ChunkIndex (.fdx) --> Block^BlockCount
- BlockCount是Block总数，并没有显式的存储

- Block --> BlockChunks, DocBases, StartPointers
- BlockChunks --> block中的chunk数目，0表示.fdx文件的结束
- DocBases --> DocBase, AvgChunkDocs, BitsPerDocBaseDelta, DocBaseDeltas
- DocBase --> block中第一个文档的docID
- AvgChunkDocs --> 每个chunk中的平均文档数目
- BitsPerDocBaseDelta -->  ... 



























