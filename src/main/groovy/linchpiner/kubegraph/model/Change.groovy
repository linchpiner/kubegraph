package linchpiner.kubegraph.model

import groovy.transform.Canonical

@Canonical
class Change {

    static enum Type {
        CREATE,
        UPDATE,
        DELETE    
    }
    
    Type type
    Watchable watchable
    long timestamp = System.currentTimeMillis()
    
}
