package linchpiner.kubegraph.model

import java.util.concurrent.ConcurrentLinkedQueue

import groovy.util.logging.Log4j

@Singleton
@Log4j
class DataSet {
    
    HashSet<KNode> nodes = new HashSet<>().asSynchronized()
    HashSet<KEdge> edges = new HashSet<>().asSynchronized()
    HashMap<String, KNode> nodemap = new HashMap<>().asSynchronized()       // uid -> node
    HashMap<String, String> namekind = new HashMap<>().asSynchronized()     // kind/name -> uid
    ConcurrentLinkedQueue<Change> queue = new ConcurrentLinkedQueue<>()
    
    HashMap<String, String> leid = new HashMap<>().asSynchronized() // uid -> kind/name
    HashMap<String, String> lenk = new HashMap<>().asSynchronized() // kind/name -> uid
    
    def listener = [
        onChange: { Change c -> }
    ]
    
    def _update(KNode w, Change.Type type, boolean deleted) {
        KNode _w = nodemap[w.uid]
        if (_w == null || _w.version <= w.version) {
            w.deleted = deleted
            nodemap[w.uid] = w
            def nk = namekindKey(w)
            namekind[nk] = deleted ? null : w.uid 
            
            if (w instanceof KEndpoint) {
                updateLinks(_w, w)
            } else {
                addChange(new Change(type: type, watchable: w))
            }
            
            nodes.remove(w)
            
            if (!deleted) {
                nodes.add(w)
                // check lazy edge
                def fuid = lenk[nk]
                if (fuid != null) {
                    link(fuid, w.uid)
                    lenk.remove(nk)
                    leid.remove(fuid)
                }
            } else {
                // remove lazy edge 
                def _nk = leid[w.uid]
                if (_nk != null) {
                    leid.remove(w.uid)
                    lenk.remove(_nk)
                }
            }
        }
    }
    
    def create(KNode node) {
        _update(node, Change.Type.CREATE, false)
    }
    
    def update(KNode node) {
        _update(node, Change.Type.UPDATE, false)
    }
        
    def delete(KNode node) {
        _update(node, Change.Type.DELETE, true)
    }
    
    def link(String fuid, String name, String kind) {
        def nk = namekindKey(name, kind)
        def tuid = namekind[nk]
        if (tuid) {
            link(fuid, tuid)
        } else {
            leid[fuid] = nk
            lenk[nk] = fuid
        }
    }
    
    def link(String fuid, String tuid) {
        def edge = new KEdge(from: fuid, to: tuid)
        if (!edges.contains(edge)) {
            edges.add(edge)
            addChange(new Change(type: Change.Type.CREATE, watchable: edge))
        }
    }
    
    def unlink(String fuid, String name, String kind) {
        def nk = namekindKey(name, kind)
        def tuid = namekind[nk]
        if (tuid) {
            unlink(fuid, tuid)
        }
    }
    
    def unlink(String fuid, String tuid) {
        def edge = new KEdge(from: fuid, to: tuid)
        edges.remove(edge)
        addChange(new Change(type: Change.Type.DELETE, watchable: edge))
    }
    
    def updateLinks(KEndpoint oldEP, KEndpoint newEP) {
        def newUids = oldEP ? newEP.uids - oldEP.uids : newEP.uids
        def oldUids = oldEP ? oldEP.uids - newEP.uids : []
        newUids.each { uid ->
            link(uid, newEP.name, "service")
        }
        oldUids.each { uid ->
            unlink(uid, newEP.name, "service")
        }
    }
    
    String namekindKey(String name, String kind) {
        return "${kind}/${name}"
    }
    
    String namekindKey(KNode w) {
        return namekindKey(w.name, w.kind)
    }
    
    def addChange(Change c) {
        queue.add(c)
        listener.onChange.call(c)
    }
    
}
