package linchpiner.kubegraph.kubernetes

import io.kubernetes.client.util.Config
import io.kubernetes.client.ApiClient
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.apis.ExtensionsV1beta1Api
import io.kubernetes.client.util.Watch
import linchpiner.kubegraph.model.DataSet
import linchpiner.kubegraph.model.KEndpoint
import linchpiner.kubegraph.map.ModelMap
import linchpiner.kubegraph.model.KNode
import linchpiner.kubegraph.model.KPod
import linchpiner.kubegraph.model.Change.Type

import java.util.prefs.FileSystemPreferences.NodeCreate

import com.google.gson.reflect.TypeToken
import groovy.transform.TupleConstructor
import groovy.util.ObjectGraphBuilder.DefaultNewInstanceResolver
import groovy.util.logging.Slf4j
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1Node
import io.kubernetes.client.models.V1ReplicationController
import io.kubernetes.client.models.V1Service
import io.kubernetes.client.models.V1beta1ReplicaSet
import io.kubernetes.client.models.AppsV1beta1Deployment
import io.kubernetes.client.models.V1EndpointAddress
import io.kubernetes.client.models.V1Endpoints
import io.kubernetes.client.models.V1Event
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

    def config = "config"
    String namespace = "default"
    int timeout = 0
    
    def links = [
        podsToNodes: true,
        podsToRCs:   true,
    ]
    
    ApiClient client
    
    KubeWatcher() {
        client = Config.fromConfig(config)
        Configuration.setDefaultApiClient(client)
        client.httpClient.setReadTimeout(0, TimeUnit.SECONDS)
    }
    
    def nodes() {
        watch(call: v1Api.listNodeCall(null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1Node>>(){}.getType()) { item, node ->
        }
    }

    def pods() {
        watch(call: v1Api.listNamespacedPodCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1Pod>>(){}.getType()) { item, KPod pod ->
            
            V1Pod object = item.object  
                
            // phase
            pod.phase = object.status.phase.toLowerCase() 
                
            // link to node
            def node = object.spec.nodeName
            if (links.podsToNodes && node) {  
                ds.link(pod.uid, node, "node")
            }
                
            // link to owners
            def owners = object.metadata.ownerReferences
            if (links.podsToRCs) {
                owners?.each { owner ->
                    ds.link(owner.uid, pod.uid)
                }
            }
                
        }
    }
    
    def rcs() {
        watch(call: v1Api.listNamespacedReplicationControllerCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1ReplicationController>>(){}.getType()) { item, w ->
        }
    }
    
    def rss() {
        watch(call: extensionsV1beta1Api.listNamespacedReplicaSetCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1beta1ReplicaSet>>(){}.getType()) { item, w ->
                
            def owners = item.object.metadata.ownerReferences
            owners?.each { owner ->
                ds.link(owner.uid, w.uid)
            }
    
        }
    }
    
    def deployments() {
        watch(call: extensionsV1beta1Api.listNamespacedDeploymentCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<AppsV1beta1Deployment>>(){}.getType()) { item, w ->
        }
    }

    def services() {
        watch(call: v1Api.listNamespacedServiceCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1Service>>(){}.getType()) { item, w ->
        }
    }
    
    def endpoints() {
        watch(call: v1Api.listNamespacedEndpointsCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1Endpoints>>(){}.getType()) { Watch.Response<V1Endpoints> item, KEndpoint endpoint ->
            item.object.subsets?.find { true }?.addresses?.each { V1EndpointAddress addr ->
                endpoint.uids.add(addr?.targetRef?.uid)
            }
        }
    }
    
    def events() {
        watch(call: v1Api.listNamespacedEventCall(namespace, null, null, null, null, timeout, true, null, null),
              type: new TypeToken<Watch.Response<V1Event>>(){}.getType()) { item, w ->
            log.debug "${item.object}"
        }
    }
    
    def createWatch(args) {
        return Watch.createWatch(client, args['call'], args['type'])
    }
    
    def watch(args, Closure c = null) {
        createWatch(args).each { item -> toDataSet(item, c) }
    }
    
    def toDataSet(item, Closure c) {
        def object = item.object
        def kind   = object.kind.toLowerCase()
        
        KNode w   = ModelMap.classForKind(kind).newInstance()
        w.uid     = object.metadata.uid 
        w.kind    = kind 
        w.name    = object.metadata.name
        w.version = Long.valueOf(object.metadata.resourceVersion)
        
        if (c != null) {
            c.call(item, w)
        }

        toDataSetMethod(item, w) 
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
