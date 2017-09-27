package linchpiner.kubegraph.http

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import groovy.json.JsonOutput
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.Watchable

class JavaScriptServlet extends HttpServlet {
    
    def context
    
    void init(ServletConfig config) {
        super.init(config)
        context = config.servletContext
    }

    DataSet getDataSet() {
        return DataSet.instance
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
    
    def asJsonString(List list) {
        return JsonOutput.toJson(list)
    }

}
