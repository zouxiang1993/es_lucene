### SolrSynonymMap
SolrSynonymMap(通过org.apache.lucene.analysis.synonym.SolrSynonymParser来构造)
可以看做是一个数据结构，存储**短语到同义词的映射关系**。  
SolrSynonymMap接收输入A，输出A的所有同义词 [S1, S2, S3 ...]  
SolrSynonymMap一般通过一个文件来配置
 
#### 配置文件说明 
1. 空行以及井号'#'开头的注释行被忽略
2. 显式映射。例如: 
    ```
    i-pod, i pod => ipod
    ```
    当接收到任何匹配到箭头左侧的短语时，会输出在箭头右侧的所有短语。显式的映射无视expand参数。

    显式映射时, A, B => C, D  
    对于A, B, C, D每个短语， 先用analyzer分词(可能有一些词干提取之类的功能)，再连成一个新的短语：
    A', B' => C', D'     
    然后对于每个输入输出对，添加一组映射关系：
    ```
    A' => C'  
    A' => D'   
    B' => C'  
    B' => D'
    此时，
    接受输入A', 输出[C', D']
    接受输入B', 输出[C', D']
    ```  
3. 等效同义词。例如： 
    ```
    ipod, i-pod, i pod
    ```
    假设配置了: A, B, C   
    对于每个短语，先用analyzer分词，再连成一个新的短语A', B', C'  
    此时，如果expand=true,则添加所有输入对之间的映射关系：
    ```
    for (int i = 0; i < inputs.length; i++) {
        for (int j = 0; j < inputs.length; j++) {
            if (i != j) {
              add(inputs[i], inputs[j], true);
            }
        }
    }
    
    A' => B' 
    A' => C'
    B' => A'
    B' => C'
    C' => A' 
    C' => B' 
    includeOrigin=true (同义词包含自身)
    
    此时,
    接受输入A', 输出[A', B', C']
    接受输入B', 输出[A', B', C']
    接受输入C', 输出[A', B', C']
    ```
    如果expand=false,则添加所有输入到第一个的映射关系：
    ```
    for (int i = 0; i < inputs.length; i++) {
        add(inputs[i], inputs[0], false);
    }
    
    A' => A' 
    B' => A'
    C' => A'
    includeOrigin=false
    
    此时，
    接受输入A', 输出[A']
    接受输入B', 输出[A']
    接受输入C', 输出[A']
    ```
4. 同义词配置项之间会自动合并，例如： 
    ```
    foo => foo bar
    foo => baz
    等价于: 
    foo => foo bar, baz
    ```
#### 配置时的注意事项
1. 如果配置了 i pod => ipod 的显式映射，那么在索引时倒排索引中只会写入ipod，
导致在查询时搜索"pod"不能召回有"i pod"的文档, 必须搜"i pod"才能召回