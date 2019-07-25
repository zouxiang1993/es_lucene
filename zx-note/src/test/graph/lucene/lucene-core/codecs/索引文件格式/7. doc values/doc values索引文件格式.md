// TODO 

参考资料：  
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene54/Lucene54DocValuesFormat.html  
https://www.elastic.co/guide/cn/elasticsearch/guide/current/docvalues-intro.html
https://www.elastic.co/guide/cn/elasticsearch/guide/current/_deep_dive_on_doc_values.html    
https://www.elastic.co/guide/cn/elasticsearch/guide/current/aggregations-and-analysis.html  
https://blog.csdn.net/zteny/article/details/84627990  
https://blog.csdn.net/zteny/article/details/60633374  


Lucene支持五种类型的DocValues：

#### 1. NUMERIC
每个文档一个整数。 仅能处理单值的字段，且不支持浮点数，但是float和double可以重新编码成integer和long 

可以应用以下的一种或多种编码策略： 
- 差值压缩：先找出最小值，然后记录每个值与最小值的差值
- 表格压缩：如果这些值的基数(unique count)<256，那么使用一个简单的编码表，
然后每个文档存储表中的序号。序号也还是可以压缩的。 
- 最大公约数压缩： 如果所有的数字都有一个公共的除数(例如:用int表示日期，后面都有很多0)，
先会计算出最大公约数，然后再存储商。
- 单调压缩 : 如果所有的值是单调递增的，只需要记录每个值与上一个值的增量。  
仅适用于单调递增的数据组，例如存储文件地址之类比较连续的数据（见下面的BINARY）。
- Const-compressed : 如果只有一个可能的非空值，那么只需要编码缺失文档的位集。 
- 稀疏压缩 ： 只存储有值的文档。查询时使用二分查找。 

#### 2. BINARY
- 固定长度的二进制：所有数据写到一个大的byte[]中，每个文档占固定长度的字节数。
因此每个文档的值可以直接通过 docID*length 计算得到
- 可变长度的二进制： 所有数据写到一个大的byte[]中，记录每个文档的值的结束地址。  
结束地址使用"**单调压缩的数值形式**"来存储。  

#### 3. SORTED
可以看做是**字符串**类型。  
将所有的Term去重后排序，然后以**2.BINARY**的形式写成一个有序的词典。  
每个文档记录它的值在词典中的序号，这又可以使用 **1.NUMERIC** 的压缩策略。 

#### 4. SORTED_SET 
SORTED的升级版，支持多个字符串。
- Single： 如果所有的文档都只有0或1个值， 那么基本和SORTED相同
- SortedSet table :  如果set的基数<256，那么为每个set赋予一个id，生成一个查询表。  
然后每个文档记录它在表中的id，采用NUMERIC的压缩策略。 
- SortedSet : 将所有的Term去重后排序，然后以BINARY的形式写成一个有序的词典。  
序号列表 和 每个文档的位置指针分别使用NUMERIC的压缩策略。 

#### 5. SORTED_NUMERIC   
NUMERIC的升级版，支持多值。 每个文档的多个值是有序的。 
- Single： 如果所有文档都只有0或1个值，那么基本和NUMERIC相同
- SortedSet table： 如果set的基数<256，那么为每一个set赋予一个id，生成一个查询表。
然后每个文档记录它在表中的id，采用NUMERIC的压缩策略。  
- SortedNumeric： 值列表 和 每个文档的位置指针分别使用NUMERIC的压缩策略。  























