package com.example.boyanyotov.songle.XmlParsers;

import android.util.Xml;

import com.example.boyanyotov.songle.DataStructures.IconStyles;
import com.example.boyanyotov.songle.DataStructures.Placemark;
import com.example.boyanyotov.songle.LevelData;
import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class PlacemarksXmlParser {
    private static String ns = null;

    public List<Placemark> parse (InputStream inputStream) throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature (XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream,null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            inputStream.close();
        }
    }

    private List<Placemark> readFeed (XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"kml");

        //Log.e(TAG,"readFeed");
        List<Placemark> placemarks = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            //Log.w(TAG,"Parser.getName = " + name);
            // Starts by looking for the entry tag
            if (name.equals("Document")) {
                placemarks = readDocument(parser);
            } else {
                skip(parser);
            }
        }

        return placemarks;
    }
    private List<Placemark> readDocument(XmlPullParser parser) throws XmlPullParserException,IOException{
        parser.require(XmlPullParser.START_TAG,ns,"Document");

        //Log.e(TAG,"readDocument");
        List<Placemark> placemarks = new ArrayList<>();
        HashMap<String,IconStyles> allIconsStyles = new HashMap<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            //Log.w(TAG,"Parser.getName = " + name);
            // Starts by looking for the entry tag
            if (name.equals("Style")){
                //Log.e(TAG,"STYLE attributes: " + parser.getAttributeValue(0));
                String id = parser.getAttributeValue(0);
                IconStyles iconStyle = readStyle(parser);
                allIconsStyles.put(id,iconStyle);

            }
            else if (name.equals("Placemark")) {
                placemarks.add(readPlacemark(parser));
            } else {
                skip(parser);
            }
        }
        LevelData.getInstance().setIconStylesMap(allIconsStyles);
        return placemarks;
    }

    private  IconStyles readStyle(XmlPullParser parser) throws IOException,XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns,"Style");
        //Log.i(TAG,"readStyle");
        IconStyles iconStyle = new IconStyles(null,null);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
           // Log.w(TAG,"Parser.getName = " + name);
            if(name.equals("IconStyle")){
                iconStyle = readIconStyle(parser);
            }
        }
        return iconStyle;
    }

    private IconStyles readIconStyle(XmlPullParser parser) throws XmlPullParserException,IOException {
        parser.require(XmlPullParser.START_TAG,ns,"IconStyle");
//        Log.e(TAG,"readIconStyle");
        String scale = null;
        String link = null;

        while(parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType()!=XmlPullParser.START_TAG) continue;
            String parserName = parser.getName();
            if(parserName.equals("scale")){
                scale = readScale(parser);
            }
            else if(parserName.equals("Icon")){
                link = readIcon(parser);
            }
        }
//        Log.w(TAG,"Scale = " + scale + " : Link = " + link);
        return new IconStyles(scale,link);
    }
    private String readScale(XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,"scale");
//        Log.i(TAG,"readScale");
        String scale = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"scale");
        return scale;
    }
    private String readIcon(XmlPullParser parser)throws  IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,"Icon");
        String link = null;

        while(parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType()!=XmlPullParser.START_TAG) continue;
            String parserName = parser.getName();
            if(parserName.equals("href")){
                link = readLink(parser);
            }
        }
        return link;
    }

    private String readLink(XmlPullParser parser) throws IOException,XmlPullParserException{
        parser.require(XmlPullParser.START_TAG,ns,"href");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"href");
        return link;
    }

    private Placemark readPlacemark(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Placemark");
        String name = null;
        String description = null;
        String styleURL = null;
        LatLng point = null;
        while (parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG) continue;

            String parserName = parser.getName();
            switch (parserName) {

                case "name":
                    name = readName(parser);
                    break;
                case "description":
                    description = readDescription(parser);
                    break;
                case "styleUrl":
                    styleURL = readStyleUrl(parser);
                    break;
                case "Point":
                    point = readPoint(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Placemark (name,description,styleURL,point);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private String readStyleUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "styleUrl");
        String styleUrl = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "styleUrl");
        return styleUrl;
    }

    private LatLng readPoint(XmlPullParser parser) throws IOException, XmlPullParserException {

        parser.require(XmlPullParser.START_TAG, ns, "Point");
        String coordinates = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("coordinates")) {
                coordinates = readText(parser);
            } else {
                skip(parser);
            }
        }

        String[] latlong =  coordinates.split(",");
        double longitude = Double.parseDouble(latlong[0]);
        double latitude = Double.parseDouble(latlong[1]);
        return new LatLng(latitude,longitude);
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }


}
