package com.tdeado.core.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {

    String storeFile(MultipartFile file);
    String storeFile(File file);
    String getFileByUrl(String url);
    byte[] getFileByteByUrl(String url);
    Resource loadFileAsResource(String fileName);
    String getFileByBase64(String base64);
}
