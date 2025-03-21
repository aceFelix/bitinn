package com.itniuma.bigevent.controller;

import com.itniuma.bigevent.pojo.Result;
import com.itniuma.bigevent.utils.AliOssUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
public class FileUploadController {
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws Exception {
        // 将文件内容存储到本地磁盘上
        // 使用uuid保证文件名唯一，从而防止文件被覆盖
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename
                /*
                * 如果不加 toString()，那么 UUID.randomUUID() 返回的是一个 UUID 对象，而不是字符串。
                * 在 Java 中，当你尝试将一个对象与字符串进行拼接时（例如使用 + 操作符），
                * Java 会自动调用该对象的 toString() 方法来将其转换为字符串。
                * 因此，从功能上讲，即使你不显式调用 toString()，
                * 代码仍然可以运行并且最终结果是相同的。
                * 但是，显式调用 toString() 更加明确和直观，有助于提高代码的可读性和维护性。
                * */
                .substring(originalFilename.lastIndexOf("."));
        // file.transferTo(new File("C:\\Users\\许发明\\Desktop\\files\\" + fileName));
        String url = AliOssUtil.uploadFile(fileName, file.getInputStream());
        return Result.success(url);
    }
}
