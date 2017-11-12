package edu.umkc.mobile.cargasmileageestimator.model;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import edu.umkc.mobile.cargasmileageestimator.data.CarDetailsCollection;
import edu.umkc.mobile.cargasmileageestimator.data.MileageCollection;

/**
 * Created by vinuthna on 11-11-2017.
 */

public class MileageModel {

    MileageCollection mileageCollection;

    public static String POST(String url, MileageCollection mileageCollection){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("distance", mileageCollection.getDistance());
            jsonObject.accumulate("gasRemaining", mileageCollection.getGasRemaining());
            jsonObject.accumulate("range", mileageCollection.getRange());
            jsonObject.accumulate("mileage", mileageCollection.getMileage());
            jsonObject.accumulate("date", mileageCollection.getDate());
            jsonObject.accumulate("totalDistance", mileageCollection.getRange());
            jsonObject.accumulate("totalGas", mileageCollection.getMileage());
            jsonObject.accumulate("totalGasCost", mileageCollection.getTotalGasCost());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static List<MileageCollection> GETALLCollection(String url){
        InputStream inputStream = null;
        String result = "";
        List<MileageCollection> mileageCollectionList = null;
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                mileageCollectionList = new ArrayList<MileageCollection>();
                try {
                    if(result!=null && !"".equalsIgnoreCase(result)){

                        JSONArray jsonarray = new JSONArray(result);
                        for (int i = 0; i < jsonarray.length(); i++) {
                            MileageCollection mileageCollection = new MileageCollection();
                            JSONObject jsonobject = jsonarray.getJSONObject(i);
                            mileageCollection.setDistance(jsonobject.getString("distance"));
                            mileageCollection.setGasRemaining(jsonobject.getString("gasRemaining"));
                            mileageCollection.setMileage(jsonobject.getString("mileage"));
                            mileageCollection.setDate(jsonobject.getString("date"));
                            mileageCollectionList.add(mileageCollection);
                        }
                    }

                }catch (Exception e){

                }

            }
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return mileageCollectionList;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static String POST_CarDetails(String url, CarDetailsCollection carDetailsCollection){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("emailId", carDetailsCollection.getEmailId());
            jsonObject.accumulate("make", carDetailsCollection.getMake());
            jsonObject.accumulate("model", carDetailsCollection.getModel());
            jsonObject.accumulate("year", carDetailsCollection.getYear());
            jsonObject.accumulate("odometer", carDetailsCollection.getOdometer());
            jsonObject.accumulate("tankCapacity", carDetailsCollection.getTankCapacity());
            jsonObject.accumulate("mileage", carDetailsCollection.getMileage());
            jsonObject.accumulate("fuel", carDetailsCollection.getFuel());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

}
