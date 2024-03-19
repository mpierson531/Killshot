package com.killshot;

class KillshotException extends Throwable {
    String prologue = "";
    String message = "";
    String finalMessage = "";

    KillshotException(final String prologue, final String message) {
        this.prologue = prologue;
        this.message = message;
        this.finalMessage = prologue + message;
    }
}
