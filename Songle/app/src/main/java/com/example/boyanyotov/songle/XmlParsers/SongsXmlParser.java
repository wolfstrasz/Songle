package com.example.boyanyotov.songle.XmlParsers;

import android.util.Xml;

import com.example.boyanyotov.songle.DataStructures.Song;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class SongsXmlParser {
    private static String ns = null;

    public List<Song> parse (InputStream inputStream) throws XmlPullParserException, IOException {

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


    private List<Song> readFeed (XmlPullParser parser) throws XmlPullParserException, IOException {

        List<Song> songs = new ArrayList<Song>();

        parser.require(XmlPullParser.START_TAG,ns,"Songs");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("Song")) {
                songs.add(readSong(parser));
            } else {
                skip(parser);
            }
        }

        return songs;
    }

    private Song readSong(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Song");
        String number = null;
        String artist = null;
        String title = null;
        String link = null;
        while (parser.next()!=XmlPullParser.END_TAG){
            if(parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            switch (name) {

                case "Number":
                    number = readNumber(parser);
                    break;
                case "Artist":
                    artist = readArtist(parser);
                    break;
                case "Title":
                    title = readTitle(parser);
                    break;
                case "Link":
                    link = readLink(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Song (number,artist,title,link);
    }

    private String readNumber(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Number");
        String number = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"Number");
        return number;
    }

    private String readArtist(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Artist");
        String artist = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"Artist");
        return artist;
    }

    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"Title");
        return title;
    }

    private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG,ns,"Link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns,"Link");
        return link;
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        /*
        IOException{
            if(parser.getEventType()!= XmlPullParser.START_TAG){
                throw new IllegalStateException();
            }
        }
        */
        int depth = 1;
        while (depth !=0 ){
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
}

