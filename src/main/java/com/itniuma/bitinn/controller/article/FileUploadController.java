package com.itniuma.bitinn.controller.article;

import com.itniuma.bitinn.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class FileUploadController {

    private final FileStorageService fileStorageService;
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/api/upload")
    public Result<String> upload(MultipartFile file) {
        String url = fileStorageService.of(file).upload().getUrl();
        log.info("[文件上传] 文件名: {}, 返回URL: {}", file.getOriginalFilename(), url);
        return Result.success(url);
    }
}
