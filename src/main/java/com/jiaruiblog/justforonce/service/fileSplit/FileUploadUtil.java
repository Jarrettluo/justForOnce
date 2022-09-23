package com.jiaruiblog.justforonce.service.fileSplit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import org.apache.http.client.methods.CloseableHttpResponse;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import cn.hutool.json.JSONUtil;


/**
 * @Author Jarrett Luo
 * @Date 2022/9/23 14:16
 * @Version 1.0
 */
public class FileUploadUtil {

    /**
     * 不用hutool方式，采用httpClient方式上传（hutool和okhttp上传大文件都会有内存溢出的报错）
     *
     * @param file    file
     * @param path    path
     * @param showUrl showUrl
     * @return ResponseBean
     */
    public static GoFastDfsUploadResult upload(FileInputStream file, String fileName,  String path, String showUrl,
                                               String folderName) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            CloseableHttpResponse httpResponse = null;
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(200000)
                    .setSocketTimeout(2000000)
                    .build();
            HttpPost httpPost = new HttpPost(path);
            httpPost.setConfig(requestConfig);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setCharset(StandardCharsets.UTF_8)
                    .addTextBody("output", "json")
                    .addTextBody("path", folderName)
                    .addBinaryBody("file", file,
                            ContentType.DEFAULT_BINARY, fileName);
            httpPost.setEntity(multipartEntityBuilder.build());
            httpResponse = httpClient.execute(httpPost);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String respStr = EntityUtils.toString(httpResponse.getEntity());
                GoFastDfsUploadResult goFastDfsResult = JSONUtil.toBean(respStr, GoFastDfsUploadResult.class);
                //替换url
                goFastDfsResult.setUrl(showUrl + goFastDfsResult.getPath());
                return (goFastDfsResult);
            }
            httpClient.close();
            httpResponse.close();
            return null;
        } catch (Exception e) {
            return null;
        }
    }


}
