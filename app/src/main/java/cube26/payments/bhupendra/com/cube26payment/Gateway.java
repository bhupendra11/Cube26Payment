package cube26.payments.bhupendra.com.cube26payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bhupendra Shekhawat on 14/12/15.
 */
public class Gateway implements Parcelable{
    String name;
    String image;
    String description;
    String branding;
    String rating;
    String currencies;
    String setup_fee;
    String transaction_fee;
    String how_to_doc;


    public Gateway(String name, String image, String description, String branding, String rating, String currencies,String setup_fee , String transaction_fee , String how_to_doc ){
        this.name =  name;
        this.image = image;
        this.description= description;
        this.branding =  branding;
        this.rating = rating;
        this.currencies = currencies;
        this.setup_fee = setup_fee;
        this.transaction_fee = transaction_fee;
        this.how_to_doc = how_to_doc;
    }

    private Gateway(Parcel in){
        name = in.readString();
        image = in.readString();
        description =in.readString();
        branding =in.readString();
        rating = in.readString() ;
        currencies = in.readString();
        setup_fee = in.readString();
        transaction_fee =in.readString();
        how_to_doc =in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeString(description);
        parcel.writeString(branding);
        parcel.writeString(rating);
        parcel.writeString(currencies);
        parcel.writeString(setup_fee);
        parcel.writeString(transaction_fee);
        parcel.writeString(how_to_doc);

    }

    public static final Creator<Gateway> CREATOR  = new Creator<Gateway>()
    {

        @Override
        public Gateway createFromParcel(Parcel parcel) {
            return new Gateway(parcel);
        }

        @Override
        public Gateway[] newArray(int i) {
            return new Gateway[i];
        }
    };


}
