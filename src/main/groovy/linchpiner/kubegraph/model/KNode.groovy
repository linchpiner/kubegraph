package linchpiner.kubegraph.model

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor()
@EqualsAndHashCode(includes="uid")
@ToString(includePackage=false, includeSuperProperties=true, includeNames=true)
class KNode implements Watchable {

    String uid
    String kind
    String name
    long version
    boolean deleted = false

    boolean canEqual(Object other) {
        return other instanceof KNode
    }    
}
