package com.tdeado.core.service;

import java.io.File;

public interface EmailService {
    public boolean sendEmail(String from, String to, String subject, String content, File... files) throws Exception;
    public boolean sendEmail(String to, String subject, String content, File... files) throws Exception;
    public boolean sendEmail(String to, String subject, String content) throws Exception;
}
