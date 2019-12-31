package com.seater.smartmining.utils.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seater.smartmining.constant.SmartminingConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.hibernate.validator.internal.util.privilegedactions.GetResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Description:
 * @Author yueyuzhe
 * @Email 87167070@qq.com
 * @Date 2019/10/21 0021 15:00
 */
public class ConnectionUtils {

    private static Logger logger = LoggerFactory.getLogger(ConnectionUtils.class); // 日志记录
    // 请求超时时间(毫秒) 5秒
    public static RequestConfig requestConfig;

    // 响应超时时间(毫秒) 60秒
    public static int HTTP_RESPONSE_TIMEOUT = 60 * 1000;

    public static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(5 * 1000)
                .setConnectionRequestTimeout(HTTP_RESPONSE_TIMEOUT).build();
    }


    /**
     * post请求传输json参数
     *
     * @param url
     *            url地址
     * @param strParam
     *            参数
     * @return
     */
    public static JSONObject httpPost(String url, String strParam) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        JSONObject jsonResult = null;
        HttpPost httpPost = new HttpPost(url);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        httpPost.setConfig(requestConfig);
        try {
            if (null != strParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(strParam, SmartminingConstant.ENCODEUTF);
                entity.setContentEncoding(SmartminingConstant.ENCODEUTF);
                entity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse result = httpClient.execute(httpPost);
            //请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String str = "";
                try {
                    //读取服务器返回过来的json字符串数据
                    str = EntityUtils.toString(result.getEntity(), SmartminingConstant.ENCODEUTF);
                    //把json字符串转换成json对象
                    jsonResult = JSONObject.parseObject(str);
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return jsonResult;
    }

    /**
     * 发送get请求
     *
     * @param url
     *            路径
     * @return
     */
    public static JSONObject httpGet(String url) {
        // get请求返回结果
        JSONObject jsonResult = null;
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);
            //请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity();
                String strResult = EntityUtils.toString(entity, SmartminingConstant.ENCODEUTF);
                //把json字符串转换成json对象
                jsonResult = JSONObject.parseObject(strResult);
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        } finally {
            request.releaseConnection();
        }
        return jsonResult;
    }

    public static JSONObject httpsClient(String url,HttpEntity he){
        // 建立一个sslcontext，这里我们信任任何的证书。
        SSLContext context = null;
        try {
            context = getTrustAllSSLContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 建立socket工厂
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                context);
        // 建立连接器
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(factory).build();
        try {
            // 得到一个post请求的实体
            HttpPost post = getMultipartPost(url);
            // 给请求添加参数
            post.setEntity(he);
            // 执行请求并获得结果
            CloseableHttpResponse reponse = client.execute(post);
            try {
                // 获得返回的内容
                HttpEntity entity = reponse.getEntity();
                // 输出
                return JSONObject.parseObject(EntityUtils.toString(entity));
            } finally {
                // 关闭返回的reponse
                reponse.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭client
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getBoundaryStr(String str) {
        return "------------" + str;
    }

    public static MultipartEntityBuilder get_COMPATIBLE_Builder(String charSet) {
        MultipartEntityBuilder result = MultipartEntityBuilder.create();
        result.setBoundary(getBoundaryStr("7da2e536604c8"))
                .setCharset(Charset.forName(charSet))
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        return result;
    }

    private static HttpPost getMultipartPost(String url) {
        /* 这里设置一些post的头部信息，具体求百度吧 */
        HttpPost post = new HttpPost(url);
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "multipart/form-data;boundary="
                + getBoundaryStr("7da2e536604c8"));
        post.addHeader("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        return post;
    }

    /**
     * https请求伪造证书
     * @return
     */
    private static SSLContext getTrustAllSSLContext() throws Exception {
        SSLContext context = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                        // 这一句就是信任任何的证书，当然你也可以去验证微信服务器的真实性
                        return true;
                    }
                }).build();
        return context;
    }

    /**
     * https请求加证书
     * @return
     */
    public static CloseableHttpClient defaultSSLClientFile(String sslType,String sslFile,String mchId) {
        FileInputStream inputStream = null;
        KeyStore keyStore = null;
        String path = GetResource.class.getClassLoader().getResource(sslFile).getPath();
        try {
            // ssl类型
            keyStore = KeyStore.getInstance(sslType);
            // ssl文件
            inputStream = new FileInputStream(path);
            // 设置ssl密码
            keyStore.load(inputStream,mchId.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadKeyMaterial(keyStore,mchId.toCharArray()).build();
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            e.printStackTrace();
        }

        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return HttpClients.custom().setSSLSocketFactory(factory).build();
    }

    /**
     * 封装发送请求的方法
     * @throws UnsupportedEncodingException
     */
    public static String httpsClient(String url, String data, CloseableHttpClient closeableHttpClient)
            throws UnsupportedEncodingException {
        CloseableHttpClient client = closeableHttpClient;
        HttpPost httpPost = new HttpPost(URLDecoder.decode(url, SmartminingConstant.ENCODEUTF));
        httpPost.addHeader("Connection", "keep-alive");
        httpPost.addHeader("Accept", "*/*");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("Host", "api.mch.weixin.qq.com");
        httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
        httpPost.addHeader("Cache-Control", "max-age=0");
        httpPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        httpPost.setConfig(getRequestConfig());// 设置超时时间
        CloseableHttpResponse response = null;
        // 参数放入
        StringEntity entity = new StringEntity(data, SmartminingConstant.ENCODEUTF);
        entity.setContentEncoding(SmartminingConstant.ENCODEUTF);
        entity.setContentType("application/xml");
        httpPost.setEntity(entity);
        try {
            response = client.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = (HttpEntity) response.getEntity();
                if (response != null) {
                    return EntityUtils.toString(httpEntity,SmartminingConstant.ENCODEUTF);
                    //return JSON.toJSONString(httpEntity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
