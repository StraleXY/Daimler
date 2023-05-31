package com.tim1.daimlerback.utils;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendGridMailService {
    SendGrid sendGrid;

    public SendGridMailService() {
        this.sendGrid = new SendGrid("API_KEY");
    }

    public Mail verificationMail(String token, String email, String subject, String hintText, String buttonText, String redirectUrl) {
        String html= "<html> <head> <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\"> <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin> <link href=\"https://fonts.googleapis.com/css2?family=Cardo&family=Roboto&display=swap\" rel=\"stylesheet\"> <style> @import url('https://fonts.googleapis.com/css2?family=Cardo&family=Roboto&display=swap'); </style> </head> <body> <div style=\"width: 1000px; text-align:center\"> <h1 style=\"margin-bottom: 0;font-family:Cardo, serif; font-style: normal;font-size:36pt;\"> Welcome to Daimler!</h1> <p style=\"margin-left:50px; margin-right:50px;margin-top:20px; margin-bottom: 100px;font-family:Cardo, serif; font-style: normal;font-size:16pt;\">" + hintText + "</p> <button style=\"font-family:Roboto, sans-serif; border-radius:15px; letter-spacing:0.25em; font-size: 16pt; background-color:#212121; color: white; width: 300px; padding: 10px;\"> <a href=\"" + redirectUrl + "?token=" + token + "\" style=\" text-decoration: none; color:white; display: inline-block; width:100%;\">" + buttonText + "</a></button> </div> </body> </html>\n";
        Email from = new Email("");
        Email to = new Email(email);
        Content content = new Content("text/html", html);
        return new Mail(from, subject, to, content);
    }

    public Mail invitationMail(String email, String name, String from, String to, String hrefAccept, String hrefReject) {
        String html = "<html>\n" +
                "    <head>\n" +
                "        <link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n" +
                "        <link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n" +
                "        <link href=\"https://fonts.googleapis.com/css2?family=Cardo&family=Roboto&display=swap\" rel=\"stylesheet\">\n" +
                "        <style> @import url('https://fonts.googleapis.com/css2?family=Cardo&family=Roboto&display=swap');\n" +
                "p {\n" +
                "font-family:Cardo, serif; font-style: normal;font-size:16pt;\n" +
                "}\n" +
                ".left {\n" +
                "    font-weight: bold;\n" +
                "    text-align: right;\n" +
                "    font-size: 16pt;\n" +
                "    padding-right: 30px;\n" +
                "}\n" +
                ".right {\n" +
                "    text-align: left;\n" +
                "    padding-left: 10px;\n" +
                "}\n" +
                "        </style>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <div style=\"width: 1000px; text-align:center;\">\n" +
                "            <h1 style=\"margin-bottom: 0;font-family:Cardo, serif; font-style: normal;font-size:36pt;\">\n" +
                "                You've received a ride invitation!\n" +
                "            </h1>\n" +
                "            <div style=\"margin-left:50px; margin-right:50px;margin-top:20px; margin-bottom: 50px;display: flex;justify-content: center;align-items: center;\">\n" +
                "                <div style=\"float:left; width: 500px\">\n" +
                "                    <p class=\"left\">Invited by:</p>\n" +
                "                    <p class=\"left\">From:</p>\n" +
                "                    <p class=\"left\">To:</p>\n" +
                "                </div>\n" +
                "                <div style=\"float:left; width: 500px\">\n" +
                "                    <p class=\"right\">"+ name +"</p>\n" +
                "                    <p class=\"right\">"+ from +"</p>\n" +
                "                    <p class=\"right\">"+ to +"</p>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            <div style=\"clear: both\">\n" +
                "                <button style=\"font-family:Roboto, sans-serif; border-radius:15px; letter-spacing:0.25em; font-size: 16pt; background-color:#212121; color: white; width: 300px; padding: 10px; height: 60px;\">\n" +
                "                    <a href=\""+ hrefAccept +"\" style=\"text-decoration: none; color:white; display: inline-block; width:100%;\"> ACCEPT </a>\n" +
                "                </button>\n" +
                "                <button style=\"font-family:Roboto, sans-serif; border-radius:15px; letter-spacing:0.25em; font-size: 16pt; background-color:red; color: white; width: 300px; padding: 10px;height:60px;\">\n" +
                "                    <a href=\""+ hrefReject +"\" style=\"text-decoration: none; color:white; display: inline-block; width:100%;\"> DECLINE </a>\n" +
                "                </button>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </body>\n" +
                "</html>\n" +
                "\n";
        Email f = new Email("");
        Email t = new Email(email);
        Content content = new Content("text/html", html);
        return new Mail(f, "Ride invitation", t, content);
    }

    private void send(Mail mail) {
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = this.sendGrid.api(request);
        } catch (IOException ex) {
            System.out.println("SENDGRID MAIL ERROR");
            ex.printStackTrace();
        }
    }

    public void sendRegistrationMail(String token, String email) {
        Mail mail = verificationMail(token, email, "Daimler - Account verification", "Click the button below to verify your account","VERIFY", "http://localhost:4200/verification");
        send(mail);
    }

    public void sendPasswordRecoveryMail(String token, String email) {
        Mail mail = verificationMail(token, email, "Daimler - Account verification", "Click the button below to verify your account","VERIFY", "http://localhost:4200/verification");
        send(mail);
    }

    public void sendRideInvitationMail(String email, String name, String from, String to, String hrefAccept, String hrefReject) {
        Mail mail = invitationMail(email, name, from, to, hrefAccept, hrefReject);
        send(mail);
    }
}
