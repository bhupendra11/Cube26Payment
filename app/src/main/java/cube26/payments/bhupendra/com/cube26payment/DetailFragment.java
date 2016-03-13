package cube26.payments.bhupendra.com.cube26payment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private String name;
    private String image;
    private String description;
    private String branding;
    private String rating;
    private String currencies;
    private String setup_fee;
    private String transaction_fees;
    private String how_to_doc;

    // For handling the document dowwnload

    DownloadManager downloadManager;
    private long downloadReference;
    private BroadcastReceiver receiverDownloadComplete;
    private BroadcastReceiver receiverNotificationClicked;


    private Gateway gateway;


    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState != null && savedInstanceState.containsKey("GatewayParcel")){

            Log.d(LOG_TAG,"Using savedInstanceBundle ");

            gateway =savedInstanceState.getParcelable("GatewayParcel");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();

        if(intent != null){

            Log.d(LOG_TAG,"Inside intent !=null ");
            if(intent.hasExtra("GatewayParcel") ){

                Log.d(LOG_TAG,"Inside intent.hasExtra()");

               gateway = intent.getParcelableExtra("GatewayParcel");

               name = gateway.name;
               image = gateway.image;
                description = gateway.description;
                branding = gateway.branding;
                rating = gateway.rating;
                currencies = gateway.currencies;
                setup_fee = gateway.setup_fee;
                transaction_fees = gateway.transaction_fees;
                how_to_doc = gateway.how_to_doc;



                Log.d(LOG_TAG," Image url: "+image);
                Log.d(LOG_TAG,"Gateway Name : "+name);
                Log.d(LOG_TAG,"Gateway description: "+ description);

                getActivity().setTitle(name);

                TextView gatewayNameTextView = (TextView) rootView.findViewById(R.id.gateway_name_textview);
                gatewayNameTextView.setText(name);

                RatingBar gatewayRating = (RatingBar) rootView.findViewById(R.id.gateway_rating_bar);
                gatewayRating.setRating(Float.parseFloat(rating));

                TextView transactionFeeTextView = (TextView) rootView.findViewById(R.id.gateway_transfee_textview);
                transactionFeeTextView.setText("Transaction fee: "+transaction_fees);

                TextView brandingTextView = (TextView) rootView.findViewById(R.id.gateway_branding_textview);
                if(Integer.parseInt(branding)==1){
                    brandingTextView.setText("Branding: YES!");
                }
                else{
                    brandingTextView.setText("Branding:     NO!");
                }


                TextView descriptionTextView  = (TextView) rootView.findViewById(R.id.gateway_description_textview);
                descriptionTextView.setText(description);

                ImageView logoView = (ImageView) rootView.findViewById(R.id.gateway_logo_view);

                Picasso.with(getContext()).load(image).into(logoView);

                TextView gatewayCurrenciesTextView = (TextView) rootView.findViewById(R.id.gateway_currencies_textview);
                gatewayCurrenciesTextView.setText("Supported Currencies: "+currencies);

                Button downloadDocButton = (Button) rootView.findViewById(R.id.downloadDocButton);
                downloadDocButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleDocumentDownload();
                    }
                });


            }
        }

        return  rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("GatewayParcel", gateway);
        super.onSaveInstanceState(outState);
    }


    public void handleDocumentDownload(){
        downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(how_to_doc);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setTitle(name).setDescription("Downloading info docs for "+name);

        request.setDestinationInExternalFilesDir(getContext(), Environment.DIRECTORY_DOWNLOADS,name +".pdf");
        request.setVisibleInDownloadsUi(true);

        downloadReference =downloadManager.enqueue(request);
        
    }

    @Override
    public void onResume() {
        super.onResume();

        // Notifications broadcast receiver

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        receiverNotificationClicked  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId= DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;

                long[] references = intent.getLongArrayExtra(extraId);

                for (long reference : references){
                    if(reference == downloadReference){

                    }
                }
            }
        };
        getContext().registerReceiver(receiverNotificationClicked,filter);


        // download broadcast receiver

        IntentFilter intentfilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        receiverDownloadComplete  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID , -1);

                if( downloadReference == reference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();

                    // get status of download

                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                    int status = cursor.getInt(columnIndex);

                    int fileNameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);

                    String savedFilePath = cursor.getString(fileNameIndex);

                    // get reason -- more detail on the status

                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                    int reason = cursor.getInt(columnReason);
                }
            }
        };
        getContext().registerReceiver(receiverNotificationClicked,filter);

    }
}
