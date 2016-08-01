package com.zigapk.alert.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import com.zigapk.alert.exceptions.CouldNotReachServerException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by zigapk on 22.6.2016.
 */
public class InternetUtils {
    public static String getTextFromUrl(String url) throws CouldNotReachServerException {
        try {
            URLConnection feedUrl = new URL(url).openConnection();
            try {
                InputStream in = feedUrl.getInputStream();
                String result = convertStreamToString(in);

                return result;
            } catch (Exception e) {
                throw new CouldNotReachServerException();
            }

        } catch (Exception e) {
            throw new CouldNotReachServerException();
        }

    }


    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }
}