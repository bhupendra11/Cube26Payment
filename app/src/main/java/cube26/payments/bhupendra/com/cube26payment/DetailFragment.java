package cube26.payments.bhupendra.com.cube26payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



            }
        }

        return  rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("GatewayParcel", gateway);
        super.onSaveInstanceState(outState);
    }
}
