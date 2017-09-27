package linchpiner.kubegraph

import spock.lang.Specification

class MainSpec extends Specification {

    def "test empty command line"() {
        when:
            def m = main("")
        then:
            m == null
    }
    
    def "test help command"() {
        when:
            def m = main("help")
        then:       
            m == null
    }
    
    def "test load command before -f"() {
        when:
            def m = main("load -f test.yaml")
        then:
            m.options.reader.enabled
            m.options.watcher.enabled == false
            m.options.reader.file == "test.yaml"
    }

    def "test load command after -f"() {
        when:
            def m = main("-f test.yaml load")
        then:
            m.options.reader.enabled
            m.options.watcher.enabled == false
            m.options.reader.file == "test.yaml"
    }

    def "test watch command before -f"() {
        when:
            def m = main("watch -f test.yaml")
        then:
            m.options.reader.enabled == false
            m.options.watcher.enabled
            m.options.watcher.file == "test.yaml"
    }

    def "test watch command after -f"() {
        when:
            def m = main("-f test.yaml watch")
        then:
            m.options.reader.enabled == false
            m.options.watcher.enabled
            m.options.watcher.file == "test.yaml"
    }

    def "test config option"() {
        when:
            def m = main("watch -c ~/.kube/config")
        then:
            m.options.watcher.config == "~/.kube/config"
    }
    
    def "test port 9999"() {
        when:
            def m = main("watch -p 9999")
        then:
            m.options.http.enabled
            m.options.http.port == 9999
            m.options.watcher.enabled
            m.options.reader.enabled == false
    }
    
    def "test port 0"() {
        when:
            def m = main("watch -p 0")
        then:
            m.options.http.enabled == false
    }
    
    def "test long options"() {
        when:
            def m = main("watch --file file --port 9999 --config config --ns namespace")
        then:
            m.options.http.enabled
            m.options.http.port == 9999
            m.options.watcher.enabled
            m.options.watcher.config == "config"
            m.options.watcher.namespace == "namespace"
            m.options.watcher.file == "file"
            m.options.reader.enabled == false
    }
        
    Main main(String cmdline) {
        return new Main().parseCli(cmdline.split(' '))
    }
}
