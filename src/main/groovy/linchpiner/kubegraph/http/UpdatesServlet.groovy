package linchpiner.kubegraph.http

import groovy.servlet.ServletBinding
import javax.servlet.ServletConfig
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEdge
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.Watchable

class UpdatesServlet extends JavaScriptServlet {
    
    void service(HttpServletRequest request, HttpServletResponse response) {
        response.contentType = "application/javascript"
        while (true) {
            Change c = dataSet.queue.poll()
            if (c == null) break;
            switch (c.type) {
                case Change.Type.UPDATE:
                case Change.Type.CREATE:
                    response.writer.println(jsUpdate(c.watchable)) 
                    break
                case Change.Type.DELETE:
                    response.writer.println(jsRemove(c.watchable))
                    break
            }
        }
    }
    
    String jsUpdate(KNode node) {
        return "nodes.update(${asJsonString(node)});"
    }
    
    String jsUpdate(KEdge edge) {
        return "edges.update(${asJsonString(edge)});"
    }

    String jsRemove(KNode node) {
        return "nodes.remove('${node.uid}');"
    }
    
    String jsRemove(KEdge edge) {
        return "edges.remove('${edge.uid}');"
    }
}
