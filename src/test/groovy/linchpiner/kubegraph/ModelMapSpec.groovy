package linchpiner.kubegraph

import spock.lang.Specification
import linchpiner.kubegraph.map.ModelMap
import linchpiner.kubegraph.model.*

class ModelMapSpec extends Specification {

    def "test classForKind"() {
        expect:
            ModelMap.classForKind("node") == KNode
            ModelMap.classForKind("pod")  == KPod
            ModelMap.classForKind("edge") == KEdge
    }
    
    def "test asMap"() {
        when:
            def node = ModelMap.asMap(new KNode(uid: "nodeuid", name: "nodename"))
            println node
            def pod  = ModelMap.asMap(new KPod(uid: "poduid", name: "podname", phase: "running"))
            println pod
        then:
            node.uid  == "nodeuid"
            node.name == "nodename"
            pod.uid   == "poduid"
            pod.name  == "podname"
            pod.phase == "running"
    }   
     
}
