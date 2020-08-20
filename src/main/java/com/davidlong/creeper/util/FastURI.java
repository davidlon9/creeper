package com.davidlong.creeper.util;

import com.davidlong.creeper.HttpConst;
import com.davidlong.creeper.model.Param;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FastURI {
    public static final String METHOD_POST="POST";
    public static final String METHOD_GET="GET";
    public static final String METHOD_PUT="PUT";
    public static final String METHOD_DELETE="DELETE";
    private URIBuilder uriBuilder;

    private final List<NameValuePair> paramStore=new CopyOnWriteArrayList<>();

    private String method=METHOD_GET;

    public FastURI(URI uri) {
        this.uriBuilder = new URIBuilder(uri);
    }

    public FastURI() {
        this.uriBuilder = new URIBuilder();
        this.uriBuilder.setScheme(HttpConst.SCHEME_HTTP);
    }

    public FastURI(URI host,String path) {
        this(host);
        if (path.contains("?")) {
            String[] split = path.split("\\?");
            if(split.length>1){
                this.path(split[0]);
                this.params(split[1]);
            }
        }else{
            this.path(path);
        }
    }

    public FastURI(String hostOrUri) {
        if(hostOrUri.contains("http")){
            try {
                this.uriBuilder=new URIBuilder(hostOrUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            this.uriBuilder=new URIBuilder();
            this.uriBuilder.setScheme(HttpConst.SCHEME_HTTP);
            this.uriBuilder.setHost(hostOrUri);
        }
    }


    public FastURI(String host,int port) {
        this();
        this.uriBuilder.setHost(host).setPort(port);
    }

    public FastURI(String host,String path) {
        this(host);
        this.path(path);
    }

    public FastURI POST(){
        this.method=METHOD_POST;
        return this;
    }

    public FastURI GET(){
        this.method=METHOD_GET;
        return this;
    }

    public FastURI PUT(){
        this.method=METHOD_PUT;
        return this;
    }
    public FastURI DELETE(){
        this.method=METHOD_DELETE;
        return this;
    }



    public FastURI HTTPS(){
        this.uriBuilder.setScheme(HttpConst.SCHEME_HTTPS);
        return this;
    }

    public FastURI host(String host){
        this.uriBuilder.setHost(host);
        return this;
    }

    public FastURI path(String path){
        if (path.contains("?")) {
            String[] split = path.split("\\?");
            if(split.length>1){
                path = split[0];
                uriBuilder.setCustomQuery(split[1]);
            }
        }
        uriBuilder.setPath(path);
        return this;
    }

    public FastURI params(String params){
        String[] arr1 = params.replace("\\?", "").split("&");
        for (String s : arr1) {
            String[] arr2 = s.split("=");
            if(arr2.length==2){
                this.param(arr2[0],arr2[1]);
            }
        }
        return this;
    }

    public FastURI params(Param ...params){
        addParams(params);
        return this;
    }

    public FastURI param(String name, String value){
        addParams(new Param(name,value));
        return this;
    }

    public FastURI paramValues(Object ...values){
        int vIdx=0;
        int pIdx=0;
        do {
            Param param = (Param) paramStore.get(pIdx);
            if(param!=null && param.getValue().equals("?")){
                Object value = values[vIdx];
                paramStore.remove(param);
                paramStore.add(vIdx,new Param(param.getName(),value));
                vIdx++;
            }
            pIdx++;
        }while (vIdx<values.length);
        return this;
    }


    private FastURI fillParams(String uri, Param ...params){
        if(uri.contains("$")){
            int numCount=0;
            for (int i = 0; i < params.length; i++) {
                Param param = params[i];
                String name = param.getName();
                String signal;
                if(uri.contains(signal="${" + name + "}")){
                    uri=uri.replace(signal,param.getValue());
                    ++numCount;
                }else if(uri.contains(signal="${" + (i+1) + "}")){
                    uri=uri.replace(signal,param.getValue());
                    ++numCount;
                }
            }
            if(numCount!=params.length){
                throw new IllegalStateException("set paramStore failed, uri:"+uri);
            }
            try {
                this.uriBuilder=new URIBuilder(uri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            this.uriBuilder.setParameters(params);
        }
        return this;
    }

    private FastURI fillParams(String uri, String ...paramValues){
        if(uri.contains("$")){
            int numCount=0;
            for (int i = 0; i < paramValues.length; i++) {
                String paramValue = paramValues[i];
                String signal;
                if(uri.contains(signal="${" + (i+1) + "}")){
                    uri=uri.replace(signal,paramValue);
                    ++numCount;
                }
            }
            if(numCount!=paramValues.length){
                throw new IllegalStateException("set paramStore failed, uri:"+uri);
            }
        }
        try {
            this.uriBuilder=new URIBuilder(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return this;
    }

    public URI build(){
        try {
            if(paramStore != null){
                if(METHOD_GET.equals(method)){
                    this.uriBuilder.setParameters(paramStore);
                }
            }
            return this.uriBuilder.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NameValuePair> getParamStore() {
        return paramStore;
    }

    synchronized private void addParams(Param ...params) {
        for (Param param : params) {
            if(paramStore.contains(param)){
                paramStore.remove(param);
            }
            paramStore.add(param);
        }
    }

    public Request buildRequest(){
        URI uri = this.build();
        Request request;
        if(this.method.equals(METHOD_POST)){
            String s = uri.toString();
            request=Request.Post(s.replace("?","")).bodyForm(this.paramStore);
        }else if(this.method.equals(METHOD_PUT)){
            request=Request.Put(uri).bodyForm(this.paramStore);
        }else if(this.method.equals(METHOD_DELETE)){
            request=Request.Delete(uri).bodyForm(this.paramStore);
        }else{
            request=Request.Get(uri);
        }
        return request;
    }

    public static void main(String[] args) {
        Param param1 = new Param("a", "v");
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(param1);
        System.out.println(objects.size());

        System.out.println(objects.get(0));


        Param param = new Param("a", "c");
        System.out.println(objects.contains(param));

        objects.remove(param);
        objects.add(param);
        System.out.println(objects.get(0));
        System.out.println(objects.size());

    }
}
