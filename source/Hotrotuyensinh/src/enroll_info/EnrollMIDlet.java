//EnrollMIDlet.java
// Copyright 2003 Le Ngoc Quoc Khanh.
//Main MIDlet
package enroll_info;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import support.HttpPoster;
import support.MessageBox;
import support.ProgressIndicator;
import support.VietSign;

public class EnrollMIDlet extends MIDlet {
    public final static String CHARSET = "UTF-8";
    public final static String NOTES = "Ch\u01b0\u01a1ng tr\u00ecnh s\u1ebd trao \u0111\u1ed5i d\u1eef li\u1ec7u v\u1edbi m\u1ea1ng, vi\u1ec7c n\u00e0y c\u00f3 th\u1ec3 s\u1ebd t\u1ed1n c\u01b0\u1edbc ph\u00ed."
            + "\nB\u1ea1n c\u00f3 \u0111\u1ed3ng \u00fd kh\u00f4ng?";
    // public final static String CONTENT_TYPE = "text/xml; charset=\"" +
    // CHARSET + "\"";
    // Binary HTTP type
    public final static String CONTENT_TYPE = "application/octet-stream";
    // JAX-RPC SOAP namespace
    public static final String METHOD_NAME_SPACE = "http://enroll.test/wsdl/EnrollService";
    // Exchange's phone number
    public static String SMS_NUM;
    public static String CONTACT_MAIL;
    public static String MAIL_HOST;
    public final String serverUrl;

    private ListMain mainScreen;
    private HttpPoster httpPoster;
    public ProgressIndicator progressIndicator;
    public VietSign vietSign;
    public MessageBox messageBox = null;

    public EnrollMIDlet() {
        serverUrl = getAppProperty("Server-URL");
        SMS_NUM = getAppProperty("SMS-NUM");
        CONTACT_MAIL = getAppProperty("CONTACT-MAIL");
        MAIL_HOST = getAppProperty("MAIL-HOST");

        mainScreen = new ListMain(this);
    }

    void deInitialize(boolean all) {
        progressIndicator = null;
        messageBox = null;
        // VietSign vietSign = null;
    }

    public void destroyApp(boolean unconditional) {

    }

    void EnrollQuit() {
        httpPoster.abort();
        destroyApp(false);
        notifyDestroyed();
    }

    public HttpPoster getHttpPoster() {
        return httpPoster;
    }

    public Displayable getMainScreen() {
        return mainScreen;
    }

    public void pauseApp() {
        mainScreen = null;
        httpPoster.abort();
        httpPoster = null;
        vietSign = null;
    }

    // Display Error
    public void setError(String error, Displayable dis) {
        Alert errorAlert = new Alert("\u263b L\u1ed7i x\u1ea3y ra. ", error,
                null, AlertType.ERROR);
        errorAlert.setTimeout(Alert.FOREVER);
        if (dis != null)
            Display.getDisplay(this).setCurrent(errorAlert, dis);
        else
            Display.getDisplay(this).setCurrent(errorAlert, mainScreen);
    }

    // Display Status
    public void setStatus(String status, Displayable dis) {
        Alert statusAlert = new Alert("\u263a Tr\u1ea1ng th\u00e1i", status,
                null, AlertType.INFO);
        statusAlert.setTimeout(Alert.FOREVER);
        if (dis != null)
            Display.getDisplay(this).setCurrent(statusAlert, dis);
        else
            Display.getDisplay(this).setCurrent(statusAlert, mainScreen);
    }

    public void startApp() {
        httpPoster = new HttpPoster(serverUrl);
        vietSign = new VietSign(this);
        mainScreen = new ListMain(this);

        Displayable current = Display.getDisplay(this).getCurrent();
        // Display splash screen
        if (current == null) {
            try {
                Alert splashScreen;
                Image logoImage = Image.createImage("/logo.png");
                splashScreen = new Alert(null, null, logoImage, AlertType.INFO);
                splashScreen.setTimeout(2000);
                Display.getDisplay(this).setCurrent(splashScreen, mainScreen);
            } catch (Exception e) {
            }
        } else {
            Display.getDisplay(this).setCurrent(current);
        }
    }
}