segment_N文件存储了当前所有活跃的segment信息。 

主要内容：  
- SegCount : segment总数   
- 对于每一个segment: <SegName, HasSegID, SegID, SegCodec, DelGen, DeletionCount, FieldInfosGen, DocValuesGen, UpdatesFiles>
- SegName : segment名称
- DeletionCount : segment中已删除的文档总数   
