//UnivList.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package enroll_info;

import java.util.Vector;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
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

class UnivList extends List implements CommandListener, HttpPosterListener,
        ProgressListener {
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);

    private static final Command cancelCommand = new Command(
            "H\u1ee7y b\u1ecf", Command.CANCEL, 1);
    private static final int MAX_COUNT = 10;;

    private static final String[] univStringArray = {
            "Xem danh s\u00e1ch tr\u01b0\u1eddng \u0111\u1ea7y \u0111\u1ee7",
            "T\u00ecm tr\u01b0\u1eddng" };

    private final EnrollMIDlet midlet;
    private Displayable previousScreen = null;
    private List listUnivScreen = null;
    private Form findUnivScreen = null;
    private Form univDetailScreen = null;

    private List branchListScreen = null;
    private Form branchDetailScreen = null;
    private Command getUnivListCommand = null;
    private TextField univCodeField = null;
    private TextField univNameField = null;
    private ChoiceGroup blockField = null;
    private TextField branchField = null;
    private Command getBlockConfirm = null;
    private Command findUnivCommand = null;

    private Command findUnivConfirm = null;
    private Command getBranchListCommand = null;
    private Vector listUniv = null;
    private Vector listBlock = null;
    private Vector listBranch = null;

    private Vector listBlockBranch = null;
    private String currentUniv = null;
    private int indexUniv = 0;

    private int indexBranch = 0;

    UnivList(EnrollMIDlet midl) {
        super("Th\u00f4ng tin tr\u01b0\u1eddng", List.IMPLICIT,
                univStringArray, null);
        setTicker(new Ticker("Th\u00f4ng tin c\u00e1c tr\u01b0\u1eddng"));
        this.midlet = midl;

        addCommand(backCommand);

        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == List.SELECT_COMMAND) {
            if (d == this) {
                deInitialize(false);
                switch (((List) d).getSelectedIndex()) {
                // Choose Xem danh s\u00e1ch \u0111\u1ea7y \u0111\u1ee7
                case 0:
                    previousScreen = this;
                    getUnivListCommand = new Command("\u0110\u1ed3ng \u00fd",
                            Command.OK, 0);
                    midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                            getUnivListCommand, cancelCommand, this);
                    Display.getDisplay(midlet).setCurrent(midlet.messageBox);
                    break;
                // Choose T\u00ecm tr\u01b0\u1eddng
                case 1:
                    if (listBlock == null) {
                        previousScreen = this;
                        ;
                        getBlockConfirm = new Command("\u0110\u1ed3ng \u00fd",
                                Command.OK, 0);
                        midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                                getBlockConfirm, cancelCommand, this);
                        Display.getDisplay(midlet)
                                .setCurrent(midlet.messageBox);
                    } else
                        displayFindScreen();
                    break;
                }
            } else if (d == listUnivScreen) {
                int selectedIndex = ((List) d).getSelectedIndex();
                // Return to MAX_COUNT univesities in the list
                if (selectedIndex == 0) {
                    if (indexUniv > 0) {
                        indexUniv--;
                        if (findUnivScreen == null)
                            getUnivList(indexUniv, MAX_COUNT);
                        else
                            findUniv(univCodeField.getString(), univNameField
                                    .getString(), (String) listBlock
                                    .elementAt(blockField.getSelectedIndex()),
                                    branchField.getString(), indexUniv,
                                    MAX_COUNT);
                    } else
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                        listUnivScreen);
                }
                // Next to MAX_COUNT univesities in the list
                else if (selectedIndex == listUnivScreen.size() - 1) {
                    if (listUnivScreen.size() < MAX_COUNT + 2) {
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                        listUnivScreen);
                    } else {
                        indexUniv++;
                        if (findUnivScreen == null)
                            getUnivList(indexUniv, MAX_COUNT);
                        else
                            findUniv(univCodeField.getString(), univNameField
                                    .getString(), (String) listBlock
                                    .elementAt(blockField.getSelectedIndex()),
                                    branchField.getString(), indexUniv,
                                    MAX_COUNT);
                    }
                }
                // Select one university in university list, display university
                // detail
                else {
                    univDetailScreen = null;
                    branchListScreen = null;
                    listBranch = null;
                    listBlockBranch = null;
                    branchDetailScreen = null;
                    indexBranch = 0;
                    currentUniv = (String) listUniv
                            .elementAt(selectedIndex - 1);
                    getUnivDetail(currentUniv);
                }
            }
            // Choose one branch in the branch list
            else if (d == branchListScreen) {
                int selectedIndex = ((List) d).getSelectedIndex();
                // Return to MAX_COUNT branches in the list
                if (selectedIndex == 0) {
                    if (indexBranch > 0) {
                        indexBranch--;
                        getBranchList(currentUniv, indexBranch, MAX_COUNT);
                    } else
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                        branchListScreen);
                }
                // Next to MAX_COUNT branches in the list
                else if (selectedIndex == branchListScreen.size() - 1) {
                    if (branchListScreen.size() < MAX_COUNT + 2) {
                        midlet
                                .setStatus(
                                        "B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                        branchListScreen);
                    } else {
                        indexBranch++;
                        getBranchList(currentUniv, indexBranch, MAX_COUNT);
                    }
                }
                // Select one branch in university list, display branch detail
                else {
                    branchDetailScreen = null;
                    getBranchDetail(currentUniv, (String) listBlockBranch
                            .elementAt(selectedIndex - 1), (String) listBranch
                            .elementAt(selectedIndex - 1));
                }
            }
        } else if (c == getUnivListCommand) {
            deInitialize(false);
            midlet.messageBox = null;
            getUnivList(indexUniv, MAX_COUNT);
        } else if (c == getBlockConfirm) {
            getBlockList();
        }

        else if (c == findUnivCommand) {
            previousScreen = findUnivScreen;
            if (univCodeField.getString().equals("")
                    && univNameField.getString().equals("")
                    && branchField.getString().equals("")
                    && (blockField.getSelectedIndex() == blockField.size() - 1)) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i cung c\u1ea5p \u00edt nh\u1ea5t m\u1ed9t d\u1eef ki\u1ec7n t\u00ecm kiem",
                                findUnivScreen);
            } else {
                findUnivConfirm = new Command("\u0110\u1ed3ng \u00fd",
                        Command.OK, 0);
                midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                        findUnivConfirm, cancelCommand, this);
                Display.getDisplay(midlet).setCurrent(midlet.messageBox);
            }
        } else if (c == findUnivConfirm) {
            midlet.messageBox = null;
            // kh\u00f4ng de Initialize do con l\u1ea5y du lieu TextField
            findUniv(
                    univCodeField.getString(),
                    univNameField.getString(),
                    (String) listBlock.elementAt(blockField.getSelectedIndex()),
                    branchField.getString(), indexUniv, MAX_COUNT);
        } else if (c == getBranchListCommand) {
            if (branchListScreen == null)
                getBranchList(currentUniv, indexBranch, MAX_COUNT);
            else
                Display.getDisplay(midlet).setCurrent(branchListScreen);
        } else if (c == cancelCommand) {
            Display.getDisplay(midlet).setCurrent(previousScreen);
            midlet.messageBox = null;
            previousScreen = null;
        } else if (c == backCommand) {
            Displayable currentScreen = Display.getDisplay(midlet).getCurrent();
            if (currentScreen == this)
                Display.getDisplay(midlet).setCurrent(midlet.getMainScreen());
            if (currentScreen == findUnivScreen
                    || currentScreen == listUnivScreen)
                Display.getDisplay(midlet).setCurrent(this);
            if (currentScreen == univDetailScreen)
                Display.getDisplay(midlet).setCurrent(listUnivScreen);
            if (currentScreen == branchListScreen)
                Display.getDisplay(midlet).setCurrent(univDetailScreen);
            if (currentScreen == branchDetailScreen)
                Display.getDisplay(midlet).setCurrent(branchListScreen);
            currentScreen = null;
        }
    }

    void deInitialize(boolean all) {
        previousScreen = null;
        listUniv = null;
        listBranch = null;
        listBlockBranch = null;
        currentUniv = null;
        listUnivScreen = null;
        indexUniv = 0;
        indexBranch = 0;
        findUnivScreen = null;
        univDetailScreen = null;
        branchListScreen = null;
        branchDetailScreen = null;
        univCodeField = null;
        univNameField = null;
        blockField = null;
        branchField = null;
        getBlockConfirm = null;
        findUnivCommand = null;
        getUnivListCommand = null;
        findUnivConfirm = null;
        getBranchListCommand = null;

        midlet.messageBox = null;
        midlet.progressIndicator = null;

        if (all) {
            listBlock = null;
        }
    }

    void displayBranchDetail(Vector result) {
        branchDetailScreen = null;
        branchDetailScreen = new Form("Chi ti\u1ebft c\u1ee7a ng\u00e0nh");
        int i = 0;
        branchDetailScreen.append(new StringItem("M\u00e3 ng\u00e0nh:",
                (String) result.elementAt(i++)));
        branchDetailScreen.append(new StringItem("T\u00ean ng\u00e0nh:",
                (String) result.elementAt(i++)));
        branchDetailScreen.append(new StringItem("Kh\u1ed1i:", (String) result
                .elementAt(i++)));
        branchDetailScreen.append(new StringItem("M\u00f4n thi 1:",
                (String) result.elementAt(i++) + " (h\u1ec7 s\u1ed1 "
                        + (String) result.elementAt(i++) + ")"));
        branchDetailScreen.append(new StringItem("M\u00f4n thi 2:",
                (String) result.elementAt(i++) + " (h\u1ec7 s\u1ed1 "
                        + (String) result.elementAt(i++) + ")"));
        branchDetailScreen.append(new StringItem("M\u00f4n thi 3:",
                (String) result.elementAt(i++) + " (h\u1ec7 s\u1ed1 "
                        + (String) result.elementAt(i++) + ")"));
        branchDetailScreen.append(new StringItem("M\u00f4n thi 4:",
                (String) result.elementAt(i++) + " (h\u1ec7 s\u1ed1 "
                        + (String) result.elementAt(i++) + ")"));
        branchDetailScreen.append(new StringItem("Ch\u1ec9 ti\u00eau:",
                (String) result.elementAt(i++)));
        branchDetailScreen.append(new StringItem("Ghi ch\u00fa:",
                (String) result.elementAt(i++)));
        branchDetailScreen.addCommand(backCommand);
        branchDetailScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(branchDetailScreen);
    }

    void displayBranchList(Vector result) {
        if (result != null && result.size() > 1) {
            branchListScreen = null;
            listBranch = null;
            listBranch = new Vector();
            listBlockBranch = null;
            listBlockBranch = new Vector();
            branchListScreen = new List("Xem t\u1eeb ng\u00e0nh thu "
                    + (indexBranch * MAX_COUNT + 1), List.IMPLICIT);
            Ticker ticker = new Ticker("C\u00f3 t\u1ea5t c\u1ea3 "
                    + (String) result.elementAt(0) + " ng\u00e0nh");
            branchListScreen.setTicker(ticker);
            int i = 1;
            int count = 0;
            StringBuffer sb;

            if (indexBranch == 0) {
                branchListScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                null);
            } else {
                branchListScreen.append("\u25b2 Xem " + MAX_COUNT
                        + " ng\u00e0nh ph\u00eda tr\u01b0\u1edbc", null);
            }

            while (i < result.size()) {
                count++;
                sb = new StringBuffer();
                sb.append("Ng\u00e0nh ");
                sb.append(result.elementAt(i++));
                sb.append("-kh\u1ed1i ");
                sb.append(result.elementAt(i));
                // M\u00e3 kh\u1ed1i
                listBlockBranch.addElement(result.elementAt(i++));
                // M\u00e3 ng\u00e0nh
                listBranch.addElement(result.elementAt(i++));

                branchListScreen.append(sb.toString(), null);
            }

            if (result.size() < (MAX_COUNT * 3 + 1)) {
                branchListScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                null);
            } else {
                branchListScreen.append("\u25bc Xem " + MAX_COUNT
                        + " ng\u00e0nh ti\u1ebfp theo", null);
            }

            branchListScreen.addCommand(backCommand);
            branchListScreen.setCommandListener(this);
            Display.getDisplay(midlet).setCurrent(branchListScreen);
        } else {
            midlet
                    .setStatus(
                            "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                            branchListScreen);
            indexBranch--;
        }
    }

    void displayFindResult(Vector result) {
        if (result != null && result.size() > 1) {
            listUniv = null;
            listUniv = new Vector();
            listUnivScreen = null;
            listUnivScreen = new List("Xem t\u1eeb tr\u01b0\u1eddng thu "
                    + (indexUniv * MAX_COUNT + 1), List.IMPLICIT);
            StringBuffer tickerSB = new StringBuffer();
            tickerSB.append("T\u00ecm \u0111\u01b0\u1ee3c "
                    + (String) result.elementAt(0) + " tr\u01b0\u1eddng theo");
            if (!univCodeField.getString().equals(""))
                tickerSB.append(" ma tr\u01b0\u1eddng: "
                        + univCodeField.getString());
            if (!univNameField.getString().equals(""))
                tickerSB.append(" t\u00ean tr\u01b0\u1eddng: "
                        + univNameField.getString());
            tickerSB.append(" kh\u1ed1i: "
                    + (String) listBlock.elementAt(blockField
                            .getSelectedIndex()));
            if (!branchField.getString().equals(""))
                tickerSB.append(" ng\u00e0nh: " + branchField.getString());
            Ticker ticker = new Ticker(tickerSB.toString());
            listUnivScreen.setTicker(ticker);
            listUnivScreen.addCommand(backCommand);
            listUnivScreen.setCommandListener(this);
            if (indexUniv == 0) {
                listUnivScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                null);
            } else {
                listUnivScreen.append("\u25b2 Xem " + MAX_COUNT
                        + " tr\u01b0\u1eddng ph\u00eda tr\u01b0\u1edbc", null);
            }
            for (int i = 1; i < result.size();) {
                listUniv.addElement((String) result.elementAt(i++));
                listUnivScreen.append((String) result.elementAt(i++), null);
            }
            if (result.size() < (MAX_COUNT * 2 + 1)) {
                listUnivScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                null);
            } else {
                listUnivScreen.append("\u25bc Xem " + MAX_COUNT
                        + " tr\u01b0\u1eddng ti\u1ebfp theo", null);
            }
            Display.getDisplay(midlet).setCurrent(listUnivScreen);
        } else {
            if (indexUniv == 0)
                midlet
                        .setStatus(
                                "Kh\u00f4ng t\u00ecm th\u1ea5y tr\u01b0\u1eddng ph\u00f9 h\u1ee3p",
                                findUnivScreen);
            else {
                midlet
                        .setStatus(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                listUnivScreen);
                indexUniv--;
            }
        }
    }

    void displayFindScreen() {
        findUnivScreen = new Form("T\u00ecm tr\u01b0\u1eddng");
        findUnivScreen.setTicker(new Ticker(
                "T\u00ecm tr\u01b0\u1eddng theo d\u1eef ki\u1ec7n"));
        findUnivCommand = new Command("T\u00ecm tr\u01b0\u1eddng",
                Command.SCREEN, 0);
        univCodeField = new TextField("M\u00e3 tr\u01b0\u1eddng", null, 10,
                TextField.ANY);
        univNameField = new TextField("T\u00ean tr\u01b0\u1eddng", null, 50,
                TextField.ANY);
        univNameField.addCommand(VietSign.signCommand);
        univNameField.setItemCommandListener(midlet.vietSign);
        blockField = new ChoiceGroup("Kh\u1ed1i", Choice.POPUP);
        for (int i = 0; i < listBlock.size(); i++)
            blockField.append((String) listBlock.elementAt(i), null);
        blockField.setSelectedIndex(0, true);
        branchField = new TextField("T\u00ean ng\u00e0nh", null, 50,
                TextField.ANY);
        branchField.addCommand(VietSign.signCommand);
        branchField.setItemCommandListener(midlet.vietSign);
        findUnivScreen.append(univCodeField);
        findUnivScreen.append(univNameField);
        findUnivScreen.append(blockField);
        findUnivScreen.append(branchField);
        findUnivScreen.addCommand(backCommand);
        findUnivScreen.addCommand(findUnivCommand);
        findUnivScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(findUnivScreen);
    }

    void displayUnivDetail(Vector result) {
        univDetailScreen = null;
        univDetailScreen = new Form("Chi ti\u1ebft c\u1ee7a tr\u01b0\u1eddng");
        getBranchListCommand = new Command("Ng\u00e0nh tuy\u1ec3n",
                Command.SCREEN, 1);
        int i = 0;
        univDetailScreen.append(new StringItem("M\u00e3 tr\u01b0\u1eddng:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("T\u00ean tr\u01b0\u1eddng:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("\u0110\u1ecba ch\u1ec9:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("\u0110i\u1ec7n tho\u1ea1i:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("T\u1ed5ng ch\u1ec9 ti\u00eau:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("Vung tuy\u1ec3n:",
                (String) result.elementAt(i++)));
        univDetailScreen.append(new StringItem("Ghi ch\u00fa:", (String) result
                .elementAt(i++)));
        univDetailScreen.addCommand(backCommand);
        univDetailScreen.addCommand(getBranchListCommand);
        univDetailScreen.setCommandListener(this);
        Display.getDisplay(midlet).setCurrent(univDetailScreen);
    }

    void displayUnivList(Vector result) {
        if (result != null && result.size() > 1) {
            listUniv = null;
            listUniv = new Vector();
            listUnivScreen = null;
            listUnivScreen = new List("Xem t\u1eeb tr\u01b0\u1eddng thu "
                    + (indexUniv * MAX_COUNT + 1), List.IMPLICIT);
            Ticker ticker = new Ticker("Danh s\u00e1ch c\u00f3 "
                    + (String) result.elementAt(0) + " tr\u01b0\u1eddng");
            listUnivScreen.setTicker(ticker);
            listUnivScreen.addCommand(backCommand);
            listUnivScreen.setCommandListener(this);
            if (indexUniv == 0) {
                listUnivScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf \u0111\u1ea7u danh s\u00e1ch",
                                null);
            } else {
                listUnivScreen.append("\u25b2 Xem " + MAX_COUNT
                        + " tr\u01b0\u1eddng ph\u00eda tr\u01b0\u1edbc", null);
            }
            for (int i = 1; i < result.size();) {
                listUniv.addElement((String) result.elementAt(i++));
                listUnivScreen.append((String) result.elementAt(i++), null);
            }
            if (result.size() < (MAX_COUNT * 2 + 1)) {
                listUnivScreen
                        .append(
                                "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                                null);
            } else {
                listUnivScreen.append("\u25bc Xem " + MAX_COUNT
                        + " tr\u01b0\u1eddng ti\u1ebfp theo", null);
            }
            Display.getDisplay(midlet).setCurrent(listUnivScreen);
        } else {
            midlet
                    .setStatus(
                            "\u25a0 B\u1ea1n \u0111ang \u1edf cu\u1ed1i danh s\u00e1ch",
                            listUnivScreen);
            indexUniv--;
        }
    }

    void findUniv(String univCode, String univName, String block,
            String branch, int index, int maxCount) {
        if (block.equals(listBlock.lastElement()))
            block = new String("");

        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang t\u00ecm tr\u01b0\u1eddng", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "findUniv",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", univCode);
        soapProcessor.addProperty("String_2", univName);
        soapProcessor.addProperty("String_3", block);
        soapProcessor.addProperty("String_4", branch);
        soapProcessor.addProperty("int_5", new Integer(index));
        soapProcessor.addProperty("int_6", new Integer(maxCount));

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("T\u00ecm tr\u01b0\u1eddng b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getBlockList() {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y danh s\u00e1ch c\u00e1c kh\u1ed1i", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getBlockList",
                EnrollMIDlet.CHARSET);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y danh s\u00e1ch kh\u1ed1i b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getBranchDetail(String univCode, String blockCode, String branchCode) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u1ee7a ng\u00e0nh", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getBranchDetail",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", univCode);
        soapProcessor.addProperty("String_2", blockCode);
        soapProcessor.addProperty("String_3", branchCode);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin ng\u00e0nh b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getBranchList(String univCode, int indexB, int maxCount) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u00e1c ng\u00e0nh", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getBranchList",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", univCode);
        soapProcessor.addProperty("int_2", new Integer(indexB));
        soapProcessor.addProperty("int_3", new Integer(maxCount));

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin ng\u00e0nh b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getUnivDetail(String univCode) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y th\u00f4ng tin c\u1ee7a tr\u01b0\u1eddng",
                this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getUnivDetail",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", univCode);

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError(
                    "L\u1ea5y th\u00f4ng tin tr\u01b0\u1eddng b\u1ecb l\u1ed7i. "
                            + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    void getUnivList(int index, int maxCount) {
        midlet.progressIndicator = new ProgressIndicator(
                "\u0110ang l\u1ea5y danh s\u00e1ch", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "getUnivList",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("int_1", new Integer(index));
        soapProcessor.addProperty("int_2", new Integer(maxCount));

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("L\u1ea5y danh s\u00e1ch b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
        }

        soapProcessor = null;
    }

    public void handleHttpPosterError(String error) {
        deInitialize(false);
        // System.out.println(error);
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
                if (soapProcessor.hasResponse("getUnivListResponse")) {
                    displayUnivList(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getBlockListResponse")) {
                    listBlock = soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult()));
                    listBlock.addElement(new String("Tat ca"));
                    displayFindScreen();
                } else if (soapProcessor.hasResponse("findUnivResponse")) {
                    displayFindResult(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getUnivDetailResponse")) {
                    displayUnivDetail(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getBranchListResponse")) {
                    displayBranchList(soapProcessor
                            .stringVectorConvert((Vector) (soapProcessor
                                    .getResult())));
                } else if (soapProcessor.hasResponse("getBranchDetailResponse")) {
                    displayBranchDetail(soapProcessor
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