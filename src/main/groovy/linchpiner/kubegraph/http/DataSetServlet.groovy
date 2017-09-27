package linchpiner.kubegraph.http

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode

class DataSetServlet extends JavaScriptServlet {
    
    void service(HttpServletRequest request, HttpServletResponse response) {
        response.contentType = "application/javascript"
        
        def nodes = dataSet.nodes.collect { KNode n -> asJson(n) }
        def edges = dataSet.edges.collect { KEdge e -> asJson(e) }
        
        response.writer.println "var _nodes = ${asJsonString(nodes)};"
        response.writer.println "var _edges = ${asJsonString(edges)};"
    }
    
}
