package com.brnbyk;

import com.brnbyk.annotations.Body;
import com.brnbyk.annotations.CC;
import com.brnbyk.annotations.Email;
import com.brnbyk.annotations.From;
import com.brnbyk.annotations.Subject;
import com.brnbyk.annotations.To;
import com.brnbyk.exceptions.Email4jException;
import com.brnbyk.util.ClassTypeName;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Baran Buyuk <baranbuyuk@globalbilisim.com>
 */
public class Email4j {

    //Mandatory
    private static Optional<String> host;
    private static Optional<String> username;
    private static Optional<String> password;

    //Optional
    private static boolean tlsEnable;
    private static int port;
    private static boolean auth;
    private static boolean htmlContent;

    private static Object object;

    private static Map<Class<? extends Annotation>, List<String>> data = new HashMap<>();

    private Email4j(Object obj) throws Email4jException {
        object = obj;
        data.put(From.class, new ArrayList<>());
        data.put(To.class, new ArrayList<>());
        data.put(CC.class, new ArrayList<>());
        data.put(Subject.class, new ArrayList<>());
        data.put(Body.class, new ArrayList<>());
        checkEmailAnnotation();
    }

    public static Binder bind(Object obj) throws Email4jException {
        new Email4j(obj);
        return new Binder();
    }

    private static void checkEmailAnnotation() throws Email4jException {
        if (!object.getClass().isAnnotationPresent(Email.class)) {
            throw new Email4jException("You should add annotation to the class named " + object.getClass().getCanonicalName());
        }
        //get email object
        Email emailAnnotation = object.getClass().getAnnotationsByType(Email.class)[0];

        tlsEnable = emailAnnotation.tlsEnable();
        port = emailAnnotation.port();
        auth = emailAnnotation.auth();
        host = Optional.of(emailAnnotation.host()).filter(s -> !s.isEmpty());
        username = Optional.of(emailAnnotation.username()).filter(s -> !s.isEmpty());
        password = Optional.of(emailAnnotation.password()).filter(s -> !s.isEmpty());

        if (!host.isPresent()) {
            throw new Email4jException("Host is empty. please enter host name");
        } else if (!username.isPresent()) {
            throw new Email4jException("Username is empty. please enter username name");
        } else if (!password.isPresent()) {
            throw new Email4jException("Password is empty. please enter password name");
        } else {
            //do nothing
        }

        findDeclaredFields();
    }

    private static void findDeclaredFields() {
        List<Field> declaredFields = Arrays.asList(object.getClass().getDeclaredFields());
        declaredFields.stream().forEach(field -> {
            data.forEach((k, v) -> {
                if (field.isAnnotationPresent(k)) {
                    v.addAll(getDeclaredFieldData(field));
                }
            });
        });
    }

    @SuppressWarnings("unchecked")
    private static Collection<? extends String> getDeclaredFieldData(Field field) {
        try {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Body.class)) {
                htmlContent = field.getAnnotation(Body.class).htmlContent();
            }
            if (field.isAnnotationPresent(To.class) || field.isAnnotationPresent(CC.class)) {
                switch (field.getType().getName()) {
                    case ClassTypeName.STRING:
                        return Arrays.asList((String) field.get(object));
                    case ClassTypeName.STRING_ARRAY:
                        return Arrays.asList((String[]) field.get(object));
                    case ClassTypeName.LIST:
                        return (Collection<? extends String>) field.get(object);
                    default:
                        break;
                }

            }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(Email4j.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Collections.EMPTY_LIST;
    }

    public static class Binder {

        private final Properties props = new Properties();

        public Binder() {
            props.put("mail.smtp.starttls.enable", tlsEnable ? "true" : false);
            props.put("mail.smtp.auth", auth ? "true" : "false");
            props.put("mail.smtp.host", host.get());
            props.put("mail.smtp.port", port);
        }

        public boolean send() throws Email4jException {
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username.get(), password.get());
                }
            });
            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(data.get(From.class).get(0)));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(String.join(",", data.get(To.class))));
                if (!data.get(CC.class).isEmpty()) {
                    message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(String.join(",", data.get(CC.class))));
                }
                message.setSubject(data.get(Subject.class).get(0));
                if (htmlContent) {
                    message.setContent(data.get(Body.class).get(0), "text/html");
                } else {
                    message.setText(data.get(Body.class).get(0));
                }
                Transport.send(message);
                return true;
            } catch (MessagingException e) {
                throw new Email4jException("", e);
            }
        }

    }
}
