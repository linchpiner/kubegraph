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
    ChangeConsumer consumer
    
    def start() {
        consumer.start()
        log.info "KubegraphReader loading from '${file}'"
        
        new Yaml().load(file.text).each {
            def yc = it.change
            Change c = new Change(
                    type: Change.Type.valueOf(yc.type),
                    timestamp: yc.timestamp,
                    watchable: watchable(yc))
            consumer.consume(c)
        }
        
        consumer.stop()
        log.info "KubegraphReader finished"
    }
    
    Watchable watchable(yc) {
        // FIXME
        if (yc.version) {
            return new KNode(uid: yc.uid, kind: yc.kind, name: yc.name, version: yc.version)
        } else {
            return new KEdge(from: yc.from, to: yc.to)
        }
    }
    
}
