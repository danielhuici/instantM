package info.androidhive.loginandregistration.chats;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

import info.androidhive.loginandregistration.scaledrone.Message;

/**
 * Define las propiedades de un chat.
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */


public abstract class Chat implements Serializable {
    protected String name;
    protected Bitmap pic;
    protected boolean isGroup;


    public abstract String getTitle();
    protected abstract String getSubtitle();
    protected abstract Bitmap getPic();
    protected abstract boolean nameLike(String target);

}
