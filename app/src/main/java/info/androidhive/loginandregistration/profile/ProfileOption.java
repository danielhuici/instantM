package info.androidhive.loginandregistration.profile;

import android.graphics.Bitmap;
/**
 * Define las propiedades de una opción de perfil
 * @author Martín Gascón
 * @author Eduardo Ruiz
 * @author Daniel Huici
 * @version 1.0
 */
public class ProfileOption {
    private String title;
    private Bitmap pic;
    private String value;

    public ProfileOption(String title, Bitmap pic, String value) {
        this.title = title;
        this.pic = pic;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
