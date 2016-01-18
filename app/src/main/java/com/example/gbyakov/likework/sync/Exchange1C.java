package com.example.gbyakov.likework.sync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.gbyakov.likework.R;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Exchange1C {

    static public String LOG_TAG = Exchange1C.class.getSimpleName();

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

            HttpPost httpPost = new HttpPost(mContext.getString(R.string.ws_link));

            StringEntity sEntity = new StringEntity(request);
            sEntity.setContentType("text/xml");
            httpPost.setEntity(sEntity);
            HttpResponse response = httpclient.execute(httpPost);

            Integer mStatusCode = response.getStatusLine().getStatusCode();
            if (mStatusCode == 200) {
                return EntityUtils.toString(response.getEntity());
            }

        }
        catch (Exception e)
        {
            return "";
        }

        return "";
    }

    public void UpdateOrders() {

        List<String> ids = new ArrayList<>();

        String response = SendRequest("GetOrders");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetOrders - start");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:order");
                for (int i=0;i<items.getLength();i++){
                    ids.add("'"+Order(items.item(i))+"'");
                }

                // delete orders not from list
                String selection = LikeWorkContract.OrderEntry.COLUMN_ID_1C + " NOT IN ("+
                        android.text.TextUtils.join(",", ids)+")";

                mContext.getContentResolver().delete(LikeWorkContract.OrderEntry.CONTENT_URI, selection, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetOrders - finish");
        }

    }

    public void UpdateCalls() {

        List<String> ids = new ArrayList<>();

        String response = SendRequest("GetFollowups");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetFollowUps - start");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:followup");
                for (int i=0;i<items.getLength();i++){
                    ids.add("'"+Call(items.item(i))+"'");
                }

                // delete calls not from list
                String selection = LikeWorkContract.CallEntry.TABLE_NAME + "." +
                        LikeWorkContract.CallEntry.COLUMN_ID_1C + " NOT IN (" +
                        android.text.TextUtils.join(",", ids)+")";

                mContext.getContentResolver().delete(LikeWorkContract.CallEntry.CONTENT_URI, selection, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetFollowUps - finish");
        }

    }

    public void UpdateRecords() {

        List<String> ids = new ArrayList<>();

        String response = SendRequest("GetRecords");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetRecords - start");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:record");
                for (int i=0;i<items.getLength();i++){
                    ids.add("'" + Record(items.item(i)) + "'");
                }

                // delete records not from list
                String selection = LikeWorkContract.RecordEntry.TABLE_NAME + "." +
                        LikeWorkContract.RecordEntry.COLUMN_ID_1C + " NOT IN (" +
                        android.text.TextUtils.join(",", ids)+")";

                mContext.getContentResolver().delete(LikeWorkContract.RecordEntry.CONTENT_URI, selection, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetRecords - finish");
        }

    }

    public void UpdateStates() {

        String response = SendRequest("GetStates");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetStates - start");
            mContext.getContentResolver().delete(LikeWorkContract.StateEntry.CONTENT_URI, null, null);
            Log.d(LOG_TAG, "GetStates - trancate table");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:state");
                for (int i=0;i<items.getLength();i++){
                    State(items.item(i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetStates - finish");
        }

    }

    public void UpdateParts() {

        String response = SendRequest("GetParts");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetParts - start");
            mContext.getContentResolver().delete(LikeWorkContract.PartEntry.CONTENT_URI, null, null);
            Log.d(LOG_TAG, "GetParts - trancate table");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:part");
                for (int i=0;i<items.getLength();i++){
                    Part(items.item(i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetParts - finish");
        }

    }

    public void UpdateOperations() {

        String response = SendRequest("GetOperations");
        if (!response.equals("")){
            Log.d(LOG_TAG, "GetOperations - start");
            mContext.getContentResolver().delete(LikeWorkContract.OperationEntry.CONTENT_URI, null, null);
            Log.d(LOG_TAG, "GetOperations - trancate table");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document dom = builder.parse(new ByteArrayInputStream(response.getBytes()));
                Element root = dom.getDocumentElement();
                NodeList items = root.getElementsByTagName("m:operation");
                for (int i=0;i<items.getLength();i++){
                    Operation(items.item(i));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.d(LOG_TAG, "GetOperations - finish");
        }

    }

    private static String Order(Node orderNode) throws ParseException {

        ContentValues orderValues = new ContentValues();
        NodeList orderAttributes = orderNode.getChildNodes();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String orderID = "";

        for (int i = 0; i < orderAttributes.getLength(); i++) {
            Node attr = orderAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_ID_1C, attr.getTextContent());
                orderID = attr.getTextContent();
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:number")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_NUMBER, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:date")) {
                Date date = format.parse(attr.getTextContent());
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_DATE, date.getTime());
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
            else if(attr.getNodeName().equalsIgnoreCase("m:reason")) {
                orderValues.put(LikeWorkContract.OrderEntry.COLUMN_REASON, attr.getTextContent());
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
        String[] selectionArgs = {orderID};

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.OrderEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, orderValues)) {
                mContext.getContentResolver().update(LikeWorkContract.OrderEntry.CONTENT_URI, orderValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.OrderEntry.CONTENT_URI, orderValues);
        }

        return orderID;
    }

    private static String Record(Node recordNode) throws ParseException {

        ContentValues recordValues = new ContentValues();
        NodeList recordAttributes = recordNode.getChildNodes();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String recordID = "";

        for (int i = 0; i < recordAttributes.getLength(); i++) {
            Node attr = recordAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_ID_1C, attr.getTextContent());
                recordID = attr.getTextContent();
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:number")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_NUMBER, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:date")) {
                Date date = format.parse(attr.getTextContent());
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_DATE, date.getTime());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:sum")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_SUM, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:type")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_TYPE, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:comment")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_COMMENT, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:reason")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_REASON, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:car")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_CAR_ID, Car(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:customer")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_CUSTOMER_ID, Client(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:client")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_CLIENT_ID, Client(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:done")) {
                recordValues.put(LikeWorkContract.RecordEntry.COLUMN_DONE, (attr.getTextContent().equals("true")) ? 1 : 0);
            }
        }

        String selection = LikeWorkContract.RecordEntry.TABLE_NAME + "." +
                            LikeWorkContract.RecordEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = {recordID};

        String[] projection = {
                LikeWorkContract.RecordEntry.TABLE_NAME + "." + LikeWorkContract.RecordEntry._ID,
                LikeWorkContract.RecordEntry.TABLE_NAME + "." + LikeWorkContract.RecordEntry.COLUMN_ID_1C,
                LikeWorkContract.RecordEntry.COLUMN_NUMBER,
                LikeWorkContract.RecordEntry.COLUMN_DATE,
                LikeWorkContract.RecordEntry.COLUMN_CAR_ID,
                LikeWorkContract.RecordEntry.COLUMN_CLIENT_ID,
                LikeWorkContract.RecordEntry.COLUMN_CUSTOMER_ID,
                LikeWorkContract.RecordEntry.COLUMN_TYPE,
                LikeWorkContract.RecordEntry.COLUMN_COMMENT,
                LikeWorkContract.RecordEntry.COLUMN_REASON,
                LikeWorkContract.RecordEntry.COLUMN_SUM
        };

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.RecordEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, recordValues)) {
                mContext.getContentResolver().update(LikeWorkContract.RecordEntry.CONTENT_URI, recordValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.RecordEntry.CONTENT_URI, recordValues);
        }

        return recordID;

    }

    private static String Call(Node callNode) throws ParseException {

        ContentValues CallValues = new ContentValues();
        NodeList CallAttributes = callNode.getChildNodes();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String callID = "";

        for (int i = 0; i < CallAttributes.getLength(); i++) {
            Node attr = CallAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_ID_1C, attr.getTextContent());
                callID = attr.getTextContent();
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:date")) {
                Date date = format.parse(attr.getTextContent());
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_DATE, date.getTime());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:sum")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_SUM, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:type")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_TYPE, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:reason")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_REASON, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:car")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_CAR_ID, Car(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:client")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_CLIENT_ID, Client(attr));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:interview")) {
                CallValues.put(LikeWorkContract.CallEntry.COLUMN_INTERVIEW_ID, attr.getTextContent());
            }
        }

        String[] projection = {
                LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry._ID,
                LikeWorkContract.CallEntry.TABLE_NAME + "." + LikeWorkContract.CallEntry.COLUMN_ID_1C,
                LikeWorkContract.CallEntry.COLUMN_DATE,
                LikeWorkContract.CallEntry.COLUMN_CAR_ID,
                LikeWorkContract.CallEntry.COLUMN_CLIENT_ID,
                LikeWorkContract.CallEntry.COLUMN_TYPE,
                LikeWorkContract.CallEntry.COLUMN_REASON,
                LikeWorkContract.CallEntry.COLUMN_SUM,
                LikeWorkContract.CallEntry.COLUMN_INTERVIEW_ID
        };

        String selection = LikeWorkContract.CallEntry.TABLE_NAME + "." +
                            LikeWorkContract.CallEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = {callID};

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.CallEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, CallValues)) {
                mContext.getContentResolver().update(LikeWorkContract.CallEntry.CONTENT_URI, CallValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.CallEntry.CONTENT_URI, CallValues);
        }

        return callID;
    }

    private static void State(Node node) throws ParseException {

        ContentValues newValues = new ContentValues();
        NodeList attrs = node.getChildNodes();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id_doc")) {
                newValues.put(LikeWorkContract.StateEntry.COLUMN_DOC_ID_1C, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:user")) {
                newValues.put(LikeWorkContract.StateEntry.COLUMN_USER, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:date")) {
                Date date = format.parse(attr.getTextContent());
                newValues.put(LikeWorkContract.StateEntry.COLUMN_DATE, date.getTime());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:status")) {
                newValues.put(LikeWorkContract.StateEntry.COLUMN_STATUS, Status(attr));
            }
        }

        mContext.getContentResolver().insert(LikeWorkContract.StateEntry.CONTENT_URI, newValues);

    }

    private static void Part(Node node) throws ParseException {

        ContentValues newValues = new ContentValues();
        NodeList attrs = node.getChildNodes();

        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id_doc")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_DOC_ID_1C, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:linenumber")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_LINENUM, Integer.parseInt(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:code")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_CODE_1C, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:catnumber")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_CATNUM, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:name")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_NAME, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:amount")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_AMOUNT, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:sum")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_SUM, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:status")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_STATUS, Integer.parseInt(attr.getTextContent()));
            }
        }

        mContext.getContentResolver().insert(LikeWorkContract.PartEntry.CONTENT_URI, newValues);
    }

    private static void Operation(Node node) throws ParseException {

        ContentValues newValues = new ContentValues();
        NodeList attrs = node.getChildNodes();

        for (int i = 0; i < attrs.getLength(); i++) {
            Node attr = attrs.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id_doc")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_DOC_ID_1C, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:linenumber")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_LINENUM, Integer.parseInt(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:code")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_CODE_1C, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:name")) {
                newValues.put(LikeWorkContract.PartEntry.COLUMN_NAME, attr.getTextContent());
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:amount")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_AMOUNT, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:sum")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_SUM, Double.parseDouble(attr.getTextContent()));
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:status")) {
                newValues.put(LikeWorkContract.OperationEntry.COLUMN_STATUS, attr.getTextContent());
            }
        }

        mContext.getContentResolver().insert(LikeWorkContract.OperationEntry.CONTENT_URI, newValues);
    }

    private static String Car(Node carNode) {

        ContentValues carValues = new ContentValues();
        NodeList carAttributes = carNode.getChildNodes();
        String carID = "";

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                carValues.put(LikeWorkContract.CarEntry.COLUMN_ID_1C, attr.getTextContent());
                carID = attr.getTextContent();
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
        String[] selectionArgs = {carID};

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.CarEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, carValues)) {
                mContext.getContentResolver().update(LikeWorkContract.CarEntry.CONTENT_URI, carValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.CarEntry.CONTENT_URI, carValues);
        }

        return carID;
    }

    private static String Client(Node clientNode) {

        ContentValues clientValues = new ContentValues();
        NodeList carAttributes = clientNode.getChildNodes();
        String clientID = "";

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                clientValues.put(LikeWorkContract.ClientEntry.COLUMN_ID_1C, attr.getTextContent());
                clientID = attr.getTextContent();
            }
            else if(attr.getNodeName().equalsIgnoreCase("m:name")) {
                clientValues.put(LikeWorkContract.ClientEntry.COLUMN_NAME, attr.getTextContent());
            }
        }

        String selection = LikeWorkContract.ClientEntry.COLUMN_ID_1C + " = ?";
        String[] selectionArgs = {clientID};

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.ClientEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, clientValues)) {
                mContext.getContentResolver().update(LikeWorkContract.ClientEntry.CONTENT_URI, clientValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.ClientEntry.CONTENT_URI, clientValues);
        }

        return clientID;

    }

    private static String Status(Node statusNode) {

        ContentValues statusValues = new ContentValues();
        NodeList carAttributes = statusNode.getChildNodes();
        String statusID = "";

        for (int i = 0; i < carAttributes.getLength(); i++) {
            Node attr = carAttributes.item(i);
            if (attr.getNodeName().equalsIgnoreCase("m:id")) {
                statusValues.put(LikeWorkContract.StatusEntry.COLUMN_ID_1C, attr.getTextContent());
                statusID = attr.getTextContent();
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
        String[] selectionArgs = {statusID};

        Cursor cursor = mContext.getContentResolver().query(
                LikeWorkContract.StatusEntry.CONTENT_URI,
                null,
                selection,
                selectionArgs,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            if (dataChanged(cursor, statusValues)) {
                mContext.getContentResolver().update(LikeWorkContract.StatusEntry.CONTENT_URI, statusValues, selection, selectionArgs);
            }
        } else {
            mContext.getContentResolver().insert(LikeWorkContract.StatusEntry.CONTENT_URI, statusValues);
        }

        return statusID;
    }

    private static boolean dataChanged(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            String expectedValue = entry.getValue().toString();
            if (idx == -1) return true;
            if (!expectedValue.equals(valueCursor.getString(idx))) return true;
        }
        return false;
    }
}
