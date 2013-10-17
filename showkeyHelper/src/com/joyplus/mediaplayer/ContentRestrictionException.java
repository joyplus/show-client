package com.joyplus.mediaplayer;

public class ContentRestrictionException extends RuntimeException {
	//serial Version UID for Android System
    private static final long serialVersionUID = 816136015813043499L;

    public ContentRestrictionException() {
        super();
    }

    public ContentRestrictionException(String msg) {
        super(msg);
    }

    public ContentRestrictionException(Exception cause) {
        super(cause);
    }
}

