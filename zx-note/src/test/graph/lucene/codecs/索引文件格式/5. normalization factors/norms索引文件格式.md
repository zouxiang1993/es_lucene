参考资料：  
http://lucene.apache.org/core/6_6_1/core/org/apache/lucene/codecs/lucene53/Lucene53NormsFormat.html 

对于一个文档，每个字段都可以存储一个normalization因子，在查询时，这个因子将会被乘到这个字段的得分上。 

Lucene推荐使用查询时boost，因此一般不会用到这个norm因子。

索引文件：
- .nvm : norms metadata  
Norms metadata(.nvm) -> Entry^NumFields  
    - NumFields : 字段总数
    - Entry -> FieldNumber, BytesPerValue, Address
        - FieldNumber : 字段编号
        - BytesPerValue : 每个值占几个字节
        - Address ： 指向.nvd文件中的位置。 
- .nvd : norms data   
Norms data(.nvd) -> Data^NumFields
    - Data -> byte^MaxDoc * BytesPerValue 
    存储了每个文档在该字段的norm值。 