// EnrollProxyServlet.java
// Copyright 2003 Le Ngoc Quoc Khanh.
// Proxy Servlet, co chuc nang moi gioi giua client di dong va JAX-RPC Server
// Client di dong giao tiep voi proxy bang WBXML, proxy se bien dich
// WBXML thanh tai lieu XML van ban de goi den JAX-RPC server
// sau do nhan response tra ve la van ban XML, proxy servlet se nen
// tai lieu XML thanh dang nhi phan WBXML de tra ve cho client di dong
// Viec nay se tiet kiem bang thong dang ke cho client di dong
package enrollservlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ksoap.SoapEnvelope;
import org.ksoap.SoapObject;
import org.kxml.io.XmlWriter;
import org.kxml.parser.XmlParser;
import org.kxml.wap.WbxmlParser;
import org.kxml.wap.WbxmlWriter;

public class EnrollProxyServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    // Encoding cua WBXML la iso-8859-1
    public static final String BYTE_CHARSET = "iso-8859-1";
    // Encoding cua tai lieu XML hoan chinh la UTF-8
    public static final String STR_CHARSET = "UTF-8";
    static final String tagTable[] = { "SOAP-ENV:Envelope", "SOAP-ENV:Body",
            "SOAP-ENC", "result", "soapenv", "val", "src", "dst", "ExecTime",
            "xsd:anyType", "SOAP-ENC:arrayType", "SOAP-ENC:Array",
            "SOAP-ENC:position", "item", "String_1", "String_2", "String_3",
            "String_4", "String_5", "String_6", "String_7", "String_8",
            "String_9", "String_10", "String_11", "int_1", "int_2", "int_3",
            "int_4", "int_5", "int_10", "int_11", "int_12", "int_13", };
    private static final String attrStartTable[] = { "Envelope", "Body",
            "xmlns", "xmlns:xsd", "xmlns:xsi", "xsi:type", "xsd", "xsi", "ENC",
            "encodingStyle", "id", "root", "type", "xmlns:SOAP-ENC",
            "xmlns:SOAP-ENV", "SOAP-ENV:encodingStyle", "SOAP-ENC:root",
            "SOAP-ENC:arrayType", "SOAP-ENC:Array", "SOAP-ENC:position",
            "xsd:anyType" };

    // Bang index cua cac the XML, dung de nen WBXML

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

    // Bang ten cac thuoc tinh cua the XML

    // Cac hang so cau hinh
    // Dia chi cua dich vu Web
    public String serverURL = "http://localhost:8080/enrollfull/enrollservice";

    // Bang cac gia tri co the co cua cac thuoc tinh

    // method namespace cua dich vu web
    public String METHOD_NAME_SPACE = "http://com.test/wsdl/EnrollService";

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        // Phuong thuc doGet, tra ve response cho HTTP GET (trinh duyet goi)
        // Lay luong input stream
        byte[] result = ("Enroll Proxy Servlet is running").getBytes();
        // Dat cac thuoc tinh HTTP Header
        response.setContentType("text/html");
        response.setContentLength(result.length);
        // Mo luong xuat va goi
        OutputStream out = response.getOutputStream();
        out.write(result);
        out.close();
    }

    // Client di dong va proxy servlet giao tiep bang HTTP POST
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        java.net.URL url;
        java.net.URLConnection con;

        byte[] responseBytes;
        int ch;

        // Doc luong stream cua request goi den
        DataInputStream DIS = new DataInputStream(request.getInputStream());
        ByteArrayOutputStream BAOS = new ByteArrayOutputStream();

        while ((ch = DIS.read()) != -1) {
            BAOS.write(ch);
        }
        DIS.close();
        ByteArrayInputStream BAIS = new ByteArrayInputStream(BAOS.toByteArray());

        // Xay dung lai Request SOAP Envelope cua client goi den
        SoapEnvelope requestEnvelope;
        WbxmlParser wbxmlParser;
        try {
            wbxmlParser = new WbxmlParser(BAIS);
            wbxmlParser.setTagTable(0, tagTable);
            wbxmlParser.setAttrStartTable(0, attrStartTable);
            wbxmlParser.setAttrValueTable(0, attrValueTable);

            requestEnvelope = new SoapEnvelope();
            requestEnvelope.parse(wbxmlParser);
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        // Xay dung lai Request SOAP Envelope voi method namespace moi
        // Client ko can biet bat ky thong tin nao ve JAX-RPC that su
        SoapObject requestMethod = (SoapObject) requestEnvelope.getBody();
        requestMethod.setNamespace(METHOD_NAME_SPACE);
        requestEnvelope.setBody(requestMethod);

        // Chuan bi goi request cho JAX-RPC server
        try {
            url = new java.net.URL(serverURL);
            con = url.openConnection();
            con.setUseCaches(false);
            con.setDoInput(true);
            con.setDoOutput(true);
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        // Xay dung lai tai lieu XML van ban tu SOAP Envelope
        BAOS = new ByteArrayOutputStream();
        byte[] requestBytes;
        try {
            String xmlProlog = "<?xml version=\"1.0\" encoding=\""
                    + STR_CHARSET + "\"?>";
            BAOS.write(xmlProlog.getBytes(BYTE_CHARSET));
            XmlWriter xmlWriter = new XmlWriter(new OutputStreamWriter(BAOS,
                    BYTE_CHARSET));
            // Ghi tai lieu XML bang XML Writer
            requestEnvelope.write(xmlWriter);
            xmlWriter.close();
            requestBytes = BAOS.toByteArray();
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        // Thiet lap cac thuoc tinh HTTP Header
        con.setRequestProperty("Content-type", "text/xml");
        con.setRequestProperty("Content-length", "" + requestBytes.length);
        // Mo luong xuat output stream va goi response
        DataOutputStream dataOut = new DataOutputStream(con.getOutputStream());
        dataOut.write(requestBytes);
        dataOut.flush();
        dataOut.close();

        // Chuan bi nhan hoi dap tu JAX-RPC Server
        DIS = new DataInputStream(con.getInputStream());
        BAOS = new ByteArrayOutputStream();
        while ((ch = DIS.read()) != -1) {
            BAOS.write(ch);
        }
        DIS.close();

        BAIS = new ByteArrayInputStream(BAOS.toByteArray());

        // Doc luong du lieu tra ve va xay dung dung reponse SOAP Envelope
        SoapEnvelope responseEnvelope;
        try {
            XmlParser xmlParser = new XmlParser(new InputStreamReader(BAIS,
                    BYTE_CHARSET));

            responseEnvelope = new SoapEnvelope();
            responseEnvelope.parse(xmlParser);
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        // SOAP ghi tai lieu XML nhi phan (WBXML) dung bo WbxmlWriter
        BAOS = new ByteArrayOutputStream();
        try {
            WbxmlWriter wbxmlWriter = new WbxmlWriter(BAOS);
            wbxmlWriter.setTagTable(0, tagTable);
            wbxmlWriter.setAttrStartTable(0, attrStartTable);
            wbxmlWriter.setAttrValueTable(0, attrValueTable);
            responseEnvelope.write(wbxmlWriter);
            wbxmlWriter.close();
            responseBytes = BAOS.toByteArray();
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        // Dat cac thuoc tinh cua HTTP
        response.setContentType("application/octet-stream");
        response.setContentLength(responseBytes.length);

        // Mo luong xuat den client va tra ve ket qua
        OutputStream out = response.getOutputStream();
        out.write(responseBytes);
        out.close();
    }

    @Override
    public String getServletInfo() {
        return "Proxy Servlet providing the proxy for server and client";
    }

    // Khoi tao servlet
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (config.getInitParameter("serverURL") != null) {
            serverURL = config.getInitParameter("serverURL");
        }
        if (config.getInitParameter("method-namespace") != null) {
            METHOD_NAME_SPACE = config.getInitParameter("method-namespace");
        }
    }
}