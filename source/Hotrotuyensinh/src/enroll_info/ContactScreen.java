//ContactScreen.java
// Copyright 2003 Le Ngoc Quoc Khanh.
//List display tasks for contact with system admin
package enroll_info;

import javax.microedition.io.Connector;
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
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import support.HttpPosterListener;
import support.MessageBox;
import support.ProgressIndicator;
import support.ProgressListener;
import support.SoapProcessor;
import support.VietSign;

//ContactScreen implements Runnable because it has a thread to send SMS
class ContactScreen extends List implements CommandListener, Runnable,
        HttpPosterListener, ProgressListener {

    // Main tasks
    private static final String[] contArray = { "Nh\u1eafn tin SMS",
            "G\u1edfi mail" };
    private static final Command backCommand = new Command("Tr\u1edf l\u1ea1i",
            Command.BACK, 1);
    private static final Command cancelCommand = new Command(
            "H\u1ee7y b\u1ecf", Command.CANCEL, 1);

    private String messageText;

    // Father MIDlet
    private final EnrollMIDlet midlet;

    private Form smsScreen = null;
    private Form mailScreen = null;
    // SMS message TextField
    private TextField messageField = null;
    // Mail Content TextField
    private TextField contentField = null;
    private TextField name1Field = null;
    private TextField name2Field = null;
    private TextField phoneField = null;
    private TextField emailField = null;
    private TextField fromField = null;

    private Command smsCommand = null;
    private Command mailCommand = null;
    private Command sendMailConfirm = null;

    boolean success;

    ContactScreen(EnrollMIDlet midlet) {
        super("Li\u00ean h\u1ec7", Choice.IMPLICIT, contArray, null);
        setTicker(new Ticker(
                "Li\u00ean h\u1ec7 v\u1edbi ng\u01b0\u1eddi c\u00f3 tr\u00e1ch nhi\u1ec7m"));
        this.midlet = midlet;
        addCommand(backCommand);

        smsCommand = new Command("G\u1edfi tin nh\u1eafn", Command.SCREEN, 1);
        mailCommand = new Command("G\u1edfi mail", Command.SCREEN, 1);

        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (d == this) {
            if (c == List.SELECT_COMMAND) {
                deInitialize(false);
                switch (((List) d).getSelectedIndex()) {
                case 0:
                    messageText = new String();
                    smsScreen = new Form("Nh\u1eafn tin SMS");
                    name1Field = new TextField("T\u00ean", null, 50,
                            TextField.ANY);
                    name1Field.addCommand(VietSign.signCommand);
                    name1Field.setItemCommandListener(midlet.vietSign);
                    emailField = new TextField("Email", null, 50,
                            TextField.EMAILADDR);
                    messageField = new TextField("Tin nh\u1eafn", null, 160,
                            TextField.ANY);
                    messageField.addCommand(VietSign.signCommand);
                    messageField.setItemCommandListener(midlet.vietSign);
                    smsScreen
                            .append(new StringItem(
                                    null,
                                    "Tin nh\u1eafn c\u1ee7a b\u1ea1n s\u1ebd \u0111\u01b0\u1ee3c g\u1edfi \u0111\u1ebfn t\u1ed5ng \u0111\u00e0i "
                                            + EnrollMIDlet.SMS_NUM));
                    smsScreen.append(name1Field);
                    smsScreen.append(emailField);
                    smsScreen.append(messageField);
                    smsScreen.addCommand(smsCommand);
                    smsScreen.addCommand(backCommand);
                    smsScreen.setCommandListener(this);
                    Display.getDisplay(midlet).setCurrent(smsScreen);
                    break;
                case 1:
                    mailScreen = new Form("G\u1edfi mail");
                    name2Field = new TextField("T\u00ean", null, 50,
                            TextField.ANY);
                    name2Field.addCommand(VietSign.signCommand);
                    name2Field.setItemCommandListener(midlet.vietSign);
                    phoneField = new TextField("\u0110i\u1ec7n tho\u1ea1i",
                            null, 15, TextField.PHONENUMBER);
                    fromField = new TextField("From email", null, 50,
                            TextField.EMAILADDR);
                    contentField = new TextField("N\u1ed9i dung", null, 250,
                            TextField.ANY);
                    contentField.addCommand(VietSign.signCommand);
                    contentField.setItemCommandListener(midlet.vietSign);
                    mailScreen
                            .append(new StringItem(
                                    null,
                                    "Mail c\u1ee7a b\u1ea1n s\u1ebd \u0111\u01b0\u1ee3c g\u1edfi \u0111\u1ebfn \u0111\u1ecba ch\u1ec9 "
                                            + EnrollMIDlet.CONTACT_MAIL));
                    mailScreen.append(name2Field);
                    mailScreen.append(fromField);
                    mailScreen.append(phoneField);
                    mailScreen.append(contentField);
                    mailScreen.addCommand(mailCommand);
                    mailScreen.addCommand(backCommand);
                    mailScreen.setCommandListener(this);
                    Display.getDisplay(midlet).setCurrent(mailScreen);
                    break;
                }
            } else if (c == backCommand) {
                deInitialize(false);
                Display.getDisplay(midlet).setCurrent(midlet.getMainScreen());
            }
        } else if (c == smsCommand) {
            if (name1Field.getString().equals("")
                    || messageField.getString().equals("")) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i nh\u1eadp \u0111\u1ea7y \u0111\u1ee7 t\u00ean, tin nh\u1eafn",
                                smsScreen);
            } else {
                messageText = messageField.getString();
                midlet.progressIndicator = new ProgressIndicator(
                        "Tin nh\u1eafn \u0111ang \u0111\u01b0\u1ee3c g\u1edfi",
                        this);
                midlet.progressIndicator.startActivityIndicator();
                Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);
                Thread thread = new Thread(this);
                thread.start();
            }
        } else if (c == mailCommand) {
            if (name2Field.getString().equals("")
                    || fromField.getString().equals("")
                    || contentField.getString().equals("")) {
                midlet
                        .setError(
                                "B\u1ea1n ph\u1ea3i nh\u1eadp \u0111\u1ea7y \u0111\u1ee7 t\u00ean, email, n\u1ed9i dung",
                                mailScreen);
            } else {
                sendMailConfirm = new Command("\u0110\u1ed3ng \u00fd",
                        Command.OK, 0);
                midlet.messageBox = new MessageBox(EnrollMIDlet.NOTES,
                        sendMailConfirm, cancelCommand, this);
                Display.getDisplay(midlet).setCurrent(midlet.messageBox);
            }
        } else if (c == sendMailConfirm) {
            sendMail();
        } else if (c == backCommand || c == cancelCommand) {
            deInitialize(false);
            Display.getDisplay(midlet).setCurrent(this);
        }
    }

    void deInitialize(boolean all) {
        smsScreen = null;
        name1Field = null;
        emailField = null;
        messageField = null;
        messageText = null;

        mailScreen = null;
        name2Field = null;
        phoneField = null;
        fromField = null;
        contentField = null;

        sendMailConfirm = null;

        midlet.progressIndicator = null;
    }

    public void handleHttpPosterError(String error) {
        midlet.setError("G\u1edfi mail b\u1ecb l\u1ed7i. " + error, this);
    }

    // Receive response from server
    public void receiveHttpResponse(byte[] response) {
        midlet.progressIndicator.stopActivityIndicator();

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
                if (soapProcessor.hasResponse("sendMailResponse")) {
                    String result = (String) (soapProcessor.getResult());
                    midlet.setStatus(result, this);
                } else {
                    midlet.setError("L\u1ed7i Response ko h\u1ee3p l\u1ec7",
                            this);
                }
            } catch (Exception e) {
                midlet.setError(
                        "L\u1ed7i kh\u00f4ng h\u1ed7 tr\u1ee3 Encoding", this);
            }
        } else {
            midlet.setError(soapProcessor.getError(), this);
        }
        soapProcessor = null;
    }

    // Thread to send SMS for system consistence
    public void run() {
        success = true;
        MessageConnection conn = null;
        String url = "sms://+" + EnrollMIDlet.SMS_NUM;

        try {
            conn = (MessageConnection) Connector.open(url);
            TextMessage msg = (TextMessage) conn
                    .newMessage(MessageConnection.TEXT_MESSAGE);
            msg.setPayloadText(name1Field.getString() + ":"
                    + emailField.getString() + " sent: " + messageText);
            conn.send(msg);
        } catch (Exception e) {
            success = false;
            midlet.setError("G\u1edfi tin nh\u1eafn b\u1ecb l\u1ed7i. "
                    + e.getMessage(), this);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                }
                if (success)
                    midlet
                            .setStatus(
                                    "Tin nh\u1eafn \u0111\u00e3 \u0111\u01b0\u1ee3c g\u1edfi",
                                    this);
            }
        }
    }

    // Send sendMail request to server
    void sendMail() {
        SoapProcessor soapProcessor = new SoapProcessor(
                EnrollMIDlet.METHOD_NAME_SPACE, "sendMail",
                EnrollMIDlet.CHARSET);

        soapProcessor.addProperty("String_1", name2Field.getString());
        soapProcessor.addProperty("String_2", fromField.getString());
        soapProcessor.addProperty("String_3", EnrollMIDlet.CONTACT_MAIL);
        soapProcessor.addProperty("String_4", EnrollMIDlet.MAIL_HOST);
        soapProcessor.addProperty("String_5", phoneField.getString());
        soapProcessor.addProperty("String_6", contentField.getString());

        soapProcessor.sendHttpRequest(midlet.getHttpPoster(), this,
                EnrollMIDlet.CONTENT_TYPE);
        midlet.progressIndicator = new ProgressIndicator(
                "Mail \u0111ang \u0111\u01b0\u1ee3c g\u1edfi", this);
        midlet.progressIndicator.startActivityIndicator();
        Display.getDisplay(midlet).setCurrent(midlet.progressIndicator);

        if (!soapProcessor.isSuccess()) {
            midlet.setError("G\u1edfi mail b\u1ecb l\u1ed7i. "
                    + soapProcessor.getError(), this);
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