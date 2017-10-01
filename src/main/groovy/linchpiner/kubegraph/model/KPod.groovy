package linchpiner.kubegraph.model

import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import groovy.transform.TupleConstructor
import io.kubernetes.client.models.V1Pod

@TupleConstructor(includeSuperProperties=true)
@ToString(includePackage=false, includeSuperProperties=true, includeSuper=true, includeNames=true)
class KPod extends KNode {

    String phase
    
}
