package linchpiner.kubegraph.model

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode

@Canonical
@EqualsAndHashCode(includes="uid")
class KNode implements Watchable {

    String uid
    String kind
    String name
    long version
    boolean deleted = false

}
