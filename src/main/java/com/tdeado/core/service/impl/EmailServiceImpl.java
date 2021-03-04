package com.tdeado.core.service.impl;

import com.tdeado.core.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    public String from;

    @Override
    public boolean sendEmail(String from, String to, String subject, String content, File... files) throws Exception {
        //复杂邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        for (File file : files) {
            messageHelper.addAttachment(file.getName(), file);
        }
        mailSender.send(mimeMessage);
        return true;
    }

    @Override
    public boolean sendEmail(String to, String subject, String content, File... files) throws Exception {
        //复杂邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,true);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        for (File file : files) {
            messageHelper.addAttachment(file.getName(), file);
        }
        mailSender.send(mimeMessage);
        return true;
    }

    @Override
    public boolean sendEmail(String to, String subject, String content) throws Exception {
        //复杂邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        mailSender.send(mimeMessage);
        return true;
    }

}
