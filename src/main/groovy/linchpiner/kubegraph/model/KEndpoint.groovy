package linchpiner.kubegraph.model

import groovy.transform.ToString
import groovy.transform.TupleConstructor

@TupleConstructor(includeSuperProperties=true)
@ToString(includePackage=false, includeSuperProperties=true, includeSuper=true, includeNames=true)
class KEndpoint extends KNode {
    
    HashSet<String> uids = new HashSet<String>()
    
}
