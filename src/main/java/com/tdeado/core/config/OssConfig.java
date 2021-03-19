package com.tdeado.core.config;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class OssConfig {
    @Autowired
    UploadFileProperties uploadFileProperties;

    @Bean
    public OSSClient OSSClient(){
        if (StringUtils.isEmpty(uploadFileProperties.getOssAccessKeyId())){
            return null;
        }
        OSSClient ossClient = new OSSClient(uploadFileProperties.getOssEndpoint(), uploadFileProperties.getOssAccessKeyId(), uploadFileProperties.getOssAccessKeySecret());
        return ossClient;
    }

}
