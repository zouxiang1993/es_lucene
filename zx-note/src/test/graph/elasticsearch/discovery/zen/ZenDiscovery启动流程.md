构造入口：  
1. org/elasticsearch/node/Node.java:448  初始化Discovery模块
```
final DiscoveryModule discoveryModule = new DiscoveryModule(this.settings, threadPool, transportService,
    namedWriteableRegistry, networkService, clusterService, pluginsService.filterPlugins(DiscoveryPlugin.class));
```

2. org/elasticsearch/discovery/DiscoveryModule.java:82 构造ZenDiscovery对象  
```
discoveryTypes.put("zen",
    () -> new ZenDiscovery(settings, threadPool, transportService, namedWriteableRegistry, clusterService, hostsProvider)); 
```

构造完成之后，加入集群的调用入口：  
1. org/elasticsearch/node/Node.java:706
```
discovery.start();
transportService.acceptIncomingRequests();
discovery.startInitialJoin();  
```