package com.tdeado.core.service.impl;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.tdeado.core.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    public Path fileStorageLocation;
    @Autowired
    public RestTemplate restTemplate;

    /**
     * 存储文件到系统
     *
     * @param file 文件
     * @return 文件名
     */
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new ApiException("对不起!文件名包含无效的路径序列" + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            String[] imgName = fileName.split("\\.");
            String newFileName = UUID.randomUUID().toString() + "." + imgName[imgName.length - 1];
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/" + this.fileStorageLocation.getFileName() + "/" + newFileName;
        } catch (IOException ex) {
            throw new ApiException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public String storeFile(File file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getName());
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new ApiException("对不起!文件名包含无效的路径序列" + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name)
            String[] imgName = fileName.split("\\.");
            String newFileName = UUID.randomUUID().toString() + "." + imgName[imgName.length - 1];
            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(file.toPath(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return "/" + this.fileStorageLocation.getFileName() + "/" + newFileName;
        } catch (IOException ex) {
            throw new ApiException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public byte[] getFileByUrl(String url) {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,
                    httpEntity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }else {
                throw new ApiException("下载图片失败");
            }

    }

    /**
     * 加载文件
     *
     * @param fileName 文件名
     * @return 文件
     */
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new ApiException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new ApiException("File not found " + fileName, ex);
        }
    }


}
