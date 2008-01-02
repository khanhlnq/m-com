//ProgressIndicator.java
//Lop nay da duoc xay dung lai hoan toan, 
//     voi y tuong tu source code cua Nokia Forum
//Tuy nhien de dam bao nguon goc tham khao, van giu ban quyen cua Nokia

// Copyright 2003 Nokia Corporation.
//
// THIS SOURCE CODE IS PROVIDED 'AS IS', WITH NO WARRANTIES WHATSOEVER,
// EXPRESS OR IMPLIED, INCLUDING ANY WARRANTY OF MERCHANTABILITY, FITNESS
// FOR ANY PARTICULAR PURPOSE, OR ARISING FROM A COURSE OF DEALING, USAGE
// OR TRADE PRACTICE, RELATING TO THE SOURCE CODE OR ANY WARRANTY OTHERWISE
// ARISING OUT OF ANY PROPOSAL, SPECIFICATION, OR SAMPLE AND WITH NO
// OBLIGATION OF NOKIA TO PROVIDE THE LICENSEE WITH ANY MAINTENANCE OR
// SUPPORT. FURTHERMORE, NOKIA MAKES NO WARRANTY THAT EXERCISE OF THE
// RIGHTS GRANTED HEREUNDER DOES NOT INFRINGE OR MAY NOT CAUSE INFRINGEMENT
// OF ANY PATENT OR OTHER INTELLECTUAL PROPERTY RIGHTS OWNED OR CONTROLLED
// BY THIRD PARTIES
//
// Furthermore, information provided in this source code is preliminary,
// and may be changed substantially prior to final release. Nokia Corporation
// retains the right to make changes to this source code at
// any time, without notice. This source code is provided for informational
// purposes only.
//
// Nokia and Nokia Connecting People are registered trademarks of Nokia
// Corporation.
// Java and all Java-based marks are trademarks or registered trademarks of
// Sun Microsystems, Inc.
// Other product and company names mentioned herein may be trademarks or
// trade names of their respective owners.
//
// A non-exclusive, non-transferable, worldwide, limited license is hereby
// granted to the Licensee to download, print, reproduce and modify the
// source code. The licensee has the right to market, sell, distribute and
// make available the source code in original or modified form only when
// incorporated into the programs developed by the Licensee. No other
// license, express or implied, by estoppel or otherwise, to any other
// intellectual property rights is granted herein.
package support;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;

public class ProgressIndicator extends Form implements Runnable,
        CommandListener {
    private final static int TICK_TIME_MILLIS = 300;

    private static final Command cancelCommand = new Command("Ng\u01b0ng",
            Command.STOP, 2);
    ProgressListener progressListener;

    private final ImageItem activityIndicator;
    private Image active0 = null;
    private Image active1 = null;

    private volatile boolean aborting = false;
    private volatile boolean doAnimation = false;

    private Thread animationThread = null;
    private int progressCount = 0;

    public ProgressIndicator(String title, ProgressListener pL) {
        super("Tr\u1ea1ng th\u00e1i");
        progressListener = pL;

        // Initialize progress meter
        try {
            active0 = Image.createImage("/active0.png");
            active1 = Image.createImage("/active1.png");
        } catch (Exception e) {

        }

        int anchor = (ImageItem.LAYOUT_LEFT | ImageItem.LAYOUT_NEWLINE_AFTER);
        activityIndicator = new ImageItem(title, active0, anchor, "");
        append(new StringItem(null, "Xin vui l\u00f2ng ch\u1edd..."));

        setCommandListener(this);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == cancelCommand) {
            stopActivityIndicator();
            progressListener.stopProgress();
            deInitialize(false);
        }
    }

    void deInitialize(boolean all) {
        active0 = null;
        active1 = null;

        animationThread = null;
    }

    public void run() {
        while ((Thread.currentThread() == animationThread) && !aborting) {
            if (doAnimation) {
                Image image = ((progressCount % 2) == 0) ? active0 : active1;

                activityIndicator.setImage(image);
                progressCount++;
            }

            try {
                synchronized (this) {
                    wait(TICK_TIME_MILLIS);
                }
            } catch (InterruptedException e) {
                // continue running
            }
        }
    }

    public void startActivityIndicator() {
        addCommand(cancelCommand);

        insert(0, activityIndicator);

        // start animation
        aborting = false;
        doAnimation = true;
        animationThread = new Thread(this);
        animationThread.start();
    }

    public void stopActivityIndicator() {
        // stop animation
        aborting = true;
        doAnimation = false;

        if (size() == 3) {
            // remove indicator from top of Form
            delete(0);
        }

        removeCommand(cancelCommand);
    }
}