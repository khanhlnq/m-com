//ViewMarkScreen.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package enroll_info;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;

import support.HttpPosterListener;
import support.MessageBox;
import support.ProgressIndicator;
import support.ProgressListener;
import support.SoapProcessor;
import support.VietSign;

class ViewMarkScreen extends Form implements CommandListener,
        HttpPosterListener, ProgressListener {
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);
    private static final Command cancelCommand = new Command(
            "H\u1ee7y b\u1ecf", Command.CANCEL, 1);;
    private static final int MAX_COUNT = 5;

    EnrollMIDlet midlet;

    private Displayable previousScreen = null;
    private List markScreen = null;

    private int index = 0;

    private TextField mahosoField = null;
    private TextField sbdField = null;
    private TextField tenField = null;
    private TextField hoField = null;
    private ChoiceGroup genderField = null;
    private DateField birthdayField = null;
    private TextField idField = null;
    private TextField districtField = null;
    private TextField provinceField = null;
    private TextField univField = null;
    private TextField branchField = null;

    private Command getMarkCommand = null;
    private Command getMarkConfirm = null;

    ViewMarkScreen(EnrollMIDlet midlet) {
        super("Xem k\u1ebft qu\u1ea3 tuy\u1ec3n sinh");
        setTicker(new Ticker(
                "B\u1ea1n n\u00ean nh\u1eadp SBD, m\u00e3 h\u1ed3 s\u01a1 \u0111\u1ec3 t\u00ecm k\u1ebft qu\u1ea3 \u0111\u01b0\u1ee3c ch\u00ednh x\u00e1c"));
        this.midlet = midlet;

        mahosoField = new TextField("M\u00e3 h\u1ed3 s\u01a1", null, 15,
                TextField.ANY);
        sbdField = new TextField("SBD", null, 10, TextField.ANY);
        hoField = new TextField("H\u1ecd", null, 50, TextField.ANY);
        hoField.addCommand(VietSign.signCommand);
        hoField.setItemCommandListener(midlet.vietSign);
        tenField = new TextField("T\u00ean", null, 50, TextField.ANY);
        tenField.addCommand(VietSign.signCommand);
        tenField.setItemCommandListener(midlet.vietSign);
        genderField = new ChoiceGroup("Ph\u00e1i", Choice.EXCLUSIVE);
        genderField.append("Nam", null);
        genderField.append("N\u1eef", null);
        genderField.setSelectedIndex(0, true);
        birthdayField = new DateField("Ng\u00e0y sinh", DateField.DATE);
        idField = new TextField("CMND", null, 9, TextField.NUMERIC);
        districtField = new TextField("T\u00ean huy\u1ec7n", null, 50,
                TextField.ANY);
        districtField.addCommand(VietSign.signCommand);
        districtField.setItemCommandListener(midlet.vietSign);
        provinceField = new TextField("T\u00ean t\u1ec9nh", null, 50,
                TextField.ANY);
        provinceField.addCommand(VietSign.signCommand);
        provinceField.setItemCommandListener(midlet.vietSign);
        univField = new TextField("M\u00e3 tr\u01b0\u1eddng", null, 3,
                TextField.ANY);
        branchField = new TextField("M\u00e3 ng\u00e0nh", null, 3,
                TextField.ANY);

        getMarkCommand = new Command("T\u00ecm", Command.SCREEN, 0);

        append(mahosoField);
        append(sbdField);
        append(hoField);
        append(tenField);
        append(genderField);
        append(birthdayField);
        append(univField);
        append(branchField);
        append(idField);
        append(districtField);
        append(provinceField);
        addCommand(getMarkCommand);
        addCommand(backCommand);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            int selectedIndex = ((List) d).getSelectedIndex();
            if (d == markScreen) {
                // Return to MAX_COUNT univesities in the list
                if (selectedIndex == 0) {
                    if (index > 0) {
                        index--;
                        getMark(mahosoField.getString(), sbdField.getString(),
                                hoField.getString(), tenField.getString(),
                                genderField.getSelectedIndex(), birthdayField
                                        .getDate(), idField.getString(),
                                districtField.getString(), provinceField
                                        .getString(), univField.getString(),
                                branchField.getString(), index, MAX_COUNT);
                    } else
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                        markScreen);
                }
                // Next to MAX_COUNT univesities in the list
                else if (selectedIndex == markScreen.size() - 1) {
                    if (markScreen.size() < MAX_COUNT + 2) {
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                        markScreen);
                    } else {
                        index++;
                        getMark(mahosoField.getString(), sbdField.getString(),
                                hoField.getString(), tenField.getString(),
                                genderField.getSelectedIndex(), birthdayField
                                        .getDate(), idField.getString(),
                                districtField.getString(), provinceField
                                        .getString(), univField.getString(),
                                branchField.getString(), index, MAX_COUNT);
                    }
                }
                // Select one university in university list, display university
                // detail
                else {
                }
            }
        } else if (c == getMarkCommand) {
            if (mahosoField.getString().equals("")
                    && sbdField.getString().equals("")
                    && hoField.getString().equals("")
                    && tenField.getString().equals("")
                    && birthdayField.getDate() == null
                    && idField.getString().equals("")
                    && districtField.getString().equals("")
                    && provinceField.getString().equals("")
                    && univField.getString().equals("")
                    && branchField.getString().equals("")) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i cung c\u1ea5p \u00edt nh\u1ea5t m\u1ed9t d\u1eef ki\u1ec7n",
                                this);
            } else {
                previousScreen = this;
                getMarkConfirm = new Command("\u0110\u1ed3ng \u00fd",
                        Command.OK, 0);
                midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                        getMarkConfirm, cancelCommand, this);
                Display.getDisplay(midlet).setCurrent(midlet.messageBox);
            }
        } else if (c == getMarkConfirm) {
            deInitialize(false);
            getMark(mahosoField.getString(), sbdField.getString(), hoField
                    .getString(), tenField.getString(), genderField
                    .getSelectedIndex(), birthdayField.getDate(), idField
                    .getString(), districtField.getString(), provinceField
                    .getString(), univField.getString(), branchField
                    .getString(), index, MAX_COUNT);
        }

        else if (c == cancelCommand)
            Display.getDisplay(midlet).setCurrent(previousScreen);
        else if (c == backCommand) {
            if (d == this) {
                deInitialize(true);
                Display.getDisplay(midlet).setCurrent(midlet.getMainScreen());
            } else if (d == markScreen) {
                deInitialize(false);
                Display.getDisplay(midlet).setCurrent(this);
            }
        }
    }

    private void deInitialize(boolean all) {
        index = 0;
        previousScreen = null;
        markScreen = null;
        getMarkConfirm = null;
        midlet.messageBox = null;
        midlet.progressIndicator = null;
    }

    void displayResultList(Vector result) {
        if (result != null && result.size() > 1) {
            markScreen = null;
            markScreen = new List("Xem t\u1eeb k\u1ebft qu\u1ea3 th\u1ee9 "
                    + (index * MAX_COUNT + 1), Choice.IMPLICIT);
            Ticker ticker = new Ticker("T\u00ecm \u0111\u01b0\u1ee3c "
                    + (String) result.elementAt(0) + " k\u1ebft qu\u1ea3");
            markScreen.setTicker(ticker);
            markScreen.addCommand(backCommand);
            markScreen.setCommandListener(this);
            if (index == 0) {
                markScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                null);
            } else {
                markScreen.append("\u25b2 Xem " + MAX_COUNT
                        + " k\u1ebft qu\u1ea3 ph\u00eda tr\u01b0\u1edbc", null);
            }

            int i = 1;
            while (i < result.size()) {
                StringBuffer recordSB = new StringBuffer();

                recordSB.append("#SBD:" + (String) result.elementAt(i++));
                recordSB.append("\nH\u1ecd t\u00ean:"
                        + (String) result.elementAt(i++));
                recordSB
                        .append("\nPh\u00e1i:"
                                + ((((String) result.elementAt(i++))
                                        .equals("0")) ? "Nam" : "N\u1eef"));
                recordSB.append("\nNg\u00e0y sinh:"
                        + (String) result.elementAt(i++));
                recordSB.append("\nHuy\u1ec7n:"
                        + (String) result.elementAt(i++));
                recordSB
                        .append("\nT\u1ec9nh:" + (String) result.elementAt(i++));
                recordSB.append("\n\u0110\u1ecba ch\u1ec9:"
                        + (String) result.elementAt(i++));
                recordSB.append("\n\u0110\u1ed1i t\u01b0\u1ee3ng:"
                        + (String) result.elementAt(i++));
                recordSB
                        .append("\nKV\u01afT:" + (String) result.elementAt(i++));
                recordSB.append("\nTr\u01b0\u1eddng:"
                        + (String) result.elementAt(i++));
                recordSB.append("\nNg\u00e0nh:"
                        + (String) result.elementAt(i++));
                recordSB.append("\nDM1:" + (String) result.elementAt(i++));
                recordSB.append("\nDM2:" + (String) result.elementAt(i++));
                recordSB.append("\nDM3:" + (String) result.elementAt(i++));
                recordSB.append("\nDM4:" + (String) result.elementAt(i++));
                recordSB.append("\n\u0110i\u1ec3m th\u01b0\u1edfng:"
                        + (String) result.elementAt(i++));

                markScreen.append(recordSB.toString(), null);
            }
            if (result.size() < (MAX_COUNT * 16 + 1)) {
                markScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                null);
            } else {
                markScreen.append("\u25bc Xem " + MAX_COUNT
                        + " k\u1ebft qu\u1ea3 ti\u1ebfp theo", null);
            }
            Display.getDisplay(midlet).setCurrent(markScreen);
        } else {
            if (index == 0)
                midlet
                        .setStatus(
                                "Kh\u00f4ng t\u00ecm th\u1ea5y k\u1ebft qu\u1ea3",
                                this);
            else {
                midlet
                        .setStatus(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                markScreen);
                index--;
            }
        }
    }

    void getMark(String mahoso, String sbd, String ho, String t\u00ean,
            int gender, Date birthday, String id, String district,
            String province, String univ, String branch, int index, int maxCount) {

        StringBuffer birthdaySB = new StringBuffer();
        if (birthday != null) {
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthday);
            birthdaySB.append(birthCalendar.get(Calendar.MONTH) + 1);
            birthdaySB.append("/");
            birthdaySB.append(birthCalendar.get(Calendar.DATE));
            birthdaySB.append("/");
            birthdaySB.append(birthCalendar.get(Calendar.YEAR));
        }

        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang t\u00ecm k\u1ebft qu\u1ea3", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getMark", EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", mahoso);
        soapProcessor.addProperty("String_2", sbd);
        soapProcessor.addProperty("String_3", ho);
        soapProcessor.addProperty("String_4", t\u00ean);
        soapProcessor.addProperty("int_5", new Integer(gender));
        soapProcessor.addProperty("String_6", birthdaySB.toString());
        soapProcessor.addProperty("String_7", id);
        soapProcessor.addProperty("String_8", district);
        soapProcessor.addProperty("String_9", province);
        soapProcessor.addProperty("String_10", univ);
        soapProcessor.addProperty("String_11", branch);

        soapProcessor.addProperty("int_12", new Integer(index));
        soapProcessor.addProperty("int_13", new Integer(maxCount));

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("T\u00ecm k\u1ebft qu\u1ea3 b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    public void handleHttpPosterError(String error) {
        deInitialize(false);
        midlet.setError(error, this);
    }

    public void receiveHttpResponse(byte[] response) {
        SoapProcessor soapProcessor = new SoapProcessor(response,
                EnrollMIDlet.CHARSET);
        System.out.println("\n# SOAP Receive:\n" + new String(response));
        // System.out.println("\n# SOAP Receive:\n" +
        // soapProcessor.getResponseStr());
        System.out.println("# Compressed size: " + response.length + " Bytes");
        if (soapProcessor.isSuccess()) {
            System.out.println("# Uncompressed size: "
                    + soapProcessor.getResponseStr().getBytes().length
                    + " Bytes");
            try {
                if (soapProcessor.hasResponse("getMarkResponse")) {
                    displayResultList(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else {
                    midlet.setError("L\u1ed7i Response ko h\u1ee3p l\u1ec7",
                            this);
                }
            } catch (Exception e) {
                midlet.setError("L\u1ed7i " + e.getMessage(), this);
            }
        } else {
            midlet.setError(soapProcessor.getError(), this);
        }
        soapProcessor = null;
    }

    public void stopProgress() {
        midlet.progressIndicator.stopActivityIndicator();
        Display.getDisplay(midlet).setCurrent(this);
        midlet.getHttpPoster().abort();
        midlet.progressIndicator = null;
    }
}