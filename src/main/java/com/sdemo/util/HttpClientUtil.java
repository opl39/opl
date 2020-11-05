package com.sdemo.util;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static PoolingHttpClientConnectionManager cm;
    private static String UTF_8 = "UTF-8";

    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(50);// 整个连接池最大连接数
            cm.setDefaultMaxPerRoute(5);// 每路由最大连接数，默认值是2
        }
    }
    /**
     * 通过连接池获取HttpClient
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        if(params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
            }
        }
        return pairs;
    }
    //打印一下参数,并根据发送类型判断
    public static JsonResult<String> HttpRequest(String type, String url, Map<String, Object> paramMap,Map<String, Object> headers) throws IOException, URISyntaxException {
        Map<String, Object> params = new HashMap<>();
        if(! StringUtils.isEmpty(paramMap)){
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
                params.put(entry.getKey(), entry.getValue());
            }
        }

        if (type.equalsIgnoreCase("post")) {
            return postHttp(url, params, headers);
        } else {
            return getHttp(url, params,headers);
        }
    }
    //post请求
    public static JsonResult<String> postHttp(String url, Map<String, Object> params,Map<String, Object> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        // 如果需要head信息的话
        // 多添一个参数 Map<String, Object> headers
        for (Map.Entry<String, Object> param : headers.entrySet()) {
            httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
         }
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = httpClient.execute(httpPost);
        return getHttpStatusOrEntity(response);
    }
    //get请求
    public static JsonResult<String> getHttp(String url, Map<String, Object> params,Map<String, Object> headers) throws URISyntaxException, IOException {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);
        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);
        HttpGet httpGet = new HttpGet(ub.build());
        // 如果需要head信息
        // 多传 Map<String, Object> headers参数
        if(headers !=null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return getHttpStatusOrEntity(response);
    }
    //判断状态码是否成功   成功则返回数据
    public static JsonResult<String> getHttpStatusOrEntity( CloseableHttpResponse response) throws IOException {
        int statusCode = response.getStatusLine().getStatusCode();//状态码
        HttpEntity entity = response.getEntity();// 获取返回实体
        String  res= EntityUtils.toString(entity);
        response.close();
        switch (statusCode){
            case 200://:代表请求成功
                return JsonResult.ok(res);
            case 303://:代表重定向
                return JsonResult.error(303,res);
            case 400://:代表请求错误
                return JsonResult.error(400,res);
            case 401://:代表未授权
                return JsonResult.error(401,res);
            case 403://:代表禁止访问
                return JsonResult.error(403,res);
            case 404://:代表文件未找到\
                return JsonResult.error(404,res);
            case 500://:代表服务器错误
                return JsonResult.error(500,res);
            default://其他
                return JsonResult.error(statusCode,res);
        }
    }


    public static JsonResult<String> httpParamJsonPost(String url,Map map,Map<String, Object> headers) throws IOException {
        // 返回body
        String body = null;
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse=null;
        // 2、创建一个HttpPost请求
        HttpPost post = new HttpPost(url);
        // 5、设置header信息
        /**header中通用属性*/
        post.setHeader("Accept","*/*");
        post.setHeader("Accept-Encoding","gzip, deflate");
        post.setHeader("Cache-Control","no-cache");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        //设置头部传参
        if(headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                post.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }


/*            System.out.println(JSON.toJSONString(map));*/
            try {
                StringEntity entity1=null;
                // 设置参数
                if (map != null) {
                    entity1=new StringEntity(JSON.toJSONString(map),"UTF-8");
                }else{
                    entity1=new StringEntity("{}","UTF-8");
                }
                entity1.setContentEncoding("UTF-8");
                entity1.setContentType("application/json");
                post.setEntity(entity1);
                // 7、执行post请求操作，并拿到结果
                httpResponse = httpClient.execute(post);
               /* // 获取结果实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    // 按指定编码转换结果实体为String类型
                    body = EntityUtils.toString(entity, "UTF-8");
                }*/
               /* try {
                    httpResponse.close();
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
         return getHttpStatusOrEntity(httpResponse);
    }
    public static JsonResult<String> httpParamListJsonPost(String url, List list, Map<String, Object> headers) throws IOException {
        // 返回body
        String body = null;
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse=null;
        // 2、创建一个HttpPost请求
        HttpPost post = new HttpPost(url);
        // 5、设置header信息
        /**header中通用属性*/
        post.setHeader("Accept","*/*");
        post.setHeader("Accept-Encoding","gzip, deflate");
        post.setHeader("Cache-Control","no-cache");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        //设置头部传参
        if(headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                post.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }


        /*            System.out.println(JSON.toJSONString(map));*/
        try {
            StringEntity entity1=null;
            // 设置参数
            if (list != null) {
                entity1=new StringEntity(JSON.toJSONString(list),"UTF-8");
            }else{
                entity1=new StringEntity("{}","UTF-8");
            }
            entity1.setContentEncoding("UTF-8");
            entity1.setContentType("application/json");
            post.setEntity(entity1);
            // 7、执行post请求操作，并拿到结果
            httpResponse = httpClient.execute(post);
               /* // 获取结果实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    // 按指定编码转换结果实体为String类型
                    body = EntityUtils.toString(entity, "UTF-8");
                }*/
               /* try {
                    httpResponse.close();
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getHttpStatusOrEntity(httpResponse);
    }


    public static String httpParamJsonPostParamJSONStr(String url, String jsonParam, Map<String, Object> headers) throws IOException {
        // 返回body
        String body = null;
        // 获取连接客户端工具
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse=null;
        // 2、创建一个HttpPost请求
        HttpPost post = new HttpPost(url);
        // 5、设置header信息
        /**header中通用属性*/
        post.setHeader("Accept","*/*");
        post.setHeader("Accept-Encoding","gzip, deflate");
        post.setHeader("Cache-Control","no-cache");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        //设置头部传参
        if(headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                post.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }


        /*            System.out.println(JSON.toJSONString(map));*/
        try {
            StringEntity entity1=null;
            entity1=new StringEntity(jsonParam,"UTF-8");
            // 设置参数
            /*if (map != null) {
                entity1=new StringEntity(JSON.toJSONString(map),"UTF-8");
            }else{
                entity1=new StringEntity("{}","UTF-8");
            }*/
            entity1.setContentEncoding("UTF-8");
            entity1.setContentType("application/json");
            post.setEntity(entity1);
            // 7、执行post请求操作，并拿到结果
            httpResponse = httpClient.execute(post);
             // 获取结果实体
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    // 按指定编码转换结果实体为String类型
                    body = EntityUtils.toString(entity, "UTF-8");
                }
               try {
                    httpResponse.close();
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

}
