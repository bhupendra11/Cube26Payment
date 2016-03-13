package cube26.payments.bhupendra.com.cube26payment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

import cube26.payments.bhupendra.com.cube26payment.util.util;

/**
 * A placeholder fragment containing a simple view.
 */
public class GatewayFragment extends Fragment {
    private GatewayAdapter gatewayAdapter;
    private String SORT_PARAM ="rating";
    private  ArrayList<Gateway> resultList = new ArrayList<Gateway>();
    private static final String LOG_TAG = GatewayFragment.class.getSimpleName();
    private boolean isSavedInstance =false;
    private Gateway gateway;
    private EditText searchEditText;
    private  TextView totalGatewayTextview;
    private  TextView  apiHitsTextView;

    private int apiHits;

    public GatewayFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState != null && savedInstanceState.containsKey("GatewayList")){
            Log.d(LOG_TAG,"Using savedInstanceBundle ");
            isSavedInstance= true;
            resultList.clear();
            resultList =savedInstanceState.getParcelableArrayList("GatewayList");
        }
        else{
            updateMovies();
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Log.d(LOG_TAG,"Inside onCreateView");

        searchEditText = (EditText) rootView.findViewById(R.id.inputSearch);

        // Search EditText textChanged Listener

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
               gatewayAdapter.getFilter().filter(cs);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }


        });


        // gateway adapter and listview

       gatewayAdapter = new GatewayAdapter(getActivity() , new ArrayList<Gateway>());

        ListView gatewayListView = (ListView) rootView.findViewById(R.id.gateways_list);
        gatewayListView.setAdapter(gatewayAdapter);

        gatewayListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("GatewayFragment", "inside onItemClick");

                gateway = gatewayAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("GatewayParcel", gateway);
                startActivity(intent);

            }
        } );

       totalGatewayTextview = (TextView) rootView.findViewById(R.id.totalGatewayTextview);
        apiHitsTextView = (TextView) rootView.findViewById(R.id.apiHitsTextview);


        return  rootView;
    }

    public void onCreateOptionsMenu(Menu menu , MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_fragment_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_sort_setup_fee){

            SORT_PARAM = "setup_fee";
           gatewayAdapter.sort(new util.SetupFeeComparator());


        }

        if(id== R.id.action_sort_rating){

            SORT_PARAM = "rating";
            gatewayAdapter.sort(new util.RatingComparator());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("GatewayList",resultList);
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG," onStart Called");


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG," onActivityCreated Called");
        totalGatewayTextview.setText("Total Gateways: "+resultList.size());
        apiHitsTextView.setText("API Hits: " +apiHits);

    }

    public void updateMovies(){
        FetchGatewayDataTask gatewayDataTask = new FetchGatewayDataTask();
        Log.d(LOG_TAG," updateMovies Called");
        gatewayDataTask.execute();


        // To get API Hits Count

        FetchApiHitsTask fetchApiHitsTsk = new FetchApiHitsTask();
        fetchApiHitsTsk.execute();

        Log.d(LOG_TAG,"resultList Size: "+resultList.size() +"  APi hits: "+apiHits);


    }




    public class FetchGatewayDataTask extends AsyncTask<Void, Void, ArrayList<Gateway>>{

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
                resultList.add(gateway);

            }

            return resultList;
        }


        @Override
        protected  ArrayList<Gateway> doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String gatewayJsonStr = null;

            try {
                // Construct the URL for the paymentGateway API query

                // URI can be "list_gateway"  or "api_hits"
                final String GATEWAYS_LIST_URL = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=list_gateway";

                final String API_HITS_URL = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=api_hits";

                Uri builtUri = Uri.parse(GATEWAYS_LIST_URL);
                Uri apiHitsUri = Uri.parse(API_HITS_URL);





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
        protected void onPostExecute(ArrayList<Gateway> resultList) {
            super.onPostExecute(resultList);

            if(resultList !=null) {

                gatewayAdapter.clear();

                Gateway curGateway;
                for (int i = 0; i < resultList.size(); i++) {
                    curGateway = resultList.get(i);
                    gatewayAdapter.add(curGateway);
                }

                totalGatewayTextview.setText("Total Gateways: "+resultList.size());

            }
            else{
                // Let the user know that some problem has occurred via a toast
                Toast.makeText(getContext(),getActivity().getString(R.string.no_gateway_data) ,Toast.LENGTH_SHORT).show();
            }


        }
    }


    // Backgound task to fetch API Hits

    public class FetchApiHitsTask  extends AsyncTask<Void, Void, Integer>{

        private final String LOG_TAG = FetchApiHitsTask.class.getSimpleName();


        @Override
        protected Integer doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            // Will contain the raw JSON response as a string.
            String gatewayJsonStr = null;

            try {
                // Construct the URL for the paymentGateway API Hits query


                final String API_HITS_URL = "http://hackerearth.0x10.info/api/payment_portals?type=json&query=api_hits";

                Uri apiHitsUri = Uri.parse(API_HITS_URL);

                URL url = new URL(apiHitsUri.toString());
                Log.v(LOG_TAG, "Api Hits URI : "+apiHitsUri.toString());

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
                Log.d(LOG_TAG,"Gateway API hits  Json String: " +gatewayJsonStr);
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
                return getApiHitsFromJson(gatewayJsonStr);
            }
            catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }

        private int getApiHitsFromJson(String jsonStr) throws JSONException{

            final String API_HITS = "api_hits";



            JSONObject gatewayJsonObject = new JSONObject(jsonStr);
            int hits = Integer.parseInt( gatewayJsonObject.getString(API_HITS) );


            return  hits;

        }


        @Override
        protected void onPostExecute(Integer apiHitsCount) {
            super.onPostExecute(apiHitsCount);

           apiHits = apiHitsCount;
            apiHitsTextView.setText("API Hits: " +apiHits);



        }
    }

}
