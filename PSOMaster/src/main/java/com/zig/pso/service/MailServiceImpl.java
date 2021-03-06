/************************************************************************************************************
 * Class Name :  MailServiceImpl.java
 * Description:  
 * 
 * Author     :  Nilesh Patil
 * Date       :  Nov 19, 2017
 * **********************************************************************************************************
 */
package com.zig.pso.service;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.zig.pso.config.InitConfiguration;
import com.zig.pso.logging.PSOLoggerSrv;
import com.zig.pso.rest.bean.Mail;

/**
 * 
 */
@Service
public class MailServiceImpl implements MailService
{
    static final Logger logger = Logger.getLogger(MailServiceImpl.class);
    
    @Autowired
    JavaMailSender mailSender;

    /* (non-Javadoc)
     * @see com.zig.pso.service.MailService#sendEmail(com.zig.pso.rest.bean.Mail)
     */
    @Override
    public void sendEmail(Mail mail)
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        
        try {
 
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
 
            mimeMessageHelper.setSubject(mail.getMailSubject());
            mimeMessageHelper.setFrom(mail.getMailFrom());
            
            if(null != mail.getMailTo() && mail.getMailTo().size()>0){
                for(String emailId : mail.getMailTo())
                    mimeMessageHelper.addTo(emailId);
            }
            mimeMessageHelper.setText(mail.getMailContent(),true);
            if(null != mail.getAttachments()){
                for (Object attachment: mail.getAttachments()) {
                    File file = ((ClassPathResource) attachment).getFile();
                    mimeMessageHelper.addAttachment(file.getName(), file);
                }
            }
            
            mailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (IOException e) {
            PSOLoggerSrv.printERROR(logger,"MailServiceImpl", "sendEmail", e);
        } catch (MessagingException e) {
            PSOLoggerSrv.printERROR(logger,"MailServiceImpl", "sendEmail", e);
        }catch (Exception e) {
            PSOLoggerSrv.printERROR(logger,"MailServiceImpl", "sendEmail", e);
        }
    }
    
    public static void main(String[] args)
    {
        Mail mail = new Mail();
        mail.setMailFrom("");
        //mail.setMailTo("");
        mail.setMailSubject("TEST Email");
        mail.setMailContent("");
 
        /*List < Object > attachments = new ArrayList < Object > ();
        attachments.add(new ClassPathResource("dog.jpg"));
        attachments.add(new ClassPathResource("cat.jpg"));
        mail.setAttachments(attachments);*/
 
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(InitConfiguration.class);
        MailService mailService = (MailService) context.getBean("mailService");
        mailService.sendEmail(mail);
        context.close();
    }

}
