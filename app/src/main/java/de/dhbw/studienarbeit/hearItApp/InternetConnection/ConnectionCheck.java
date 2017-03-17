//Andi start
package de.dhbw.studienarbeit.hearItApp.InternetConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;

/**
 * Created by Andi on 15.03.2017.
 */

public class ConnectionCheck implements Runnable {

    private MainActivity parent;

    private NetworkInfo ni;
    private ConnectivityManager cm;

    private String connectionStrength;


    public static final int NETWORK_TYPE_EHRPD = 14; // Level 11            //different NETWORK_TYPES with different connection strength, dates from Internet without guarantee
    public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
    public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
    public static final int NETWORK_TYPE_IDEN = 11; // Level 8
    public static final int NETWORK_TYPE_LTE = 13; // Level 11

    private TextView label_internet_connection;

    public ConnectionCheck(MainActivity parent){
        this.parent = parent;
        this.label_internet_connection = (TextView) this.parent.findViewById(R.id.label_internet_connection);
        cm = (ConnectivityManager) parent.getSystemService(Context.CONNECTIVITY_SERVICE);
                                                                 //Thread Started from here

    }

    @Override
    public void run() {

        String label_text = this.label_internet_connection.getText().toString();

        boolean isTrueEntered = false;                                                                          //Two helpvariables to not set Text of label_internet_connection every
        boolean isFalseEntered = false;                                                                         //Time the "while"-loop runs through

        if(isConnectedToInternet()){connectionStrength = getConnectionStrength();}
        else {connectionStrength = "No Connection!";}

        while(true) {
            if (isConnectedToInternet()) {
               // isFalseEntered = false;
               // if(isTrueEntered == false)
                    setTextLabelInternetConnection(label_text + " yes " + connectionStrength);
              //  isTrueEntered = true;

            } else {
             //   isTrueEntered = false;
                //if(isFalseEntered == false)
                setTextLabelInternetConnection(label_text + " no");
             //   isFalseEntered = true;

            }

            if(isConnectedToInternet() && !connectionStrength.equals(getConnectionStrength())) //If connection strength is changing we need to set upadte the Text isFalseEntered == false &&
            {
                connectionStrength = getConnectionStrength();
                setTextLabelInternetConnection(label_text + " yes " + connectionStrength);
            }

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isConnectedToInternet(){                                                        //Check if there is an internet connection


        this.ni = cm.getActiveNetworkInfo();

        if (ni == null) {
            return false;
        } else
            return true;

    }

    private void setTextLabelInternetConnection (final String internetConnectionInfo){      //this function is necessary to set the Text of label_internet_connection by the Mainthread
        parent.runOnUiThread(new Runnable() {                                               //Otherwise there was an Fatal Error Thread-31121
            @Override
            public void run() {
                label_internet_connection.setText(internetConnectionInfo);
            }
        });
    }

    public String getConnectionStrength() {                     //Connections Type = WIFI or MOBILE
        if (this.ni.getType() == ConnectivityManager.TYPE_WIFI) {                                        //Connections Subtype is more detailed
            return "WIFI";
        } else if (this.ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (this.ni.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:                                   //dates with no guarantee
                    return "50-100 kbps";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "14-64 kbps";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "50-100 kbps";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "400-1000 kbps";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "600-1400 kbps"; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "100 kbps";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "2-14 Mbps";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "0.7-1.7 Mbps";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "1-23 Mbps"; // ~ 1-23
                // Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "0.4-7 Mbps"; // ~ 400-7000
                // kbps
                // NOT AVAILABLE YET IN API LEVEL 7
                case ConnectionCheck.NETWORK_TYPE_EHRPD:
                    return "1-2 Mbps"; // ~ 1-2 Mbps
                case ConnectionCheck.NETWORK_TYPE_EVDO_B:
                    return "5Mbps"; // ~ 5 Mbps
                case ConnectionCheck.NETWORK_TYPE_HSPAP:
                    return "10-20 Mbps"; // ~ 10-20
                // Mbps
                case ConnectionCheck.NETWORK_TYPE_IDEN:
                    return "25kbps"; // ~25 kbps
                case ConnectionCheck.NETWORK_TYPE_LTE:
                    return "10+ Mbps"; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "NETWORK TYPE UNKNOWN";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }
}
//Andi end