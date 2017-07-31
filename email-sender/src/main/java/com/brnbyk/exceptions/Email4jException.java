/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.brnbyk.exceptions;

/**
 *
 * @author Baran Buyuk <baranbuyuk@globalbilisim.com>
 */
public class Email4jException extends Throwable {

    private static final long serialVersionUID = -2970928528402661523L;

    public Email4jException() {
        super();
    }

    public Email4jException(String message) {
        super(message);

    }

    public Email4jException(Throwable throwable) {
        super(throwable);

    }

    public Email4jException(String message, Throwable throwable) {
        super(message, throwable);

    }

}
