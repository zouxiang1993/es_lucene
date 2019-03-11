SynonymFilter已经弃用，建议使用SynonymGraphFilter + FlattenGraphFilter


SynonymFilter使用正向最大匹配规则，比如你有如下的同义词配置： 
```
   a -> x
   a b -> y
   b c d -> z
```
当输入  a b c d e 时会被解析成 y b c d