package com.br.phdev.exceptions;

public class ScriptException extends Exception {

    public ScriptException() {
        super();
    }

    public ScriptException(String msg) {
        super(msg);
    }

    public ScriptException(Exception e) {
        super(e);
    }

    public ScriptException(String msg, Exception e){
        super(msg, e);
    }

}