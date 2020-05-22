package info.androidhive.loginandregistration.session;

import android.graphics.Bitmap;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class User {
    private int id;
    private String username;
    private String email;
    private Bitmap pic;
    private String state;

    private String password;
    private Date birthday;


    public User(String username, String email, int id) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public User() {
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public String getEmail() {
        return email;
    }

    public boolean nameLike(String name) {
        return this.username.toLowerCase().startsWith(name.toLowerCase()) || name.equals("");
    }

    public int getId() {
        return this.id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirthday(String birthday) throws ParseException {
        String format = "";
        if(Pattern.matches("..-..-....", birthday) ||
                Pattern.matches(".-..-....", birthday) ||
                Pattern.matches("..-.-....", birthday) ||
                Pattern.matches(".-.-....", birthday))
            format = "dd-mm-yyyy";
        else if (Pattern.matches("....-..-..", birthday))
            format = "yyyy-mm-dd";

        this.birthday = new SimpleDateFormat(format, Locale.GERMANY).parse(birthday);
    }

    public Date getBirthdate() {
        return this.birthday;
    }
    public String getBirthdateString() {
        return new SimpleDateFormat("dd-mm-yyyy", Locale.GERMANY).format(birthday);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", pic=" + pic +
                ", state='" + state + '\'' +
                ", password='" + password + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
