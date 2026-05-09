package com.xgj.outsourcing.controller.common;

import com.xgj.outsourcing.common.operationlog.annotation.Log;
import com.xgj.outsourcing.service.common.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    // 输入文件相对地址，完成指定文件下载
    @Log("文件下载")
    @GetMapping("/download")
    public void download(@RequestParam String relativePath, HttpServletResponse response) {
        fileService.downloadFile(response, relativePath);
    }
}
