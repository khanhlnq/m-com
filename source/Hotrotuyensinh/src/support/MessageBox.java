//MessageBox.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package support;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

public class MessageBox extends Form {

    public MessageBox(String text, Command okCommand, Command cancelCommand,
            CommandListener listener) {
        super("X\u00e1c nh\u1eadn");
        append(new StringItem(null, text));
        addCommand(okCommand);
        addCommand(cancelCommand);
        setCommandListener(listener);
    }
}