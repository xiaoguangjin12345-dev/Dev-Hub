package com.xgj.devpulse.service.common;

import com.alibaba.excel.EasyExcel;
import com.xgj.devpulse.common.exception.AuthorizationException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    // 文件操作根路径（从配置文件取）
    @Value("${file.upload-root-path}")
    private String rootPath;

    // 上传业务文件，返回物理相对路径（内部使用）
    public String uploadFile(MultipartFile file, String targetType, Integer targetId, String subPath, String oldRelativePath){
        // 拼接完整路径
        Path pathStr = Paths.get(rootPath, targetType, targetId.toString(), subPath);
        // 转换成物理路径（若没有，则新建）
        File path = pathStr.toFile();
        if (!path.exists()) {
            path.mkdirs();
        }

        // 获取文件扩展名
        String extension = this.getFileExtension(file);
        // 文件使用UUID唯一命名
        String newFileName = UUID.randomUUID().toString() + extension;

        // 文件上传
        try{
            // 构造文件
            File destFile = new File(path.getAbsoluteFile(), newFileName);
            // 传输至物理路径
            file.transferTo(destFile);
        }catch (IOException e){
            throw new RuntimeException("文件上传失败", e);
        }

        // 若有旧文件，删除（失败不throw，不影响主业务）
        this.checkAndDeleteOriginalFile(oldRelativePath);

        // 返回相对路径
        String relativePath = String.join("/", targetType, targetId.toString(), subPath);
        return "/" + relativePath + "/" + newFileName;
    }

    // 下载用户上传的现有业务文件
    public void downloadFile(HttpServletResponse response, String relativePath){
        // 获取文件信息
        File file = this.getDownloadFileInfo(relativePath);
        try {
            InputStream is = new FileInputStream(file);
            OutputStream os = response.getOutputStream();

            // 设置响应头Header
            String extension = this.getFileExtension(file); // 获取文件扩展名
            String fileName = URLEncoder.encode("下载文件" + extension, StandardCharsets.UTF_8); // 设置下载得到的文件名称（加上编码处理）
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(file.length());

            // 传输
            StreamUtils.copy(is, os);
            os.flush(); // 确保数据全部传输
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败", e);
        }
    }

    // 根据不同数据类型，导出业务数据报表（固定列名重载）
    public void getExportExcel(HttpServletResponse response, String fileName, List<?> data, Class<?> clazz){
        try {
            // 设置响应头Header
            fileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // 直接写到Response输出流
            EasyExcel.write(response.getOutputStream(), clazz)
                    .needHead(true)
                    .sheet(fileName)
                    .doWrite(data);
        } catch (IOException e) {
            throw new RuntimeException("报表导出失败", e);
        }
    }

    // 根据不同数据类型，导出业务数据报表（自定列名重载）
    public void getExportExcel(HttpServletResponse response, String fileName, List<?> data, Class<?> clazz, List<List<String>> head){
        try {
            // 设置响应头Header
            fileName = URLEncoder.encode(fileName + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // 直接写到Response输出流
            EasyExcel.write(response.getOutputStream(), clazz)
                    .head(head)   // 写入自定列名
                    .needHead(true)
                    .sheet(fileName)
                    .doWrite(data);
        } catch (IOException e) {
            throw new RuntimeException("报表导出失败", e);
        }
    }

    // 检查是否存在旧文件，若有，则删除
    public boolean checkAndDeleteOriginalFile(String oldRelativePath){
        // 处理旧文件问题
        if (oldRelativePath != null && !oldRelativePath.isEmpty()) {
            try {
                // 组装旧文件的绝对路径
                Path oldFilePath = Paths.get(rootPath, oldRelativePath);
                File oldFile = oldFilePath.toFile();
                if (oldFile.exists() && oldFile.isFile()) {
                    oldFile.delete();
                }
            } catch (Exception e) {
                // 不中断上传主流程
                log.error("旧文件清理失败", e);
                return false;
            }
        }
        return true;
    }


    // 获取文件信息（用于下载文件）
    private File getDownloadFileInfo(String relativePath){
        // 相对路径路径不合法
        if (relativePath == null || relativePath.contains("..")) {
            throw new AuthorizationException(403, "非法文件路径");
        }

        // 拼接物理路径（归一化处理）
        Path absolutePath = Paths.get(rootPath, relativePath).normalize();
        // 若路径不是已根路径开头
        if (!absolutePath.startsWith(Paths.get(rootPath).normalize())) {
            throw new AuthorizationException(403, "非法文件路径");
        }

        // 根据绝对路径获取文件内容
        File file = absolutePath.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("文件不存在或路径错误");
        }
        return file;
    }

    // 获取文件扩展名（File重载）
    private String getFileExtension(File file){
        // 获取文件名
        String fileName = file.getName();
        // 默认值为二进制文件
        String extension = ".bin";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        return extension;
    }

    // 获取文件扩展名（MultipartFile重载）
    private String getFileExtension(MultipartFile file){
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 默认值为二进制文件
        String extension = ".bin";
        if (fileName != null && fileName.contains(".")) {
            extension = fileName.substring(fileName.lastIndexOf("."));
        }
        return extension;
    }

}
