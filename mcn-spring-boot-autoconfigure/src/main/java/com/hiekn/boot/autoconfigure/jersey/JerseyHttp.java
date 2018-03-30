package com.hiekn.boot.autoconfigure.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * httpClient依赖JerseyClient
 * @author DingHao
 * 2018年4月2日16:49:40
 */
public class JerseyHttp {

    private JerseyClient client;
    private JerseyClientProperties clientProperties;

    public JerseyHttp(){
        this(new JerseyClientProperties());
    }

    public JerseyHttp(JerseyClientProperties clientProperties){
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.property(ClientProperties.CONNECT_TIMEOUT, clientProperties.getConnectTimeout())
                .property(ClientProperties.READ_TIMEOUT, clientProperties.getReadTimeout())
                .register(JacksonJsonProvider.class).register(MultiPartFeature.class);
        client = JerseyClientBuilder.createClient(clientConfig);
        this.clientProperties = clientProperties;
    }

    private MultivaluedMap<String,Object> getDefaultRequestHeader(){
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add(HttpHeaders.ACCEPT,clientProperties.getAcceptContentType());
        return headers;
    }

    public MultivaluedMap<String,Object> getMultiMap(){
        MultivaluedMap<String,Object> params = new MultivaluedHashMap<>();
        return params;
    }

    public String sendGet(String url,MultivaluedMap<String,Object> query){
        return sendGet(url,getDefaultRequestHeader(),query);
    }

    public String sendGet(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query){
        return sendGet(url, headers, query,String.class);
    }

    public <T> T sendGet(String url,MultivaluedMap<String,Object> query,Class<T> cls){
        return sendGet(url,getDefaultRequestHeader(),query,cls);
    }

    public <T> T sendGet(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query,Class<T> cls){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).get(cls);
    }

    public <T> T sendGet(String url,MultivaluedMap<String,Object> query,GenericType<T> hsp){
        return sendGet(url, getDefaultRequestHeader(),query, hsp);
    }

    public <T> T sendGet(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query,GenericType<T> hsp){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).get(hsp);
    }

    public String sendPost(String url,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post){
        return sendPost(url,getDefaultRequestHeader(),query,post);
    }

    public String sendPost(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post){
        return sendPost(url,headers,query,post,String.class);
    }

    public <T> T sendPost(String url,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post,Class<T> cls){
        return sendPost(url,getDefaultRequestHeader(),query,post,cls);
    }

    public <T> T sendPost(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post,Class<T> cls){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).post(Entity.entity(parsePostParams(post),clientProperties.getRequestContentEncode()),cls);
    }

    public <T> T sendPost(String url,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post,GenericType<T> hsp){
        return sendPost(url,getDefaultRequestHeader(),query,post,hsp);
    }

    public <T> T sendPost(String url,MultivaluedMap<String,Object> headers,MultivaluedMap<String,Object> query, MultivaluedMap<String, Object> post,GenericType<T> hsp){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).post(Entity.entity(parsePostParams(post),clientProperties.getRequestContentEncode()),hsp);
    }

    public String sendTextPost(String url, MultivaluedMap<String,Object> query,MultivaluedMap<String,Object> post){
        return sendTextPost(url,getDefaultRequestHeader(),query,post);
    }

    public String sendTextPost(String url,MultivaluedMap<String,Object> headers, MultivaluedMap<String,Object> query,MultivaluedMap<String,Object> post){
        return sendHttp(url,headers,query,post,MediaType.TEXT_PLAIN_TYPE,String.class);
    }

    public String sendJsonPost(String url, MultivaluedMap<String,Object> query,MultivaluedMap<String,Object> post){
        return sendJsonPost(url,getDefaultRequestHeader(),query,post);
    }

    public String sendJsonPost(String url,MultivaluedMap<String,Object> headers, MultivaluedMap<String,Object> query,MultivaluedMap<String,Object> post){
        return sendHttp(url,headers,query,post,MediaType.APPLICATION_JSON_TYPE,String.class);
    }

    /**
     * <p>
     *     构造FormDataMultiPart
     *     FormDataMultiPart multipart = new FormDataMultiPart();
     *     1.以file方式
     *          multipart.bodyPart(new FileDataBodyPart("fileData", file));
     *     2.直接以流的方式
     *          multipart.bodyPart(new StreamDataBodyPart("fileData", MetaBootApplication.class.getClassLoader().getResourceAsStream("logback-spring.xml"),"logback-spring.xml"));
     *     3.额外的post参数
     *        multipart.bodyPart(new FormDataBodyPart("param", "param"));
     * </p>
     *
     * @param url
     * @param query
     * @param multipart
     * @return
     */
    public String sendUpload(String url, MultivaluedMap<String,Object> query,FormDataMultiPart multipart){
        return sendUpload(url,getDefaultRequestHeader(),query, multipart);
    }

    public String sendUpload(String url,MultivaluedMap<String,Object> headers, MultivaluedMap<String,Object> query,FormDataMultiPart multipart){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).post(Entity.entity(multipart,multipart.getMediaType()),String.class);
    }

    private <T> T sendHttp(String url,MultivaluedMap<String,Object> headers, MultivaluedMap<String,Object> query,MultivaluedMap<String,Object> post,final MediaType mediaType,Class<T> cls){
        WebTarget webTarget = parseQueryParams(url,query);
        return webTarget.request().headers(headers).post(Entity.entity(parsePostParams(post),mediaType),cls);
    }

    private WebTarget parseQueryParams(String url,MultivaluedMap<String,Object> query){
        WebTarget webTarget = client.target(url);
        if(query != null && query.size() > 0){
            for (Entry<String, List<Object>> item : query.entrySet()) {
                webTarget = webTarget.queryParam(item.getKey(), item.getValue().size()>0?item.getValue().get(0):null);
            }
        }
        return webTarget;
    }

    private MultivaluedMap<String,String> parsePostParams(MultivaluedMap<String,Object> post){
        MultivaluedMap<String,String> p = new MultivaluedHashMap<>();
        if(post != null && post.size() > 0){
            for (Entry<String, List<Object>> item : post.entrySet()) {
                p.add(item.getKey(), item.getValue().size()>0?item.getValue().get(0).toString():null);
            }
        }
        return p;
    }

}
