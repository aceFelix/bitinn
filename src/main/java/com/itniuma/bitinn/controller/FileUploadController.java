package com.itniuma.bitinn.controller;

import com.itniuma.bitinn.pojo.Result;
import org.dromara.x.file.storage.core.FileStorageService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
// @RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;
    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        String url = fileStorageService.of(file).upload().getUrl();
        return Result.success(url);
    }
}
