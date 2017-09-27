package linchpiner.kubegraph.kubernetes

import io.kubernetes.client.util.Config
import java.util.concurrent.ConcurrentHashMap.ForEachEntryTask
import com.google.gson.reflect.TypeToken
import groovy.lang.Closure
import io.kubernetes.client.ApiClient
import io.kubernetes.client.ApiException
import io.kubernetes.client.Configuration
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.apis.ExtensionsV1beta1Api
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.models.V1PodList
import io.kubernetes.client.util.Watch
import io.kubernetes.client.ApiCallback
import java.util.concurrent.TimeUnit

class KubeTest {

    def config = "./config"
    String namespace = "default"
    int timeout = 5
    ApiClient client
    
    void start() {
        System.properties['http.keepAlive'] = "true"
        client = Config.fromConfig(config)
        Configuration.setDefaultApiClient(client)
       
        measure {
            def endpoints = v1Api.listNamespacedEndpoints(namespace, null, null, null, null, timeout, false)
            def endpoint = endpoints.items.find { endpoint -> endpoint.metadata.name == "echoserver" }
            endpoint.subsets.first().addresses.each { addr ->
                println "${addr.targetRef.name} ${addr.targetRef.uid}"
            }
        }
        
        measure {
            client.httpClient.setReadTimeout(0, TimeUnit.SECONDS)
            watch  (call: v1Api.listNamespacedPodCall(namespace, null, null, null, null, 500, true, null, null),
                    type: new TypeToken<Watch.Response<V1Pod>>(){}.getType()) { watch ->
                watch.each { item -> showItem(item) }
            }
        }
   
        
/*             
        measure {
            watch  (call: v1Api.listNamespacedPodCall(namespace, null, null, null, null, timeout, true, null, null),
                    type: new TypeToken<Watch.Response<V1PodList>>(){}.getType()) { watch ->
                watch.each { item ->
                    def obj = item.object
                    println "${item.type} ${obj.kind.toLowerCase()}"
                    println "${obj}"
                    // obj is V1PodList, kind: Pod, metadata.selfLink: <pod url>
                }
            }
        }
*/        
/*        
        measure {
            ApiCallback<V1PodList> callback = new ApiCallback<V1PodList>() {
                void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                    println "onFailure: ${statusCode}"
                }
                void onSuccess(V1PodList result, int statusCode, Map<String, List<String>> responseHeaders) {
                    println "onSuccess:"
                    result.items.each { item ->
                        println "${item.metadata.name}"
                    }
                }
                void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                    //println "onUploadProgress: done: ${done}"
                }
                void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                    //println "onDownloadProgress: done: ${done}"
                }
            }
            v1Api.listNamespacedPodAsync(namespace, null, null, null, null, 0, true, callback)
        }
*/        
    }
    
    CoreV1Api getV1Api() {
        new CoreV1Api()
    }
    
    def watch(args, c) {
        def watch = Watch.createWatch(client, args['call'], args['type'])
        c(watch)
    }
    
    def showItem(item) {
        def obj = item.object
        println "${item.type} ${obj.kind.toLowerCase()}/${obj.metadata.name}"
    }
    
    def measure(Closure c) {
        long t1 = System.currentTimeMillis()
        def result = c.call()
        long t2 = System.currentTimeMillis()
        println "Time: ${(t2 - t1) / 1000}s"
        return result
    }
    
    static void main(args) {
        new KubeTest().start()
    }
}
