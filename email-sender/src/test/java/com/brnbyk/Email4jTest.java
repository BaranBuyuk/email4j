/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brnbyk;

import com.brnbyk.annotations.Body;
import com.brnbyk.annotations.CC;
import com.brnbyk.annotations.Email;
import com.brnbyk.annotations.From;
import com.brnbyk.annotations.Subject;
import com.brnbyk.annotations.To;
import com.brnbyk.exceptions.Email4jException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author Baran Buyuk <baranbuyuk@globalbilisim.com>
 */
public class Email4jTest {

    private EmailObject email;

    @Test
    public void createEmailObject() {
        try {
            email = new EmailObject();
            Email4j.bind(email).send();

        } catch (Email4jException ex) {
            Logger.getLogger(Email4jTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Email(host = "mail.globalbilisim.com", username = "baran.buyuk@globalbilisim.com", password = "baran123", port = 587, auth = true, tlsEnable = true)
    class EmailObject {

        @From
        private final String from;
        @To
        private final List<String> to;
        @CC
        private final String[] cc;
        @Subject
        private final String subject;
        @Body(htmlContent = false)
        private final String body;

        EmailObject() {
            this.from = "baran.buyuk@globalbilisim.com";
            this.cc = new String[]{};
            this.to = new ArrayList<>(Arrays.asList("baran.buyuk@globalbilisim.com"));
            this.body = "TEST EMAIL";
            this.subject = "TEST";

        }
    }

}
