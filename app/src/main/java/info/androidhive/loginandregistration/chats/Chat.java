package info.androidhive.loginandregistration.chats;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.util.List;

import info.androidhive.loginandregistration.scaledrone.Message;

public abstract class Chat {
    protected String name;
    protected Bitmap pic;
    protected boolean isGroup;

    protected List<Message> messages;

    protected abstract String getTitle();
    protected abstract String getSubtitle();
    protected abstract Bitmap getPic();
    protected abstract boolean nameLike(String target);

}
