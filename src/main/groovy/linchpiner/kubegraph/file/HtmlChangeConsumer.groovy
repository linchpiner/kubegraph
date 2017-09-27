package linchpiner.kubegraph.file

import javax.management.InstanceOfQueryExp

import groovy.text.GStringTemplateEngine
import groovy.transform.TupleConstructor
import groovy.util.logging.Log4j
import linchpiner.kubegraph.kubernetes.KubeWatcher.Type
import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.Watchable
import groovy.json.JsonOutput

@Log4j
class HtmlChangeConsumer implements ChangeConsumer {

    File file
    StringBuilder js = new StringBuilder()
    def delay = 100 // ms
    
    HtmlChangeConsumer(File file) {
        this.file = file
    }
    
    void start() {
        js.append(getClass().getResource("init.js").text)
        js.append("var _events = [\n")
    }
    
    void stop() {
        js.append("];\n")
        js.append(getClass().getResource("play.js").text)
        def engine = new GStringTemplateEngine()
        def template = getClass().getResource("main.html").text
        def bindings = [ javascript: js.toString() ]
        file.text = engine.createTemplate(template).make(bindings).toString()
        log.info "HTML saved to '${file}'"
    }

    void consume(Change c) {
        def target = (c.watchable instanceof KNode) ? "nodes" : "edges"
        if (c.type == Change.Type.DELETE) {
            js.append("[${delay}, '${target}.remove(\"${c.watchable.uid}\");'],\n");
        } else {
            js.append("[${delay}, '${target}.update(${asJsonString(c.watchable)});'],\n");
        }
    }
    
    def asJson(KNode node) {
        return [
            id:    node.uid,
            label: node.name,
            group: node.kind,
            title: "Kind: ${node.kind}<br/>Name: ${node.name}<br/>Version: ${node.version}",
        ]
    }
    
    def asJson(KEdge edge) {
        return [
            id:   edge.uid,
            from: edge.from,
            to:   edge.to,
        ]
    }
    
    def asJsonString(Watchable w) {
        return JsonOutput.toJson(asJson(w))
    }

}
