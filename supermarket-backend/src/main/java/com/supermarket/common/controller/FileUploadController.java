package com.supermarket.common.controller;

import com.supermarket.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${supermarket.upload-path:./uploads/}")
    private String uploadPath;

    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 获取文件名
        String originalFilename = file.getOriginalFilename();
        String suffixName = "";
        if (originalFilename != null && originalFilename.contains(".")) {
             suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // 默认后缀或处理无后缀情况
             suffixName = ".jpg";
        }

        // 生成新文件名
        String fileName = UUID.randomUUID() + suffixName;

        // 获取上传目录绝对路径
        File uploadDir = new File(uploadPath).getAbsoluteFile();
        if (!uploadDir.exists()) {
             boolean mkdirs = uploadDir.mkdirs();
             if (!mkdirs) {
                 log.error("创建上传目录失败: {}", uploadDir.getAbsolutePath());
                 return Result.error("上传失败：无法创建目录");
             }
        }

        // 创建文件对象
        File dest = new File(uploadDir, fileName);

        try {
            file.transferTo(dest);
            // 返回访问路径（前端通过 /uploads/fileName 访问）
            String fileUrl = "/uploads/" + fileName;
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败");
        }
    }
}
