package linchpiner.kubegraph

import groovy.transform.Canonical
import linchpiner.kubegraph.file.KubegraphReader
import linchpiner.kubegraph.file.KubegraphWriter
import linchpiner.kubegraph.http.KubegraphServer
import linchpiner.kubegraph.kubernetes.KubeWatcher
import linchpiner.kubegraph.model.Change
import linchpiner.kubegraph.model.DataSet

class Main {
    
    def options = [
        http: [
            enabled:    true,
            port:       8080
        ],
        watcher: [
            enabled:    false,
            config:     "./config",
            namespace:  "default",
            file:       "rec/session.yaml"
        ],
        reader: [
            enabled:    true,
            file:       "rec/session.yaml",
        ]
    ]
    
    def start() {
        if (options.http.enabled) {
            startServer()
        }
        if (options.watcher.enabled) {
            if (options.watcher.file) {
                KubegraphWriter writer = new KubegraphWriter(new File(options.watcher.file))
                dataSet.listener = [
                    onChange: { Change c -> writer.write(c) }
                ]
                writer.start()
            }
            startWatcher()
        } else if (options.reader.enabled) {
            startReader()
        }
    }
    
    def startServer() {
        KubegraphServer server = new KubegraphServer(port: options.http.port)
        server.start()
    }
    
    def startWatcher() {
        KubeWatcher watcher = new KubeWatcher()
        watcher.config = options.watcher.config
        watcher.namespace = options.watcher.namespace
        
        def methods = ["pods", "rcs", "rss", "deployments", "services"]
        //methods << "nodes"
        methods.each { method ->
            Thread.start {
                while (true) {
                    watcher."${method}"()
                }
            }
        }
    }
    
    def startReader() {
        KubegraphReader reader = new KubegraphReader(file: new File(options.reader.file), dataSet: dataSet)
        reader.start()
    }
    
    DataSet getDataSet() {
        return DataSet.instance
    }
    
    Main parseCli(args) {
        def cli = new CliBuilder(
                usage:  "[options] command",
                header: "Options:",
                footer: footer,
                stopAtNonOption: false)
        cli.with {
            h longOpt: "help", "Show usage information"
            f longOpt: "file", args: 1, argName: "file", "File with cluster events"
            c longOpt: "config", args: 1, argName: "config", "K8s config ('${this.options.watcher.config}')"
            p longOpt: "port", args: 1, argName: "port", "HTTP server port (${this.options.http.port}), 0 to disable"
            n longOpt: "ns", args: 1, argName: "namespace", "K8s namespace ('${this.options.watcher.namespace}')"
        }
        def opts = cli.parse(args)
        def arguments = opts?.arguments()
        def command = arguments?.find { true }
        
        if (opts?.p =~ /\d+/) {
            options.http.port = Integer.valueOf(opts.p)
            if (options.http.port == 0) options.http.enabled = false
        }
        
        switch (command) {
            case "watch":
                options.watcher.enabled = true
                options.reader.enabled = false
                if (opts.c) options.watcher.config = opts.c
                if (opts.f) options.watcher.file = opts.f
                if (opts.n) options.watcher.namespace = opts.n
                break
            case "load":
                options.watcher.enabled = false
                options.reader.enabled = true
                if (opts.f) options.reader.file = opts.f
                break
            case "help":
            default:
                cli.usage()
                return null
        }
        
        return this
    }
    
    def footer = """\
    Commands:
     watch                  Connect to the cluster and watch it
     load                   Load saved events from the file
     help                   Show usage information
    """.stripIndent()
    
    static main(args) {
        Main main = new Main().parseCli(args)
        main?.start()
    }
    
}
