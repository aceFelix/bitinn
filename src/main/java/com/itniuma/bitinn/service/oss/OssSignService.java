package com.itniuma.bitinn.service.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OssSignService {

    @Value("${aliyun-oss.access-key}")
    private String accessKey;

    @Value("${aliyun-oss.secret-key}")
    private String secretKey;

    @Value("${aliyun-oss.end-point}")
    private String endpoint;

    @Value("${aliyun-oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun-oss.domain:}")
    private String domain;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKey, secretKey);
        log.info("[OSS] 签名服务初始化完成, endpoint={}, bucket={}", endpoint, bucketName);
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }

    public String generatePresignedUrl(String objectKey, int expireMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000L);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey);
        request.setExpiration(expiration);
        URL url = ossClient.generatePresignedUrl(request);
        return url.toString();
    }

    public Map<String, String> generatePresignedUrls(Map<String, String> objectKeys, int expireMinutes) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> entry : objectKeys.entrySet()) {
            result.put(entry.getKey(), generatePresignedUrl(entry.getValue(), expireMinutes));
        }
        return result;
    }

    public String extractObjectKey(String url) {
        if (url == null || url.isEmpty()) return null;

        String ossDomain = "big-event-base.oss-cn-hangzhou.aliyuncs.com";
        if (url.contains(ossDomain)) {
            int idx = url.indexOf(ossDomain);
            String path = url.substring(idx + ossDomain.length());
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            int queryIdx = path.indexOf("?");
            if (queryIdx > 0) {
                path = path.substring(0, queryIdx);
            }
            return path;
        }

        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                java.net.URI uri = new java.net.URI(url);
                String path = uri.getPath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                return path;
            } catch (Exception e) {
                log.warn("[OSS] 解析URL失败: {}", url);
                return null;
            }
        }

        return url;
    }

    public boolean isOssUrl(String url) {
        if (url == null) return false;
        return url.contains("aliyuncs.com") || url.contains("oss-cn-");
    }
}
