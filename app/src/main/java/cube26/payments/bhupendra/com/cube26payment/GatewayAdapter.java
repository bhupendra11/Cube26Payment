package cube26.payments.bhupendra.com.cube26payment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by  Bhupendra Shekhawat on 14/12/15.
 */
public class GatewayAdapter extends ArrayAdapter<Gateway>
    {
        private static final String LOG_TAG = GatewayAdapter.class.getSimpleName();

        public GatewayAdapter(Activity context, List<Gateway> gatewayList){

            super(context,0, gatewayList);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           Gateway gateway= getItem(position);

            String gatewayName = gateway.name;
            String gatewayHowToDoc = gateway.how_to_doc;


            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.gateway_item, parent, false);
            }

            TextView gatewayView= (TextView) convertView.findViewById(R.id.gateway_name_textview);
            gatewayView.setText(gatewayName);




            return convertView;
        }


    }
