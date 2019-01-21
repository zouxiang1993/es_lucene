Lucene60FieldInfosFormat  
xxx.fnm文件: 存储字段的信息  

主要内容: 
- FieldsCount : 字段总数
- 然后是每个字段的详细信息，包括: 
    - FieldName : 字段名称
    - FieldNumber : 字段编号 
    - FieldBits : 是否存储 term vectors, 是否禁用norms, 是否存储payloads
    - IndexOptions : 倒排表中存储哪些信息? 
        - DOCS_ONLY  
        - DOCS_AND_FREQS 
        - DOCS_AND_FREQS_AND_POSITIONS 
        - DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
    - DocValuesBits : 什么类型的DocValues? 
        - 0 : 无DocValues
        - 1 : NumericDocValues
        - 2 : BinaryDocValues
        - 3 : SortedDocValues 
    - DocValuesGen
    - Attributes
    - DimensionCount
    - DimensionNumBytes