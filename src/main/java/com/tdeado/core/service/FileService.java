package com.tdeado.core.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {

    String storeFile(MultipartFile file);
    String storeFile(File file);
    byte[] getFileByUrl(String url);
    Resource loadFileAsResource(String fileName);
}
