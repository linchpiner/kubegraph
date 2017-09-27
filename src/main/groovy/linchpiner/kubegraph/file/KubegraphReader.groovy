package linchpiner.kubegraph.file

import org.yaml.snakeyaml.Yaml

import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import linchpiner.kubegraph.kubernetes.KubeWatcher.Type
import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.Watchable

@TupleConstructor
@Log4j
class KubegraphReader {
    
    File file
    DataSet dataSet
    long delay = 100L
    
    def start() {
        log.info "Play started"
        new Yaml().load(file.text).each {
            def yc = it.change
            Change c = new Change(
                    type: Change.Type.valueOf(yc.type),
                    timestamp: yc.timestamp,
                    watchable: watchable(yc))
            toDataSet(c, c.watchable)
            Thread.sleep(delay)
        }
        log.info "Play finished"
    }
    
    Watchable watchable(yc) {
        // FIXME
        if (yc.version) {
            return new KNode(uid: yc.uid, kind: yc.kind, name: yc.name, version: yc.version)
        } else {
            return new KEdge(from: yc.from, to: yc.to)
        }
    }
    
    def toDataSet(Change c, KNode node) {
        switch (c.type) {
            case Change.Type.CREATE:
                dataSet.create(node)
                break
            case Change.Type.UPDATE:
                dataSet.update(node)
                break
            case Change.Type.DELETE:
                dataSet.delete(node)
                break
        }
    }
    
    def toDataSet(Change c, KEdge edge) {
        switch (c.type) {
            case Change.Type.CREATE:
                dataSet.link(edge.from, edge.to)
                break
        }
    }

}
