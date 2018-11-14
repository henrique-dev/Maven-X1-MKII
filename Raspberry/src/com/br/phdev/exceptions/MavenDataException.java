package com.br.phdev.exceptions;

public class MavenDataException extends Exception {

    public MavenDataException(Exception e) {
        super(e);
    }

    public MavenDataException(String msg, Exception e){
        super(msg, e);
    }

}
