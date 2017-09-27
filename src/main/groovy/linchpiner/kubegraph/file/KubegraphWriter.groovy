package linchpiner.kubegraph.file

import groovy.util.logging.Log4j
import java.io.File
import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.util.concurrent.LinkedBlockingQueue

@Log4j
class KubegraphWriter {
    
    File file
    LinkedBlockingQueue<Change> queue = new LinkedBlockingQueue<Change>()
    
    
    KubegraphWriter(File file) {
        this.file = file
        file.text = ""
    }
    
    def start() {
        Thread.start {
            DumperOptions options = new DumperOptions()
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            Yaml yaml = new Yaml(options)
            while (true) {
                Change c = queue.take()
                realWrite(yaml, c)
            }
        }
    }
    
    def write(Change c) {
        queue.put(c)
    }
    
    def realWrite(Yaml yaml, Change c) {
        def map = [
            change: [
                type: c.type.toString(),
                uid: c.watchable.uid,
                timestamp: c.timestamp,
            ] + asMap(c.watchable)
        ]
        file << yaml.dump([ map ])
    }
    
    def asMap(KNode node) {
        return [
            kind: node.kind,
            name: node.name,
            version: node.version,
        ]
    }
    
    def asMap(KEdge edge) {
        return [
            from: edge.from,
            to: edge.to,
        ]
    }

}
