package cube26.payments.bhupendra.com.cube26payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bhupendra Shekhawat on 13/03/16.
 */
public class Gateway implements Parcelable{
    String name;
    String image;
    String description;
    String branding;
    String rating;
    String currencies;
    String setup_fee;
    String transaction_fees;
    String how_to_doc;


    public Gateway(String name, String image, String description, String branding, String rating, String currencies,String setup_fee , String transaction_fees , String how_to_doc ){
        this.name =  name;
        this.image = image;
        this.description= description;
        this.branding =  branding;
        this.rating = rating;
        this.currencies = currencies;
        this.setup_fee = setup_fee;
        this.transaction_fees = transaction_fees;
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
        transaction_fees =in.readString();
        how_to_doc =in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // FOr sorting
    public float getRating(){
        return Float.parseFloat(this.rating);
    }

    public float getSetupFee(){
        return Float.parseFloat(this.setup_fee);
    }


    // Used in the searchView to search for the payment gateway name and the available currencies
    @Override
    public String toString() {
        return name + " " +currencies;
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
        parcel.writeString(transaction_fees);
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
