package services;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abhay on 14/7/16.
 */
public class APIClass extends AsyncTask<String, String, String>  {
    private MyResultReceiver Receiver;
    private int type;
    private final Bundle b = new Bundle();
    HttpURLConnection urlConnection;
    public APIClass(int type, MyResultReceiver Receiver)
    {
        this.type= type;
        this.Receiver=Receiver;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            b.putString("apiresult",result.toString());
            Receiver.send(type,b);
        } catch (Exception e) {
            b.putString("apiresult","");
            Receiver.send(type,b);
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }
}
