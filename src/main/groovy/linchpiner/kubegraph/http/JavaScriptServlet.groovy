package linchpiner.kubegraph.http

import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import groovy.json.JsonOutput
import linchpiner.kubegraph.model.*
import linchpiner.kubegraph.map.ModelMap

class JavaScriptServlet extends HttpServlet {
    
    def context
    
    void init(ServletConfig config) {
        super.init(config)
        context = config.servletContext
    }

    DataSet getDataSet() {
        return DataSet.instance
    }
    
    def asMap(KNode node) {
        return [
            id: node.uid,
            label: getName(node),
            group: getGroup(node),
            title: "Kind: ${node.kind}<br/>Name: ${node.name}<br/>Version: ${node.version}"
        ] 
    }
    
    String getName(KNode node) {
        return (node.name.length() > 10)
            ? node.name[0..3] + "..." + node.name[-4..-1]
            : node.name
    }
    
    String getGroup(KNode node) {
        return (node instanceof KPod && node.phase == "running") ? "${node.kind}-${node.phase}" : node.kind
    }
    
    def asMap(KEdge edge) {
        return [
            from: edge.from,
            to:   edge.to
        ]
    }
    
    def asJsonString(Watchable w) {
        return JsonOutput.toJson(asMap(w))
    }
    
    def asJsonString(List list) {
        return JsonOutput.toJson(list)
    }

}
