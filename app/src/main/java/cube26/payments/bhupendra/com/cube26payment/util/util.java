package cube26.payments.bhupendra.com.cube26payment.util;

import java.util.Comparator;

import cube26.payments.bhupendra.com.cube26payment.Gateway;

/**
 * Created by root on 13/3/16.
 */

public class util {

// Comparator classes

 public    static class RatingComparator implements Comparator<Gateway> {
        public int compare(Gateway object1, Gateway object2) {
            if (object1.getRating() < object2.getRating()) return 1;
            else if (object1.getRating() > object2.getRating()) return -1;
            else return 0;
        }
    }

  public  static class SetupFeeComparator implements Comparator<Gateway> {
        public int compare(Gateway object1, Gateway object2) {
            if (object1.getSetupFee() > object2.getSetupFee()) return 1;
            else if (object1.getSetupFee() < object2.getSetupFee()) return -1;
            else return 0;

        }
    }

}