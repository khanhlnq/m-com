//HttpPoster.java
//Ban quyen cua Nokia Forum
// Copyright 2002 Nokia Corporation. 
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

// This class accepts and queues POST requests for a particular URL, and
// services them in first-in-first-out order. Using the queue allows
// it to be thread-safe without forcing its clients ever to block.

public class HttpPoster implements Runnable {
    private final String url;
    private volatile boolean aborting = false;
    private Vector requestQueue = new Vector();
    private Vector httpRequestPropertiesQueue = new Vector();
    private Vector listenerQueue = new Vector();

    public HttpPoster(String url) {
        this.url = url;

        Thread thread = new Thread(this);
        thread.start();
    }

    // The instance is useless after abort has been called.
    public void abort() {
        aborting = true;
        requestQueue = null;
        httpRequestPropertiesQueue = null;
        listenerQueue = null;
        synchronized (this) {
            notify(); // wake up our posting thread and kill it
        }
    }

    private void doSend(Hashtable httpRequestProperties, byte[] bytes,
            HttpPosterListener listener) {
        HttpConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        String errorStr = null;
        boolean wasError = false;

        ByteArrayOutputStream responseBytes = new ByteArrayOutputStream();
        try {
            conn = (HttpConnection) Connector.open(url);

            // Set the request method
            conn.setRequestMethod(HttpConnection.POST);

            // Set the HTTP request header fields
            Enumeration enumeration = httpRequestProperties.keys();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = (String) httpRequestProperties.get(name);
                conn.setRequestProperty(name, value);
            }

            // Getting the output stream may flush the headers
            out = conn.openOutputStream();

            out.write(bytes);

            // Opening the InputStream will open the connection
            // and read the HTTP headers. They are stored
            // until requested.
            in = conn.openInputStream();

            // Get the length and process the data
            int ch;
            while ((ch = in.read()) != -1) {
                responseBytes.write(ch);
            }

            // If needed, one could add support for HTTP sessions
            // using URL re-writing here.
        } catch (IOException e) {
            wasError = true;

            errorStr = "IOException connecting to '" + url + "'";
            if (e.getMessage() != null) {
                errorStr += (": " + e.getMessage());
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // just ignore, there is nothing we can do
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // just ignore, there is nothing we can do
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    // just ignore, there is nothing we can do
                }
            }
        }

        if (wasError) {
            listener.handleHttpPosterError(errorStr);
        } else {
            listener.receiveHttpResponse(responseBytes.toByteArray());
        }
    }

    boolean isAborting() {
        return aborting;
    }

    public void run() {
        running: while (!aborting) {
            Hashtable httpRequestProperties;
            HttpPosterListener listener;

            byte[] bytes = null;
            synchronized (this) {
                while (requestQueue.size() == 0) {
                    try {
                        wait(); // releases lock
                    } catch (InterruptedException e) {
                    }

                    if (aborting) {
                        break running;
                    }
                }

                httpRequestProperties = (Hashtable) (httpRequestPropertiesQueue
                        .elementAt(0));
                bytes = (byte[]) requestQueue.elementAt(0);
                listener = (HttpPosterListener) (listenerQueue.elementAt(0));
                httpRequestPropertiesQueue.removeElementAt(0);
                requestQueue.removeElementAt(0);
                listenerQueue.removeElementAt(0);
            }

            // sendHttpRequest must have notified us
            doSend(httpRequestProperties, bytes, listener);
        }
    }

    synchronized public void sendHttpRequest(byte[] bytes,
            Hashtable httpRequestProperties, HttpPosterListener listener)
            throws IOException, UnsupportedEncodingException {
        if (aborting) {
            aborting = false;
            requestQueue = null;
            httpRequestPropertiesQueue = null;
            listenerQueue = null;
            requestQueue = new Vector();
            httpRequestPropertiesQueue = new Vector();
            listenerQueue = new Vector();
            Thread thread = new Thread(this);
            thread.start();
        }
        httpRequestPropertiesQueue.addElement(httpRequestProperties);

        requestQueue.addElement(bytes);

        listenerQueue.addElement(listener);

        notify(); // wake up sending thread
    }
}