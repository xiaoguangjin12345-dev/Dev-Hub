package com.xgj.outsourcing.service.common;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    // 上传业务文件（内部使用）
    String uploadFile(MultipartFile file, String targetType, Integer targetId, String subPath, String oldRelativePath);

    // 下载用户上传的现有业务文件
    void downloadFile(HttpServletResponse response, String relativePath);

    // 将业务数据导出为报表（填写文件名时不带后缀，固定列名重载）
    void getExportExcel(HttpServletResponse response, String fileName, List<?> data, Class<?> clazz);

    // 将业务数据导出为报表（填写文件名时不带后缀，自定列名重载）
    void getExportExcel(HttpServletResponse response, String fileName, List<?> data, Class<?> clazz, List<List<String>> head);
}
