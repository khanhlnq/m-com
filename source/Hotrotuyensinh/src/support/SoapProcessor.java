//SoapProcessor.java
// Copyright 2003 Le Ngoc Quoc Khanh.
package support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Vector;

import org.ksoap.SoapEnvelope;
import org.ksoap.SoapFault;
import org.ksoap.SoapObject;
import org.kxml.io.XmlWriter;
import org.kxml.wap.WbxmlParser;
import org.kxml.wap.WbxmlWriter;

public class SoapProcessor {

    // Table for the tags in our DTD

    static final String tagTable[] = { "SOAP-ENV:Envelope", "SOAP-ENV:Body",
            "SOAP-ENC", "result", "soapenv", "val", "src", "dst", "ExecTime",
            "xsd:anyType", "SOAP-ENC:arrayType", "SOAP-ENC:Array",
            "SOAP-ENC:position", "item", "String_1", "String_2", "String_3",
            "String_4", "String_5", "String_6", "String_7", "String_8",
            "String_9", "String_10", "String_11", "int_1", "int_2", "int_3",
            "int_4", "int_5", "int_10", "int_11", "int_12", "int_13", };

    // Table for the attribute names in our DTD

    private static final String attrStartTable[] = { "Envelope", "Body",
            "xmlns", "xmlns:xsd", "xmlns:xsi", "xsi:type", "xsd", "xsi", "ENC",
            "encodingStyle", "id", "root", "type", "xmlns:SOAP-ENC",
            "xmlns:SOAP-ENV", "SOAP-ENV:encodingStyle", "SOAP-ENC:root",
            "SOAP-ENC:arrayType", "SOAP-ENC:Array", "SOAP-ENC:position",
            "xsd:anyType" };

    // Table for the attribute values in our DTD

    private static final String attrValueTable[] = { "o0", "double", "String",
            "string", "int", "boolean", "long", "float", "dateTime",
            "xsd:anyType", "xsd:double", "xsd:String", "xsd:string", "xsd:int",
            "xsd:boolean", "xsd:long", "xsd:float", "xsd:dateTime",
            "http://www.w3.org/2001/XMLSchema-instance",
            "http://www.w3.org/2001/XMLSchema",
            "http://schemas.xmlsoap.org/soap/encoding/",
            "http://schemas.xmlsoap.org/soap/envelope/",
            "http://schemas.xmlsoap.org/soap/encoding/",
            "http://com.test/wsdl/EnrollService",
            "http://enroll.test/wsdl/EnrollService" };

    public static final String WBXML_CHARSET = "iso-8859-1";

    private String charSet = null;
    private SoapObject soapMethod = null;
    private SoapEnvelope soapEnvelope;
    private String responseStr = null;
    private boolean success = true;
    private String errorStr = null;
    private byte[] bytes;

    public SoapProcessor(byte[] response, String charSet) {
        this.charSet = charSet;

        try {
            soapEnvelope = new SoapEnvelope();
            ByteArrayInputStream bis = new ByteArrayInputStream(response);
            WbxmlParser parser = new WbxmlParser(bis);
            parser.setTagTable(0, tagTable);
            parser.setAttrStartTable(0, attrStartTable);
            parser.setAttrValueTable(0, attrValueTable);

            soapEnvelope.parse(parser);
        } catch (Exception e) {
            errorStr = "L\u1ed7i bi\u00ean d\u1ecbch Response: "
                    + e.getMessage();
            success = false;
        }

        if (success) {
            Object body = soapEnvelope.getBody();
            if (body instanceof SoapFault) {
                SoapFault fault = (SoapFault) body;

                // The SoapFault fault string may be verbose.
                // It contains the 'TranslationService error' message.
                // midlet.translatorScreenError(fault.toString());
                errorStr = "L\u1ed7i SOAP Fault. " + fault.faultstring;
                success = false;
            } else {
                // (Note: You normally parse the XML formatted response here
                // as needed: either the 'Document/Literal' or 'RPC/Encoded
                // SOAP response depending on your web service.
                // The simpler approach below was used because
                // TranslationService's response is so simple,
                // something like this:
                // <env:Envelope ... ><env:Body>
                // <ans1:getTranslationResponse
                // xmlns:ans1="http://foo.bar/wsd/translator/1.0/wsdl/TranslatorService">
                // <result xsi:type="xsd:string">resultStringGoesHere</result>
                // </ans1:getTranslationResponse>
                // </env:Body></env:Envelope>
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    XmlWriter xw = new XmlWriter(new OutputStreamWriter(bos,
                            charSet));
                    // Use default mapping between Java objects and Soap
                    // elements
                    soapEnvelope.write(xw);
                    xw.flush();
                    bytes = bos.toByteArray();
                    responseStr = new String(bytes, charSet);
                } catch (Exception e) {
                    errorStr = "L\u1ed7i XMLWriter: " + e.getMessage();
                    success = false;
                }
            }
        }
    }

    public SoapProcessor(SoapObject soapMethod, String charSet) {
        this.charSet = charSet;

        // Assemble "method" into an enveloped SOAP message
        // and then export to a String
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            WbxmlWriter xw = new WbxmlWriter(bos);
            xw.setTagTable(0, tagTable);
            xw.setAttrStartTable(0, attrStartTable);
            xw.setAttrValueTable(0, attrValueTable);
            // xw.write(xmlProlog);
            // Use default mapping between Java objects and Soap elements
            soapEnvelope = new SoapEnvelope();
            soapEnvelope.setBody(soapMethod);
            soapEnvelope.write(xw);
            xw.close();
            bytes = bos.toByteArray();
        } catch (Exception e) {
            errorStr = "L\u1ed7i t\u1ea1o Request: " + e.getMessage();
            success = false;
        }
        if (success) {
            try {
                bos = new ByteArrayOutputStream();
                XmlWriter xw = new XmlWriter(new OutputStreamWriter(bos,
                        charSet));
                // Use default mapping between Java objects and Soap elements
                soapEnvelope.write(xw);
                xw.flush();
                byte[] bytes2 = bos.toByteArray();
                responseStr = new String(bytes2, charSet);
            } catch (Exception e) {
                errorStr = "L\u1ed7i XMLWriter2: " + e.getMessage();
                success = false;
            }
        }
    }

    public SoapProcessor(String namespace, String method, String charSet) {
        this.charSet = charSet;
        soapMethod = new SoapObject(namespace, method);
    }

    public void addProperty(String name, Object value) {
        if (soapMethod != null) {
            if (value instanceof String) {
                try {
                    soapMethod.addProperty(name, stringInvert((String) value));
                } catch (UnsupportedEncodingException e) {
                    success = false;
                    errorStr = "L\u1ed7i addProperty";
                }
            } else
                soapMethod.addProperty(name, value);
        } else {
            success = false;
            errorStr = "L\u1ed7i addProperty";
        }
    }

    void deIninitialize(boolean all) {
        charSet = null;
        soapMethod = null;
        soapEnvelope = null;
        responseStr = null;
        success = true;
        errorStr = null;
        bytes = null;
        ;
    }

    public byte[] getBytes() {
        return (bytes);
    }

    public SoapEnvelope getEnvelope() {
        return soapEnvelope;
    }

    public String getError() {
        return errorStr;
    }

    public String getResponseStr() {
        return responseStr;
    }

    public Object getResult() {
        return soapEnvelope.getResult();
    }

    public boolean hasResponse(String response) {
        return ((responseStr.indexOf(response)) != -1);
    }

    public boolean isSuccess() {
        return success;
    }

    public void sendHttpRequest(HttpPoster poster, HttpPosterListener listener,
            String contentType) {
        // Assemble "method" into an enveloped SOAP message
        // and then export to a String
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            WbxmlWriter xw = new WbxmlWriter(bos);
            xw.setTagTable(0, tagTable);
            xw.setAttrStartTable(0, attrStartTable);
            xw.setAttrValueTable(0, attrValueTable);
            // xw.write(xmlProlog);
            // Use default mapping between Java objects and Soap elements
            soapEnvelope = new SoapEnvelope();
            soapEnvelope.setBody(soapMethod);
            soapEnvelope.write(xw);
            xw.close();
            bytes = bos.toByteArray();
        } catch (Exception e) {
            errorStr = "L\u1ed7i t\u1ea1o Request: " + e.getMessage();
            success = false;
        }
        if (success) {
            try {
                bos = new ByteArrayOutputStream();
                XmlWriter xw = new XmlWriter(new OutputStreamWriter(bos,
                        charSet));
                // Use default mapping between Java objects and Soap elements
                soapEnvelope.write(xw);
                xw.flush();
                byte[] bytes2 = bos.toByteArray();
                responseStr = new String(bytes2, charSet);
            } catch (Exception e) {
                errorStr = "L\u1ed7i XMLWriter2: " + e.getMessage();
                success = false;
            }
            try {
                Hashtable httpRequestProperties = new Hashtable();
                httpRequestProperties.put("Content-Type", contentType);
                httpRequestProperties.put("Content-Length", Integer
                        .toString(bytes.length));
                // (Set SOAPAction here if needed.)
                System.out.println("\n# SOAP Send:\n" + new String(bytes));
                // System.out.println("\n# SOAP Send:\n" + responseStr);
                System.out.println("# Compressed size: " + bytes.length
                        + " Bytes");
                System.out.println("# Uncompressed size: "
                        + responseStr.getBytes().length + " Bytes");
                poster.sendHttpRequest(bytes, httpRequestProperties, listener);
            } catch (Exception e) {
                success = false;
                errorStr = "L\u1ed7i g\u1edfi Http Request";
            }
        }
        soapMethod = null;
    }

    // Convert String from BYTE_CHARSER charSet to charSet charSet
    public String stringConvert(String str) throws UnsupportedEncodingException {
        String cString = new String();
        cString = new String(bytes, charSet);
        byte[] bytes = str.getBytes(WBXML_CHARSET);
        cString = new String(bytes, charSet);
        return cString;
    }

    public String stringInvert(String str) throws UnsupportedEncodingException {
        String cString = new String();
        byte[] bytes = str.getBytes(charSet);
        cString = new String(bytes, WBXML_CHARSET);
        return cString;
    }

    // Convert Vector of Strings from BYTE_CHARSER charSet to charSet charSet
    public Vector stringVectorConvert(Vector vector)
            throws UnsupportedEncodingException {
        Vector cVector = vector;
        for (int i = 0; i < vector.size(); i++) {
            if (vector.elementAt(i) != null) {
                if (((String) vector.elementAt(i)).equals("null")) {
                    cVector.setElementAt("\u00d8", i);
                } else
                    cVector.setElementAt(stringConvert((String) vector
                            .elementAt(i)), i);
            } else
                cVector.setElementAt("\u00d8", i);
        }
        return cVector;
    }
}