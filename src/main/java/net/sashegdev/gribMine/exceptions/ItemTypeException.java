package net.sashegdev.gribMine.exceptions;
public class ItemTypeException extends Exception {
    private String msg;
    public ItemTypeException(String msg) {
        this.msg = msg;
    }

    public String getMessage() { return msg; }
}