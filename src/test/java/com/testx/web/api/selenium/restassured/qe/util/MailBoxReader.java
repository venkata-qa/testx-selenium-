
package com.testx.web.api.selenium.restassured.qe.util;


import com.testx.web.api.selenium.restassured.qe.util.propertyreader.MailBoxPropertyReader;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MailBoxReader {



    public static void main(String[] args) throws MessagingException, IOException {
readMail();
    }

 public static void    readMail() throws MessagingException, IOException {


     Properties properties = new Properties();
     properties.put(MailBoxPropertyReader.HOSTKEY,MailBoxPropertyReader.HOSTVALUE );
     properties.put(MailBoxPropertyReader.PORTKEY, MailBoxPropertyReader.PORTVALUE);
     properties.put(MailBoxPropertyReader.SSLKEY, MailBoxPropertyReader.SSLVALUE);

     Session session = Session.getDefaultInstance(properties);
     Store store = session.getStore(MailBoxPropertyReader.PROTOCOL);
     store.connect(MailBoxPropertyReader.USERNAME, MailBoxPropertyReader.PASSWORD);
     Folder inbox = store.getFolder(MailBoxPropertyReader.FOLDERNAME);
     inbox.open(Folder.READ_ONLY);

     Message[] messages = inbox.getMessages();

     for (Message message : messages) {
         String subject = message.getSubject();
          message.getContent();

         System.out.println("Subject: " + subject);
         // Assuming that 'message' is the Message object for the email
         Object content = message.getContent();
         if (content instanceof MimeMultipart) {
             MimeMultipart multipart = (MimeMultipart) content;
             for (int i = 0; i < multipart.getCount(); i++) {
                 BodyPart bodyPart = multipart.getBodyPart(i);
                 if (bodyPart.isMimeType("text/plain")) {
                     System.out.println("Body "+bodyPart.getContent());
                 }
                 if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                     // Get the attachment file name
                     String fileName = bodyPart.getFileName();
                     // Download the attachment content
                     InputStream inputStream = bodyPart.getInputStream();
                     FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/email_contents/"+fileName);
                     byte[] buffer = new byte[4096];
                     int bytesRead = -1;
                     while ((bytesRead = inputStream.read(buffer)) != -1) {
                         fileOutputStream.write(buffer, 0, bytesRead);
                     }
                     fileOutputStream.close();
                     inputStream.close();
                     System.out.println("Attachment downloaded: " + fileName);
                 }

             }
         } else if (content instanceof String) {
             System.out.println(content);
         }


     }
     inbox.close(false);
     store.close();



 }


}
