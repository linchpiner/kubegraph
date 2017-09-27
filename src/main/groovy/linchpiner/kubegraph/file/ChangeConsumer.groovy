package linchpiner.kubegraph.file

import linchpiner.kubegraph.model.Change

interface ChangeConsumer {

    void start()
    void consume(Change c)
    void stop()
}
