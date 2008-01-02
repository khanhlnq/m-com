//ListMain.java
// Copyright 2003 Le Ngoc Quoc Khanh.
//List application main task
package enroll_info;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Ticker;

public class ListMain extends List implements CommandListener {
    // List names of main tasks
    private static final String[] mainStringArray = {
            "Gi\u1ea3i \u0111\u00e1p th\u00f4ng tin",
            "Th\u00f4ng tin tr\u01b0\u1eddng", "T\u00ecm h\u1ed3 s\u01a1",
            "Xem k\u1ebft qu\u1ea3 tuy\u1ec3n sinh", "Gi\u00fap \u0111\u1ee1",
            "Li\u00ean h\u1ec7" };
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);
    private final EnrollMIDlet midlet;
    private final Ticker ticker;

    private final Command quitCommand;
    private UnivList univList = null;
    private ContactScreen contactScreen = null;
    private ExplainScreen explainScreen = null;
    private FindRecordScreen recordScreen = null;
    private ViewMarkScreen markScreen = null;

    private Form helpScreen = null;

    ListMain(EnrollMIDlet midlet) {
        super("Ch\u1ee9c n\u0103ng ch\u00ednh", Choice.IMPLICIT,
                mainStringArray, null);
        this.midlet = midlet;
        ticker = new Ticker(
                "Ch\u01b0\u01a1ng tr\u00ecnh h\u1ed7 tr\u1ee3 tuy\u1ec3n sinh");
        setTicker(ticker);

        quitCommand = new Command("Tho�t", Command.EXIT, 0);

        addCommand(quitCommand);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            deInitialize(false);
            switch (((List) d).getSelectedIndex()) {
            case 0:
                explainScreen = new ExplainScreen(midlet);
                Display.getDisplay(midlet).setCurrent(explainScreen);
                break;
            case 1:
                univList = new UnivList(midlet);
                Display.getDisplay(midlet).setCurrent(univList);
                break;
            case 2:
                recordScreen = new FindRecordScreen(midlet);
                Display.getDisplay(midlet).setCurrent(recordScreen);
                break;
            case 3:
                markScreen = new ViewMarkScreen(midlet);
                Display.getDisplay(midlet).setCurrent(markScreen);
                break;
            case 4:
                displayHelpScreen();
                break;
            // Chose Li\u00ean h\u1ec7
            case 5:
                contactScreen = new ContactScreen(midlet);
                Display.getDisplay(midlet).setCurrent(contactScreen);
                break;
            default:
                break;
            }
        } else if (c == backCommand) {
            helpScreen = null;
            Display.getDisplay(midlet).setCurrent(this);
        } else if (c == quitCommand) {
            midlet.EnrollQuit();
        }
    }

    void deInitialize(boolean all) {
        univList = null;
        contactScreen = null;
        explainScreen = null;
        recordScreen = null;
        markScreen = null;
        helpScreen = null;
    }

    void displayHelpScreen() {
        helpScreen = null;
        helpScreen = new Form("Gi\u00fap \u0111\u1ee1");
        helpScreen
                .setTicker(new Ticker(
                        "Phi\u00ean b\u1ea3n 1.0-T\u00e1c gi\u1ea3: L\u00ea Ng\u1ecdc Qu\u1ed1c Kh\u00e1nh"));
        helpScreen
                .append(new StringItem(null,
                        "Ch\u01b0\u01a1ng tr\u00ecnh h\u1ed7 tr\u1ee3 th\u00f4ng tin tuy\u1ec3n sinh."));
        helpScreen.addCommand(backCommand);
        helpScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(helpScreen);
    }
}