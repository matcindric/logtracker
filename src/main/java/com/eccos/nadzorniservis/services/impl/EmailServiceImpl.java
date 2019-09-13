package com.eccos.nadzorniservis.services.impl;

import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.eccos.nadzorniservis.data.Email;
import com.eccos.nadzorniservis.services.EmailService;

//@Service
@Component
public class EmailServiceImpl implements EmailService {
    
    @Autowired
    public JavaMailSender emailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    
    private static final Logger logger = LogManager.getLogger(EmailServiceImpl.class);
    

    public EmailServiceImpl() {
        
    }

    
    @Override
    public void sendMail(final Email email) {
        SimpleMailMessage simpleMail = new SimpleMailMessage();
        simpleMail.setFrom(email.getFrom());
        simpleMail.setTo(email.getTo()); 
        simpleMail.setSubject(email.getSubject()); 
        simpleMail.setText(email.getMessage());
        emailSender.send(simpleMail);        
    }
    
    
    @Override
    public void sendHtmlMail(Email email) {
        try {
            
            MimeMessage htmlMail = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(htmlMail, true);
            helper.setFrom(email.getFrom());
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage(), true);
            emailSender.send(htmlMail);
            logger.info("Send email '{}' to: {}", email.getSubject(), email.getTo());
        } catch (Exception e) {
            logger.error(String.format("Problem with sending email to: {}, error message: {}", email.getTo(), e.getMessage()));
            logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
    }
    
    public String constructHTMLTemplateEmailBody(String templateName, Context context) {       
        return templateEngine.process(templateName, context);
    }
    
}