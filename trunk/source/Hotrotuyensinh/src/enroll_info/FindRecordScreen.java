//FindRecordScreen.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package enroll_info;

import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
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

class FindRecordScreen extends Form implements CommandListener,
        HttpPosterListener, ProgressListener {
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);
    private static final Command cancelCommand = new Command(
            "H\u1ee7y b\u1ecf", Command.CANCEL, 1);;
    private static final int MAX_COUNT = 5;

    EnrollMIDlet midlet;

    private Displayable previousScreen = null;
    private List listRecordScreen = null;
    private List nguyenvongScreen = null;
    private Form xepthisinhScreen = null;
    private Vector listRecord = null;
    private Vector listNguyenvong = null;
    private String currentRecord = null;

    private TextField tenField = null;
    private TextField hoField = null;
    private ChoiceGroup genderField = null;
    private DateField birthdayField = null;
    private TextField idField = null;
    private TextField districtField = null;
    private TextField provinceField = null;
    private TextField univField = null;
    private TextField branchField = null;

    private Command findRecordCommand = null;
    private Command findRecordConfirm = null;

    private int indexRecord = 0;

    FindRecordScreen(EnrollMIDlet midlet) {
        super("T\u00ecm h\u1ed3 s\u01a1");
        setTicker(new Ticker("T\u00ecm h\u1ed3 s\u01a1 th\u00ed sinh"));
        this.midlet = midlet;

        hoField = new TextField("H\u1ecd", null, 50, TextField.ANY);
        hoField.addCommand(VietSign.signCommand);
        hoField.setItemCommandListener(midlet.vietSign);
        tenField = new TextField("T\u00ean", null, 50, TextField.ANY);
        tenField.addCommand(VietSign.signCommand);
        tenField.setItemCommandListener(midlet.vietSign);
        genderField = new ChoiceGroup("Ph\u00e1i", List.EXCLUSIVE);
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

        findRecordCommand = new Command("T\u00ecm", Command.SCREEN, 0);

        append(hoField);
        append(tenField);
        append(genderField);
        append(birthdayField);
        append(univField);
        append(branchField);
        append(idField);
        append(districtField);
        append(provinceField);
        addCommand(findRecordCommand);
        addCommand(backCommand);
        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            int selectedIndex = ((List) d).getSelectedIndex();
            if (d == listRecordScreen) {
                // Return to MAX_COUNT univesities in the list
                if (selectedIndex == 0) {
                    if (indexRecord > 0) {
                        indexRecord--;
                        findRecord(hoField.getString(), tenField.getString(),
                                genderField.getSelectedIndex(), birthdayField
                                        .getDate(), idField.getString(),
                                districtField.getString(), provinceField
                                        .getString(), univField.getString(),
                                branchField.getString(), indexRecord, MAX_COUNT);
                    } else
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                        listRecordScreen);
                }
                // Next to MAX_COUNT univesities in the list
                else if (selectedIndex == listRecordScreen.size() - 1) {
                    if (listRecordScreen.size() < MAX_COUNT + 2) {
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                        listRecordScreen);
                    } else {
                        indexRecord++;
                        findRecord(hoField.getString(), tenField.getString(),
                                genderField.getSelectedIndex(), birthdayField
                                        .getDate(), idField.getString(),
                                districtField.getString(), provinceField
                                        .getString(), univField.getString(),
                                branchField.getString(), indexRecord, MAX_COUNT);
                    }
                }
                // Select one university in university list, display university
                // detail
                else {
                    nguyenvongScreen = null;
                    listNguyenvong = null;
                    currentRecord = (String) listRecord
                            .elementAt(selectedIndex - 1);
                    getNguyenvong(currentRecord);
                }
            } else if (d == nguyenvongScreen) {
                xepthisinhScreen = null;
                getXepthisinh(currentRecord, (String) listNguyenvong
                        .elementAt(selectedIndex));
            }
        }

        else if (c == findRecordCommand) {
            if (hoField.getString().equals("")
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
                findRecordConfirm = new Command("\u0110\u1ed3ng \u00fd",
                        Command.OK, 0);
                midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                        findRecordConfirm, cancelCommand, this);
                Display.getDisplay(midlet).setCurrent(midlet.messageBox);
            }
        } else if (c == findRecordConfirm) {
            deInitialize(false);
            findRecord(hoField.getString(), tenField.getString(), genderField
                    .getSelectedIndex(), birthdayField.getDate(), idField
                    .getString(), districtField.getString(), provinceField
                    .getString(), univField.getString(), branchField
                    .getString(), indexRecord, MAX_COUNT);
        }

        else if (c == cancelCommand)
            Display.getDisplay(midlet).setCurrent(previousScreen);
        else if (c == backCommand) {
            if (d == this) {
                deInitialize(true);
                Display.getDisplay(midlet).setCurrent(midlet.getMainScreen());
            } else if (d == listRecordScreen) {
                deInitialize(false);
                Display.getDisplay(midlet).setCurrent(this);
            } else if (d == nguyenvongScreen) {
                Display.getDisplay(midlet).setCurrent(listRecordScreen);
            } else if (d == xepthisinhScreen) {
                Display.getDisplay(midlet).setCurrent(nguyenvongScreen);
            }
        }
    }

    private void deInitialize(boolean all) {
        listRecordScreen = null;
        nguyenvongScreen = null;
        xepthisinhScreen = null;
        listRecord = null;
        listNguyenvong = null;
        previousScreen = null;
        findRecordConfirm = null;
        indexRecord = 0;
        currentRecord = null;
        midlet.messageBox = null;
        midlet.progressIndicator = null;
    }

    void displayNguyenvong(Vector result) {
        nguyenvongScreen = null;
        listNguyenvong = null;
        nguyenvongScreen = new List("Nguy\u1ec7n v\u1ecdng", List.IMPLICIT);
        nguyenvongScreen
                .setTicker(new Ticker(
                        "Ch\u1ecdn nguy\u1ec7n v\u1ecdng \u0111\u1ec3 xem th\u00f4ng tin thi"));
        listNguyenvong = new Vector();

        int i = 0;
        while (i < result.size()) {
            StringBuffer sb = new StringBuffer();
            listNguyenvong.addElement((String) result.elementAt(i));
            sb.append("Nguy\u1ec7n v\u1ecdng " + (String) result.elementAt(i++)
                    + "> ");
            sb.append("\nTr\u01b0\u1eddng: " + (String) result.elementAt(i++));
            sb.append("\nM\u00e3 tr\u01b0\u1eddng: "
                    + (String) result.elementAt(i++));
            sb.append("\nNg\u00e0nh: " + (String) result.elementAt(i++));
            sb
                    .append("\nM\u00e3 ng\u00e0nh: "
                            + (String) result.elementAt(i++));
            sb.append("\nKh\u1ed1i: " + (String) result.elementAt(i++));
            nguyenvongScreen.append(sb.toString(), null);
        }
        nguyenvongScreen.addCommand(backCommand);
        nguyenvongScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(nguyenvongScreen);
    }

    void displayRecordList(Vector result) {
        if (result != null && result.size() > 1) {
            listRecord = null;
            listRecord = new Vector();
            listRecordScreen = null;
            listRecordScreen = new List("Xem t\u1eeb h\u1ed3 s\u01a1 th\u1ee9 "
                    + (indexRecord * MAX_COUNT + 1), List.IMPLICIT);
            Ticker ticker = new Ticker("T\u00ecm \u0111\u01b0\u1ee3c "
                    + (String) result.elementAt(0) + " h\u1ed3 s\u01a1");
            listRecordScreen.setTicker(ticker);
            listRecordScreen.addCommand(backCommand);
            listRecordScreen.setCommandListener(this);
            if (indexRecord == 0) {
                listRecordScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                null);
            } else {
                listRecordScreen.append("\u25b2 Xem " + MAX_COUNT
                        + " h\u1ed3 s\u01a1 ph\u00eda tr\u01b0\u1edbc", null);
            }

            int i = 1;
            while (i < result.size()) {
                // M\u00e3 h\u1ed3 s\u01a1
                listRecord.addElement((String) result.elementAt(i));
                StringBuffer recordSB = new StringBuffer();
                recordSB.append("#M\u00e3 h\u1ed3 s\u01a1:"
                        + (String) result.elementAt(i++));
                recordSB.append("\nH\u1ecd t\u00ean:"
                        + (String) result.elementAt(i++));
                recordSB
                        .append("\nPh\u00e1i:"
                                + ((((String) result.elementAt(i++))
                                        .equals("0")) ? "Nam" : "N\u1eef"));
                recordSB.append("\nNg\u00e0y sinh:"
                        + (String) result.elementAt(i++));
                recordSB.append("\nCMND:" + (String) result.elementAt(i++));
                recordSB.append("\nSBD:" + (String) result.elementAt(i++));
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

                listRecordScreen.append(recordSB.toString(), null);
            }
            if (result.size() < (MAX_COUNT * 11 + 1)) {
                listRecordScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                null);
            } else {
                listRecordScreen.append("\u25bc Xem " + MAX_COUNT
                        + " h\u1ed3 s\u01a1 ti\u1ebfp theo", null);
            }
            Display.getDisplay(midlet).setCurrent(listRecordScreen);
        } else {
            if (indexRecord == 0)
                midlet.setStatus(
                        "Kh\u00f4ng t\u00ecm th\u1ea5y h\u1ed3 s\u01a1", this);
            else {
                midlet
                        .setStatus(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                listRecordScreen);
                indexRecord--;
            }
        }
    }

    void displayXepthisinh(Vector result) {
        xepthisinhScreen = null;
        xepthisinhScreen = new Form("Th\u00f4ng tin thi c\u1eed");
        xepthisinhScreen.setTicker(new Ticker("Th\u00f4ng tin thi c\u1eed"));

        int i = 0;
        while (i < result.size()) {
            StringBuffer sb = new StringBuffer();
            sb.append("SBD: " + (String) result.elementAt(i++));
            sb.append("\nPh\u00f2ng thi: " + (String) result.elementAt(i++));
            sb.append("\n\u0110\u1ecba \u0111i\u1ec3m thi: "
                    + (String) result.elementAt(i++));
            sb.append("\n\u0110\u1ecba ch\u1ec9: "
                    + (String) result.elementAt(i++));
            xepthisinhScreen.append(new StringItem(null, sb.toString()));
        }
        xepthisinhScreen.addCommand(backCommand);
        xepthisinhScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(xepthisinhScreen);
    }

    void findRecord(String ho, String t\u00ean, int gender, Date birthday,
            String id, String district, String province, String univ,
            String branch, int index, int maxCount) {

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
                "\u0110ang t\u00ecm h\u1ed3 s\u01a1", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "findRecord",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", ho);
        soapProcessor.addProperty("String_2", t\u00ean);
        soapProcessor.addProperty("int_3", new Integer(gender));
        soapProcessor.addProperty("String_4", birthdaySB.toString());
        soapProcessor.addProperty("String_5", id);
        soapProcessor.addProperty("String_6", district);
        soapProcessor.addProperty("String_7", province);
        soapProcessor.addProperty("String_8", univ);
        soapProcessor.addProperty("String_9", branch);

        soapProcessor.addProperty("int_10", new Integer(index));
        soapProcessor.addProperty("int_11", new Integer(maxCount));

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("T\u00ecm h\u1ed3 s\u01a1 b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getNguyenvong(String record) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y danh s\u00e1ch nguy\u1ec7n v\u1ecdng", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getNguyenvong",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", record);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("L\u1ea5y nguy\u1ec7n v\u1ecdng b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getXepthisinh(String mahoso, String nguyenvong) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin ph\u00f2ng thi", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getXepthisinh",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", mahoso);
        soapProcessor.addProperty("String_2", nguyenvong);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin ph\u00f2ng thi b\u1ecb l\u1ed7i. "
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
                if (soapProcessor.hasResponse("findRecordResponse")) {
                    displayRecordList(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getNguyenvongResponse")) {
                    displayNguyenvong(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getXepthisinhResponse")) {
                    displayXepthisinh(soapProcessor
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