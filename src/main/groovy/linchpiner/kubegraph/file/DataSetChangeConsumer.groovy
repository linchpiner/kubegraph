package linchpiner.kubegraph.file

import groovy.transform.TupleConstructor
import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode

@TupleConstructor
class DataSetChangeConsumer implements ChangeConsumer {
    
    DataSet dataSet
    long delay = 100L

    void start() {
    }
    
    void stop() {
    }
    
    void consume(Change c) {
        toDataSet(c, c.watchable)
        Thread.sleep(delay)
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
