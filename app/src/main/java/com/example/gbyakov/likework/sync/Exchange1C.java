package com.example.gbyakov.likework.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.gbyakov.likework.data.LikeWorkContract;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Exchange1C {

    static public String LOG_TAG = LikeWorkSyncAdapter.class.getSimpleName();

    static private String mUserName;
    static private String mDomain;
    static private String mPassword;
    static private Context mContext;

    Exchange1C(String username, String domain, String password, Context context) {
        mUserName   = username;
        mPassword   = password;
        mDomain     = domain;
        mContext    = context;
    }

    private static String SendRequest(String method) {

        String request = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Header/><soap:Body>";
        request += "<"+method+" xmlns=\"http://mobile.verra.ru\">";
        request += "</"+method+">";
        request += "</soap:Body></soap:Envelope>";

        try
        {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                    new NTCredentials(mUserName, mPassword, "", mDomain));

            HttpPost httpPost = new HttpPost("http://sr00038.md.mash-dvor.ru/alfa_green/ws/mobile.1cws");

            StringEntity sEntity = new StringEntity(request);
            sEntity.setContentType("text/xml");
            httpPost.setEntity(sEntity);
            HttpResponse response = httpclient.execute(httpPost);

            Integer mStatusCode = response.getStatusLine().getStatusCode();
            if (mStatusCode == 200) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return responseBody;
            }

        }
        catch (Exception e)
        {
            return "";
        }

        return "";
    }

    public static void UpdateOrders() {

        String response = SendRequest("GetOrders");
        if (!response.equals("")){
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:order");
                for (int i=0;i<items.getLength();i++){
                    Order(items.item(i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static void Order(Node orderNode) throws ParseException {

        ContentValues orderValues = new ContentValues();
        NodeList orderAttributes = orderNode.getChildNodes();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        ArrayList<String> orderID = new ArrayList<>();

        for (int i = 0; i < orderAttributes.getLength(); i++) {
            Node attr = orderAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_ID_1C, attr.getTextContent());
                orderID.add(attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:number")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_NUMBER, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:date")) {
                Date date = format.parse(attr.getTextContent());
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_DATE, date.getTime());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:number")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_NUMBER, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:sum")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_SUM, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:type")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_TYPE, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:comment")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_COMMENT, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:car")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_CAR_ID, Car(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:customer")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_CUSTOMER_ID, Client(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:client")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_CLIENT_ID, Client(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:status")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_STATUS_ID, Status(attr));
            }
        }

        String selection = LikeWorkContract.OrderEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = orderID.toArray(new String[orderID.size()]);

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.OrderEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor.moveToFirst()) {
            mContext.getContentResolver().update(LikeWorkContract.OrderEntry.CONTENT_URI, orderValues, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.OrderEntry.CONTENT_URI, orderValues);
        }

    }

    private static String Car(Node carNode) {

        ContentValues carValues = new ContentValues();
        NodeList carAttributes = carNode.getChildNodes();
        ArrayList<String> carID = new ArrayList<>();

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                carValues.put(LikeWorkContract.CarEntry.COLUMN_ID_1C, attr.getTextContent());
                carID.add(attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:brand")) {
                carValues.put(LikeWorkContract.CarEntry.COLUMN_BRAND, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:model")) {
                carValues.put(LikeWorkContract.CarEntry.COLUMN_MODEL, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:regnumber")) {
                carValues.put(LikeWorkContract.CarEntry.COLUMN_REGNUMBER, attr.getTextContent());
            }
        }

        String selection = LikeWorkContract.CarEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = carID.toArray(new String[carID.size()]);

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor.moveToFirst()) {
            mContext.getContentResolver().update(LikeWorkContract.CarEntry.CONTENT_URI, carValues, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.CarEntry.CONTENT_URI, carValues);
        }

        return carID.get(0);
    }

    private static String Client(Node clientNode) {

        ContentValues clientValues = new ContentValues();
        NodeList carAttributes = clientNode.getChildNodes();
        ArrayList<String> clientID = new ArrayList<>();

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                clientValues.put(LikeWorkContract.ClientEntry.COLUMN_ID_1C, attr.getTextContent());
                clientID.add(attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:name")) {
                clientValues.put(LikeWorkContract.ClientEntry.COLUMN_NAME, attr.getTextContent());
            }
        }

        String selection = LikeWorkContract.ClientEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = clientID.toArray(new String[clientID.size()]);

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.ClientEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor.moveToFirst()) {
            mContext.getContentResolver().update(LikeWorkContract.ClientEntry.CONTENT_URI, clientValues, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.ClientEntry.CONTENT_URI, clientValues);
        }

        return clientID.get(0);

    }

    private static String Status(Node statusNode) {

        ContentValues statusValues = new ContentValues();
        NodeList carAttributes = statusNode.getChildNodes();
        ArrayList<String> statusID = new ArrayList<>();

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                statusValues.put(LikeWorkContract.StatusEntry.COLUMN_ID_1C, attr.getTextContent());
                statusID.add(attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:name")) {
                statusValues.put(LikeWorkContract.StatusEntry.COLUMN_NAME, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:color")) {
                statusValues.put(LikeWorkContract.StatusEntry.COLUMN_COLOR, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:group")) {
                statusValues.put(LikeWorkContract.StatusEntry.COLUMN_GROUP, attr.getTextContent());
            }
        }

        String selection = LikeWorkContract.StatusEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = statusID.toArray(new String[statusID.size()]);

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.StatusEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor.moveToFirst()) {
            mContext.getContentResolver().update(LikeWorkContract.StatusEntry.CONTENT_URI, statusValues, selection, selectionArgs);
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.StatusEntry.CONTENT_URI, statusValues);
        }

        return statusID.get(0);
    }

}
