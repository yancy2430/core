package com.tdeado.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("tdeado.file")
public class UploadFileProperties {
    private String ossEndpoint;
    private String ossAccessKeyId;
    private String ossAccessKeySecret;
    private String ossBucketName;
    private String imgDirectory;
    private String imgDomain;
}
