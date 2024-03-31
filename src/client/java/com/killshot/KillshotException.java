package com.killshot;

public class KillshotException extends Throwable {
    public final String prologue;
    public final String message;
    public final String finalMessage;

    public KillshotException(final String finalMessage) {
        this.finalMessage = finalMessage;
        this.prologue = "";
        this.message = "";
    }

    public KillshotException(final String prologue, final String message) {
        this.prologue = prologue;
        this.message = message;
        this.finalMessage = prologue + message;
    }

    @Override
    public String getMessage() {
        return finalMessage;
    }
}