xxx.si文件： 包含一个segment的元数据信息  
详见 Lucene62SegmentInfoFormat 的读写过程  

主要内容：   
- SegSize : segment中文档个数(最大文档编号，没有排除掉已经删除的文件)
- Files : 此segment包含哪些文件
- IndexSort : ......

