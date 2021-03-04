package com.tdeado.core.config;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@Slf4j
@Configuration
@EnableConfigurationProperties({FileProperties.class})
public class FileConfigurer {
    @Autowired
    FileProperties fileProperties;
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "tdeado.code.file",value = "upload-dir",matchIfMissing = true)
    public Path initPath() {
        if (null==fileProperties.getUploadDir() || "".equals(fileProperties.getUploadDir())){
            log.info("未设置存储上传文件的目录");
            return null;
        }
        Path fileStorageLocation = Paths.get(fileProperties.getUploadDir()).toAbsolutePath().normalize();
        Path avatarStorageLocation = Paths.get(fileProperties.getUploadDir()+"/avatar/").toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
            Files.createDirectories(avatarStorageLocation);
        } catch (Exception ex) {
            throw new ApiException("无法创建存储上传文件的目录。", ex);
        }
        return fileStorageLocation;
    }
}
