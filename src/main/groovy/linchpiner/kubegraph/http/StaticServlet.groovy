package linchpiner.kubegraph.http

import groovy.text.GStringTemplateEngine
import groovy.ui.text.FindReplaceUtility.ReplaceAllAction

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StaticServlet extends HttpServlet {

    void service(HttpServletRequest request, HttpServletResponse response) {
        response.contentType = "text/html"
        
        def base = "/linchpiner/kubegraph/file/"
        def engine = new GStringTemplateEngine()
        def js = getClass().getResource(base + "init.js").text + getClass().getResource(base + "load.js").text
        def template = getClass().getResource(base + "main.html").text
        def bindings = [ javascript: js.toString(), mode: "load" ]
        response.writer.print(engine.createTemplate(template).make(bindings).toString())
    }
    
}
