//ExplainScreen.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package enroll_info;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;

import support.HttpPosterListener;
import support.MessageBox;
import support.ProgressIndicator;
import support.ProgressListener;
import support.SoapProcessor;
import support.VietSign;

class ExplainScreen extends List implements CommandListener,
        HttpPosterListener, ProgressListener {
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);
    private static final Command cancelCommand = new Command(
            "H\u1ee7y b\u1ecf", Command.CANCEL, 1);;
    private static final String[] mainStringArray = {
            "\u0110\u1ed1i t\u01b0\u1ee3ng", "\u0110\u1ee3t thi",
            "Khu v\u1ef1c", "Gi\u1ea3i \u0111\u00e1p KV\u01afT" };

    EnrollMIDlet midlet;

    private Displayable previousScreen = null;
    private Form objectInfoScreen = null;
    private Form stageInfoScreen = null;
    private Form zoneInfoScreen = null;
    private Form findZoneScreen = null;
    private Form findResultScreen = null;

    // Xa, huy\u1ec7n, t\u1ec9nh
    private TextField communeField = null;
    private TextField districtField = null;
    private TextField provinceField = null;

    private Command getObjectInfoCommand = null;
    private Command getStageInfoCommand = null;
    private Command getZoneInfoCommand = null;
    private Command findZoneCommand = null;
    private Command findZoneConfirm = null;

    ExplainScreen(EnrollMIDlet midlet) {
        super("Gi\u1ea3i \u0111\u00e1p th\u00f4ng tin", Choice.IMPLICIT,
                mainStringArray, null);
        setTicker(new Ticker(
                "Gi\u1ea3i \u0111\u00e1p c\u00e1c th\u00f4ng tin c\u1ea7n thi\u1ebft"));
        this.midlet = midlet;
        addCommand(backCommand);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            if (d == this) {
                deInitialize(false);
                switch (((List) d).getSelectedIndex()) {
                case 0:
                    if (objectInfoScreen == null) {
                        previousScreen = this;
                        getObjectInfoCommand = new Command(
                                "\u0110\u1ed3ng \u00fd", Command.OK, 0);
                        midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                                getObjectInfoCommand, cancelCommand, this);
                        Display.getDisplay(midlet)
                                .setCurrent(midlet.messageBox);
                    } else
                        Display.getDisplay(midlet).setCurrent(objectInfoScreen);
                    break;
                case 1:
                    if (stageInfoScreen == null) {
                        previousScreen = this;
                        getStageInfoCommand = new Command(
                                "\u0110\u1ed3ng \u00fd", Command.OK, 0);
                        midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                                getStageInfoCommand, cancelCommand, this);
                        Display.getDisplay(midlet)
                                .setCurrent(midlet.messageBox);
                    } else
                        Display.getDisplay(midlet).setCurrent(stageInfoScreen);
                    break;
                case 2:
                    if (zoneInfoScreen == null) {
                        previousScreen = this;
                        getZoneInfoCommand = new Command(
                                "\u0110\u1ed3ng \u00fd", Command.OK, 0);
                        midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                                getZoneInfoCommand, cancelCommand, this);
                        Display.getDisplay(midlet)
                                .setCurrent(midlet.messageBox);
                    } else
                        Display.getDisplay(midlet).setCurrent(zoneInfoScreen);
                    break;
                case 3:
                    displayFindZoneScreen();
                }
            }
        }
        // L\u1ea5y th\u00f4ng tin \u0111\u1ed1i t\u01b0\u1ee3ng
        else if (c == getObjectInfoCommand) {
            getObjectInfo();
        }
        // L\u1ea5y th\u00f4ng tin \u0111\u1ee3t thi
        else if (c == getStageInfoCommand) {
            getStageInfo();
        }
        // L\u1ea5y th\u00f4ng tin khu v\u1ef1c
        else if (c == getZoneInfoCommand) {
            getZoneInfo();
        }
        // T\u00ecm khu v\u1ef1c uu tien
        else if (c == findZoneCommand) {
            if (communeField.getString().equals("")
                    || districtField.getString().equals("")
                    || provinceField.getString().equals("")) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i nh\u1eadp t\u1ea5t c\u1ea3 c\u00e1c d\u1eef ki\u1ec7n",
                                findZoneScreen);
            } else {
                previousScreen = findZoneScreen;
                findZoneConfirm = null;
                findZoneConfirm = new Command("\u0110\u1ed3ng \u00fd",
                        Command.OK, 0);
                midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                        findZoneConfirm, cancelCommand, this);
                Display.getDisplay(midlet).setCurrent(midlet.messageBox);
            }
        }
        // T\u00ecm
        else if (c == findZoneConfirm) {
            if (communeField.getString().equals("")
                    || districtField.getString().equals("")
                    || provinceField.getString().equals("")) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i nh\u1eadp t\u1ea5t c\u1ea3 c\u00e1c d\u1eef ki\u1ec7n",
                                findZoneScreen);
            } else
                findZone();
        } else if (c == cancelCommand)
            Display.getDisplay(midlet).setCurrent(previousScreen);
        else if (c == backCommand) {
            if (d == this) {
                deInitialize(true);
                Display.getDisplay(midlet).setCurrent(midlet.getMainScreen());
            } else if (d == objectInfoScreen || d == stageInfoScreen
                    || d == zoneInfoScreen || d == findZoneScreen)
                Display.getDisplay(midlet).setCurrent(this);
            else if (d == findResultScreen)
                Display.getDisplay(midlet).setCurrent(findZoneScreen);
        }
    }

    private void deInitialize(boolean all) {
        previousScreen = null;
        findZoneScreen = null;
        findResultScreen = null;
        getObjectInfoCommand = null;
        getStageInfoCommand = null;
        getZoneInfoCommand = null;
        findZoneCommand = null;
        findZoneConfirm = null;

        communeField = null;
        districtField = null;
        provinceField = null;

        if (all) {
            objectInfoScreen = null;
            stageInfoScreen = null;
            zoneInfoScreen = null;
        }
    }

    void displayFindResult(Vector result) {
        findResultScreen = null;
        findResultScreen = new Form("K\u1ebft qu\u1ea3 KV\u01afT");
        findResultScreen.setTicker(new Ticker("T\u00ecm \u0111\u01b0\u1ee3c "
                + (String) result.elementAt(0) + " k\u1ebft qu\u1ea3"));
        if (result.size() > 1) {
            int i = 1;
            while (i < result.size()) {
                String kvut = "KV\u01afT: " + (String) result.elementAt(i++)
                        + "> ";
                StringBuffer sb = new StringBuffer();
                String communeName = (String) result.elementAt(i++);
                String commune = (String) result.elementAt(i++);
                if (commune.equals("0") || commune.equals("00")) {
                    sb.append("C\u00e1c x\u00e3 trong:");
                } else {
                    sb.append("T\u00ean x\u00e3: ");
                    sb.append(communeName);
                    sb.append("\nM\u00e3 x\u00e3: ");
                    sb.append(commune);
                }
                sb.append("\nT\u00ean huy\u1ec7n: ");
                sb.append((String) result.elementAt(i++));
                sb.append("\nM\u00e3 huy\u1ec7n: ");
                sb.append((String) result.elementAt(i++));
                sb.append("\nT\u00ean t\u1ec9nh: ");
                sb.append((String) result.elementAt(i++));
                sb.append("\nM\u00e3 t\u1ec9nh: ");
                sb.append((String) result.elementAt(i++));
                findResultScreen.append(new StringItem(kvut, sb.toString()));
                findResultScreen.addCommand(backCommand);
                findResultScreen.setCommandListener(this);
                Display.getDisplay(midlet).setCurrent(findResultScreen);
            }
        } else {
            midlet.setStatus("Kh\u00f4ng t\u00ecm th\u1ea5y KV\u01afT",
                    findZoneScreen);
        }
    }

    void displayFindZoneScreen() {
        findZoneScreen = null;
        findZoneCommand = null;
        communeField = null;
        districtField = null;
        provinceField = null;
        findZoneScreen = new Form("Gi\u1ea3i \u0111\u00e1p KV\u01afT");
        findZoneScreen
                .setTicker(new Ticker(
                        "Nh\u1eadp t\u00ean x\u00e3, huy\u1ec7n, t\u1ec9nh de t\u00ecm KV\u01afT"));

        communeField = new TextField("T\u00ean x\u00e3:", null, 50,
                TextField.ANY);
        communeField.addCommand(VietSign.signCommand);
        communeField.setItemCommandListener(midlet.vietSign);
        districtField = new TextField("T\u00ean huy\u1ec7n:", null, 50,
                TextField.ANY);
        districtField.addCommand(VietSign.signCommand);
        districtField.setItemCommandListener(midlet.vietSign);
        provinceField = new TextField("T\u00ean t\u1ec9nh:", null, 50,
                TextField.ANY);
        provinceField.addCommand(VietSign.signCommand);
        provinceField.setItemCommandListener(midlet.vietSign);
        findZoneCommand = new Command("T\u00ecm KV\u01afT", Command.SCREEN, 0);

        findZoneScreen.append(communeField);
        findZoneScreen.append(districtField);
        findZoneScreen.append(provinceField);
        findZoneScreen.addCommand(findZoneCommand);
        findZoneScreen.addCommand(backCommand);
        findZoneScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(findZoneScreen);
    }

    void displayObjectInfo(Vector result) {
        objectInfoScreen = null;
        objectInfoScreen = new Form("C\u00e1c \u0111\u1ed1i t\u01b0\u1ee3ng");
        int i = 0;
        while (i < result.size()) {
            String object = "\u0110\u1ed1i t\u01b0\u1ee3ng: "
                    + (String) result.elementAt(i++) + "> ";
            StringBuffer sb = new StringBuffer();
            sb.append("T\u00ean \u0111\u1ed1i t\u01b0\u1ee3ng: ");
            sb.append((String) result.elementAt(i++));
            sb.append("\nNh\u00f3m \u01b0u ti\u00ean: ");
            sb.append((String) result.elementAt(i++));
            objectInfoScreen.append(new StringItem(object, sb.toString()));
        }
        objectInfoScreen.addCommand(backCommand);
        objectInfoScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(objectInfoScreen);
    }

    void displayStageInfo(Vector result) {
        stageInfoScreen = null;
        stageInfoScreen = new Form("C\u00e1c \u0111\u1ee3t thi");
        int i = 0;
        while (i < result.size()) {
            String object = "\u0110\u1ee3t thi: "
                    + (String) result.elementAt(i++) + "> ";
            StringBuffer sb = new StringBuffer();
            sb.append("Ng\u00e0y b\u1eaft \u0111\u1ea7u: ");
            sb.append((String) result.elementAt(i++));
            sb.append("\nNg\u00e0y k\u1ebft th\u00fac: ");
            sb.append((String) result.elementAt(i++));
            stageInfoScreen.append(new StringItem(object, sb.toString()));
        }
        stageInfoScreen.addCommand(backCommand);
        stageInfoScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(stageInfoScreen);
    }

    void displayZoneInfo(Vector result) {
        zoneInfoScreen = null;
        zoneInfoScreen = new Form("C\u00e1c KV\u01afT");
        int i = 0;
        while (i < result.size()) {
            String object = "KV\u01afT: " + (String) result.elementAt(i++)
                    + "> ";
            StringBuffer sb = new StringBuffer();
            sb.append("Khu v\u1ef1c: ");
            sb.append((String) result.elementAt(i++));
            sb.append("\nLo\u1ea1i: ");
            sb.append((String) result.elementAt(i++));
            sb.append("\nDi\u1ec5n gi\u1ea3i: ");
            sb.append((String) result.elementAt(i++));
            zoneInfoScreen.append(new StringItem(object, sb.toString()));
        }
        zoneInfoScreen.addCommand(backCommand);
        zoneInfoScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(zoneInfoScreen);
    }

    void findZone() {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang t\u00ecm KV\u01afT", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "findZone",
                EnrollMIDlet.CHARSET);
        soapProcessor.addProperty("String_1", communeField.getString());
        soapProcessor.addProperty("String_2", districtField.getString());
        soapProcessor.addProperty("String_3", provinceField.getString());

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("T\u00ecm KV\u01afT b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getObjectInfo() {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u00e1c \u0111\u1ed1i t\u01b0\u1ee3ng",
                this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getObjectInfo",
                EnrollMIDlet.CHARSET);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin \u0111\u1ed1i t\u01b0\u1ee3ng b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getStageInfo() {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u00e1c \u0111\u1ee3t thi",
                this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getStageInfo",
                EnrollMIDlet.CHARSET);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin \u0111\u1ee3t thi b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getZoneInfo() {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u00e1c khu v\u1ef1c", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getZoneInfo",
                EnrollMIDlet.CHARSET);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin khu v\u1ef1c b\u1ecb l\u1ed7i. "
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
                if (soapProcessor.hasResponse("getObjectInfoResponse")) {
                    displayObjectInfo(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getStageInfoResponse")) {
                    displayStageInfo(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getZoneInfoResponse")) {
                    displayZoneInfo(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("findZoneResponse")) {
                    displayFindResult(soapProcessor
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