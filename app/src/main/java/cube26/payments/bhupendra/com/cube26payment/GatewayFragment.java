package cube26.payments.bhupendra.com.cube26payment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class GatewayFragment extends Fragment {
    private GatewayAdapter gatewayAdapter;
    private  ArrayList<Gateway> gatewayList = new ArrayList<Gateway>();
    private static final String LOG_TAG = GatewayFragment.class.getSimpleName();
    private boolean isSavedInstance =false;

    public GatewayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(LOG_TAG,"Inside onCreateView");

       gatewayAdapter = new GatewayAdapter(getActivity() , new ArrayList<Gateway>());

        ListView gatewayList = (ListView) rootView.findViewById(R.id.gateways_list);
        gatewayList.setAdapter(gatewayAdapter);

        gatewayList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                

            }
        } );

        return  rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG," onStart Called");

        updateMovies();
    }

    public void updateMovies(){
        FetchGatewayDataTask gatewayDataTask = new FetchGatewayDataTask();
        Log.d(LOG_TAG," updateMovies Called");
        String QUERY_PARAM = "list_gateway";
        gatewayDataTask.execute(QUERY_PARAM);
    }










    public class FetchGatewayDataTask extends AsyncTask<String, Void, ArrayList<Gateway>>{

        private final String LOG_TAG = FetchGatewayDataTask.class.getSimpleName();

        private ArrayList<Gateway> getMoviesPosterDataFromJson(String gatewayJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.

            final String GATEWAY_RESULT = "payment_gateways";

            final String GATEWAY_NAME = "name";
            final String GATEWAY_IMAGE = "image";
            final String GATEWAY_DESCRIPTION = "description";
            final String GATEWAY_BRANDING  = "branding";
            final String GATEWAY_RATING  = "rating";
            final String GATEWAY_CURRENCIES = "currencies";
            final String GATEWAY_SETUP_FEE ="setup_fee";
            final String GATEWAY_TRANSACTION_FEE = "transaction_fees";
            final String GATEWAY_HOW_TO_DOC ="how_to_document";


            JSONObject gatewayJson = new JSONObject(gatewayJsonStr);
            JSONArray gatewayArray = gatewayJson.getJSONArray(GATEWAY_RESULT);
            // API returns json gateway objects


            for(int i = 0; i < gatewayArray.length(); i++) {

                // Create gateway object
                Gateway gateway;

                String name;
                String image;
                String description;
                String branding;
                String rating;
                String currencies;
                String setup_fee;
                String transaction_fee;
                String how_to_doc;
                // Get the JSON object representing the movie
                JSONObject gatewayObject = gatewayArray.getJSONObject(i);


                name = gatewayObject.getString(GATEWAY_NAME);
                image = gatewayObject.getString(GATEWAY_IMAGE);
                description = gatewayObject.getString(GATEWAY_DESCRIPTION);
                branding = gatewayObject.getString(GATEWAY_BRANDING);
                rating = gatewayObject.getString(GATEWAY_RATING);
                currencies = gatewayObject.getString(GATEWAY_CURRENCIES);
                setup_fee = gatewayObject.getString(GATEWAY_SETUP_FEE);
                transaction_fee = gatewayObject.getString(GATEWAY_TRANSACTION_FEE);
                how_to_doc = gatewayObject.getString(GATEWAY_HOW_TO_DOC);


                gateway =new Gateway(name,image,description,branding,rating,currencies,setup_fee,transaction_fee,how_to_doc);
                gatewayList.add(gateway);

            }

            return gatewayList;
        }


        @Override
        protected  ArrayList<Gateway> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String gatewayJsonStr = null;

            try {
                // Construct the URL for the paymentGateway API query


                final String GATEWAYS_BASE_URL = "http://hackerearth.0x10.info/api/payment_portals?type=json";
                // query can be "list_gateway"  or "api_hits"
                final String QUERY_PARAM = "query";

                Uri builtUri = Uri.parse(GATEWAYS_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .build();




                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built URI : "+builtUri.toString());

                // Create the request to Gateway API, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");   // Just for debugging purposes
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                gatewayJsonStr = buffer.toString();
                  Log.d(LOG_TAG,"Gateway Json String: " +gatewayJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the payment gateway data, there's no point in attempting
                // to parse it.
                return  null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesPosterDataFromJson(gatewayJsonStr);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }


        @Override
        protected void onPostExecute(ArrayList<Gateway> results) {
            super.onPostExecute(gatewayList);

            if(gatewayList !=null) {

                gatewayAdapter.clear();

                Gateway curGateway;
                for (int i = 0; i < gatewayList.size(); i++) {
                    curGateway = gatewayList.get(i);
                    gatewayAdapter.add(curGateway);
                }

            }
            else{
                // Let the user know that some problem has occurred via a toast
                Toast.makeText(getContext(),getActivity().getString(R.string.no_gateway_data) ,Toast.LENGTH_SHORT).show();
            }


        }
    }
}
