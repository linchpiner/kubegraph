package linchpiner.kubegraph.http

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class StaticServlet extends HttpServlet {

    void service(HttpServletRequest request, HttpServletResponse response) {
        response.contentType = "text/html"
        // FIXME
        response.writer.print(getClass().getResource("index.html").text)
    }
    
}
