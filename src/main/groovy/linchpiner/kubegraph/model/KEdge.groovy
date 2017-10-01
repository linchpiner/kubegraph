package linchpiner.kubegraph.model

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes="uid")
class KEdge implements Watchable {
    
    String uid
    String kind = "edge"
    String from
    String to

    KEdge(map) {
        from = map.from
        to = map.to
        uid = from + to  
    }    
    
}
