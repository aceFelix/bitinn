package com.itniuma.bitinn.controller.oss;

import com.itniuma.bitinn.pojo.Result;
import com.itniuma.bitinn.service.oss.OssSignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssSignController {

    private final OssSignService ossSignService;

    private static final int DEFAULT_EXPIRE_MINUTES = 60;

    @GetMapping("/sign-url")
    public Result<String> getSignUrl(@RequestParam String url) {
        if (!ossSignService.isOssUrl(url)) {
            return Result.success(url);
        }
        String objectKey = ossSignService.extractObjectKey(url);
        if (objectKey == null || objectKey.isEmpty()) {
            return Result.success(url);
        }
        String signedUrl = ossSignService.generatePresignedUrl(objectKey, DEFAULT_EXPIRE_MINUTES);
        log.debug("[OSS签名] 原URL: {}, 签名URL: {}", url, signedUrl);
        return Result.success(signedUrl);
    }

    @PostMapping("/sign-urls")
    public Result<Map<String, String>> getSignUrls(@RequestBody List<String> urls) {
        Map<String, String> result = new HashMap<>();
        for (String url : urls) {
            if (url == null || url.isEmpty()) continue;
            if (!ossSignService.isOssUrl(url)) {
                result.put(url, url);
                continue;
            }
            String objectKey = ossSignService.extractObjectKey(url);
            if (objectKey == null || objectKey.isEmpty()) {
                result.put(url, url);
                continue;
            }
            String signedUrl = ossSignService.generatePresignedUrl(objectKey, DEFAULT_EXPIRE_MINUTES);
            result.put(url, signedUrl);
        }
        return Result.success(result);
    }
}
