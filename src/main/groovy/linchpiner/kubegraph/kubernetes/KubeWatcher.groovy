package linchpiner.kubegraph.kubernetes

import io.kubernetes.client.util.Config
import io.kubernetes.client.ApiClient
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.apis.ExtensionsV1beta1Api
import io.kubernetes.client.util.Watch
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.Change.Type

import java.util.prefs.FileSystemPreferences.NodeCreate

import com.google.gson.reflect.TypeToken
import groovy.transform.TupleConstructor
import groovy.util.logging.Slf4j
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1Node
import io.kubernetes.client.models.V1ReplicationController
import io.kubernetes.client.models.V1Service
import io.kubernetes.client.models.V1beta1ReplicaSet
import io.kubernetes.client.models.AppsV1beta1Deployment
import java.util.concurrent.TimeUnit

/**
 * KubeWatcher uses official Kubernetes client
 * https://github.com/kubernetes-client/java
 * Requires:
 * 1. SSH tunnel for port 6443 to the master
 * 2. Local 'config' from the master, change the server to 'https://master:6443'
 * 3. Record for '127.0.0.1 master' in local /etc/hosts
 */
@Slf4j
class KubeWatcher {

    def config = "./config"
    String namespace = "default"
    int timeout = 0
    
    ApiClient client
    Boolean doWatch = Boolean.TRUE
    
    KubeWatcher() {
        client = Config.fromConfig(config)
        Configuration.setDefaultApiClient(client)
        client.httpClient.setReadTimeout(0, TimeUnit.SECONDS)
    }
    
    def nodes() {
        watch  (call: v1Api.listNodeCall(null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<V1Node>>(){}.getType()) { item, node ->
        }
    }

    def pods() {
        watch  (call: v1Api.listNamespacedPodCall(namespace, null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<V1Pod>>(){}.getType()) { item, w ->
              
            def node = item.object.spec.nodeName
            if (node) ds.link(w.uid, node, "node")
                
            def owners = item.object.metadata.ownerReferences
            owners?.each { owner ->
                //ds.link(w.uid, owner.name, owner.kind.toLowerCase())
                ds.link(w.uid, owner.uid)
            }
                
        }
    }
    
    def rcs() {
        watch  (call: v1Api.listNamespacedReplicationControllerCall(namespace, null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<V1ReplicationController>>(){}.getType()) { item, w ->
        }
    }
    
    def rss() {
        watch  (call: extensionsV1beta1Api.listNamespacedReplicaSetCall(namespace, null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<V1beta1ReplicaSet>>(){}.getType()) { item, w ->
                
            def owners = item.object.metadata.ownerReferences
            owners?.each { owner ->
                //ds.link(w.uid, owner.name, owner.kind.toLowerCase())
                ds.link(w.uid, owner.uid)
            }
    
        }
    }
    
    def deployments() {
        watch  (call: extensionsV1beta1Api.listNamespacedDeploymentCall(namespace, null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<AppsV1beta1Deployment>>(){}.getType()) { item, w ->
        }
    }

    def services() {
        watch  (call: v1Api.listNamespacedServiceCall(namespace, null, null, null, null, timeout, doWatch, null, null),
                type: new TypeToken<Watch.Response<V1Service>>(){}.getType()) { item, w ->
/*                
            v1Api.listNamespacedEndpoints(namespace, null, null, null, null, timeout, false)
                .items
                .find { endpoint -> endpoint.metadata.name == w.name }?.subsets?.find { true }?.addresses?.each { addr ->
                    def uid = addr?.targetRef?.uid
                    if (uid != null) ds.link(w.uid, uid)
                }
*/                
        }
    }
    
    def watch(args, Closure c = null) {
        def watch = Watch.createWatch(client, args['call'], args['type'])
        watch.each { item -> toDataSet(item, c) }
    }
    
    def toDataSet(item, c) {
        def object = item.object
        KNode w = new KNode(
                uid: object.metadata.uid, 
                kind: object.kind.toLowerCase(), 
                name: object.metadata.name,
                version: Long.valueOf(object.metadata.resourceVersion))
        toDataSetMethod(item, w) 
        if (c != null) {
            c.call(item, w)
        }
        log.debug "${item.type} ${w.kind} ${w.name}@${w.version}"
    }
    
    def toDataSetMethod(item, w) {
        switch (item.type) {
            case Type.ADDED:
                ds.create(w)
                break;
            case Type.DELETED:
                ds.delete(w)
                break;
            case Type.MODIFIED:
                ds.update(w)
                break;
        }
    }
    
    CoreV1Api getV1Api() {
        new CoreV1Api()
    }
    
    ExtensionsV1beta1Api getExtensionsV1beta1Api() {
        new ExtensionsV1beta1Api()
    }
    
    DataSet getDs() {
        DataSet.instance
    }
    
    static class Type {
        static String ADDED = "ADDED"
        static String MODIFIED = "MODIFIED"
        static String DELETED = "DELETED"
        static String ERROR = "ERROR"     
    }
    
}
