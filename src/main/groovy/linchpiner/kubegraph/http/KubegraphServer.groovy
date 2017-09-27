package linchpiner.kubegraph.http

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.ContextHandlerCollection
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.eclipse.jetty.server.handler.ContextHandler.Context
import org.eclipse.jetty.servlet.*
import groovy.servlet.*
import groovy.transform.TupleConstructor
import linchpiner.kubegraph.kubernetes.KubeWatcher
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.server.Server

@TupleConstructor
class KubegraphServer {

    int port = 8080
    
    def start() {
        def server = new Server(port)
        def handler = new ServletContextHandler(ServletContextHandler.SESSIONS)
        handler.contextPath = '/'
        handler.resourceBase = '.'
        handler.addServlet(DataSetServlet, '/js/dataset.js')
        handler.addServlet(UpdatesServlet, '/js/updates.js')
        handler.addServlet(DefaultServlet, '/').setInitParameter('resourceBase', './web')
        server.handler = handler
        server.start()
    }
    
}
