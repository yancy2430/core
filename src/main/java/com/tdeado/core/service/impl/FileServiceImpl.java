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

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String getFileByUrl(String url) {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,
                    httpEntity, byte[].class);
            if (response.getStatusCode().is2xxSuccessful()) {


                String[] urr = url.split("\\.");
                String newFileName = UUID.randomUUID().toString() + ".jpg";
                newFileName = this.fileStorageLocation.getFileName() + "/" + newFileName;
                BufferedOutputStream bos = null;
                FileOutputStream fos = null;
                File file = null;
                try {
                    file = new File(newFileName);
                    fos = new FileOutputStream(file);
                    bos = new BufferedOutputStream(fos);
                    bos.write(Objects.requireNonNull(response.getBody()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                return  "/" + newFileName;

            }else {
                throw new ApiException("下载图片失败");
            }

    }

    @Override
    public byte[] getFileByteByUrl(String url) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET,
                httpEntity, byte[].class);
        if (response.getStatusCode().is2xxSuccessful()) {

            return  response.getBody();

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

    public String getFileByBase64(String base64) {
        //定义一个正则表达式的筛选规则，为了获取图片的类型
        String rgex = "data:image/(.*?);base64";

        String type = getSubUtilSimple(base64, rgex);
        //去除base64图片的前缀
        base64 = base64.replaceFirst("data:(.+?);base64,", "");
        byte[] b;
        byte[] bs;
        OutputStream os = null;
        // 格式化并获取当前日期（用来命名）
        //把图片转换成二进制
        b = java.util.Base64.getDecoder().decode(base64);
        //生成路径
        //随机生成图片的名字，同时根据类型结尾

        String newFileName = this.fileStorageLocation.getFileName() + "/" + UUID.randomUUID() + ".jpg";

        File imageFile = new File(newFileName);
        // 保存
        try {
            os = new FileOutputStream(imageFile);
            os.write(b);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.getLocalizedMessage();
                }
            }
        }

        return "/"+newFileName;
    }
    public static String getSubUtilSimple(String soap,String rgex){
        Pattern pattern = Pattern.compile(rgex);
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";


    }

}
