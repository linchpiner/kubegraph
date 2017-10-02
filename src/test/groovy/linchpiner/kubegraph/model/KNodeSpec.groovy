package linchpiner.kubegraph.model

import java.beans.ConstructorProperties

import spock.lang.Specification

class KNodeSpec extends Specification {

    def "test KNode properties"() {
        when:
            KNode node = new KNode(uid: "123", name: "node")
            println node.properties.keySet()
        then:
            node.properties.uid == "123"
            node.properties.name == "node"
    }
    
    def "test KPod properties"() {
        when:
            KNode pod = new KPod(uid: "123", name: "pod", phase: "running")
            println pod.properties.keySet()
        then:
            pod.properties.uid == "123"
            pod.properties.name == "pod"
            pod.properties.phase == "running"
    }
    
    def "test constructor"() {
        when:
            def r1 = new KNode(uid: "nodeuid")
            def r2 = new KPod(uid: "poduid")
        then:
            r1.uid == "nodeuid"
        and:
            r2.uid == "poduid"
    }
    
    def "test KNode equals and hashCode methods"() {
        when:
            def node1 = new KNode(uid: "uid1", name: "node")
            def node2 = new KNode(uid: "uid1", name: "othernode")
            def node3 = new KNode(uid: "uid2", name: "node")
            def node4 = new KPod(uid: "uid1", name: "pod")
            def node5 = new KPod(uid: "uid1", name: "otherpod")
            def node6 = new KPod(uid: "uid2", name: "pod")
        then:
            node1 == node2
            node1 != node3
            node4 == node5
            node4 != node6
            node1 == node4
    }
}
