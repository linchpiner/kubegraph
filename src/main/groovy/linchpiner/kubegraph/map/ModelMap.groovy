package linchpiner.kubegraph.map

import linchpiner.kubegraph.model.*
import linchpiner.kubegraph.model.Watchable

@Singleton
class ModelMap {

    Map<String, Class> kindmap = [
        pod:        KPod,
        edge:       KEdge,
        endpoints:  KEndpoint
    ].withDefault { k -> KNode }.asSynchronized()
    
    static Class classForKind(String kind) {
        return ModelMap.instance.kindmap[kind]
    }
    
    static asMap(Watchable w) {
        return w.properties.findAll() { Map.Entry entry ->
            entry.key != "class"
        }
    }
    
}
