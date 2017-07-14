//Andi start
package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;


/**
 * ConnectionCheck checks the availability of an internet-connection and its strength
 * in regularly time gaps
 *
 * created by Andreas
 */

public class ConnectionCheck implements Runnable {

    private IRecorder recorder;
    private ConnectivityManager cm;

    private final String CONNECTION_STRENGTH_STRING = "Connection Strength: ";

    private String connection_strentgh_actual;


    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;

    private TextView label_internet_connection;

    public ConnectionCheck(IRecorder recorder) {
        this.recorder = recorder;
        if(recorder == null){
            Log.e(MainActivity.LOG_TAG, "Cannot do connection checks. given recorder is null.");
        }
        this.label_internet_connection = (TextView) this.recorder.getMainView().findViewById(R.id.label_internet_connection);
        cm = (ConnectivityManager) this.recorder.getMainView().getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * run()
     * is an overwritten method, that runs a while-loop, that regularly checks the
     * internet connection and updates the internet connection label and
     * the connection status of the main activity
     */
    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()) {
            this.recorder.getMainView().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewGroup.LayoutParams params = label_internet_connection.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    label_internet_connection.setLayoutParams(params);
                }
            });

            this.setTextLabelInternetConnection("");

            if(evaluateNetworkConnection())
            {
                this.setTextLabelInternetConnection(this.connection_strentgh_actual
                        + " Streaming Speech reached");
                this.recorder.getMainView().updateConnectionStatus(this.recorder, true);
            }else{
                this.setTextLabelInternetConnection(this.connection_strentgh_actual
                        + " to low for speech streaming");
                if(this.recorder.isRecording()){
                    this.recorder.stopRecording();
                }
                this.recorder.getMainView().updateConnectionStatus(this.recorder, false);
            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.setTextLabelInternetConnection("");
        this.recorder.getMainView().updateConnectionStatus(this.recorder, true);

    }

    /**
     * setTextLabelInternetConnection()
     * updates the text of the internet-connection label
     */
    private void setTextLabelInternetConnection (final String internetConnectionInfo){
        this.recorder.getMainView().runOnUiThread(new Runnable() {
            @Override
            public void run() {label_internet_connection.setText(internetConnectionInfo);
            }
        });
    }

    /**
     * evaluateNetworkConnection()
     * detects the network information status and returns the belonging internet speed
     */
    public boolean evaluateNetworkConnection() {
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni == null){
            //no internet connection
            this.connection_strentgh_actual = "No Internet Connection";
            return false;
        }
        if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
            this.connection_strentgh_actual = "Internet Connection: WIFI";
            return true;
        }
        if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (ni.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    this.connection_strentgh_actual =this.CONNECTION_STRENGTH_STRING + "50-100 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    this.connection_strentgh_actual =this.CONNECTION_STRENGTH_STRING +"14-64 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    this.connection_strentgh_actual =this.CONNECTION_STRENGTH_STRING +"50-100 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    this.connection_strentgh_actual =this.CONNECTION_STRENGTH_STRING + "400-1000 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING +"600-1400 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "100 kbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "2-14 Mbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "0.7-1.7 Mbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "1-23 Mbps";
                    return false;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "0.4-7 Mbps";
                    return false;
                case ConnectionCheck.NETWORK_TYPE_EHRPD:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "1-2 Mbps";
                    return false;
                case ConnectionCheck.NETWORK_TYPE_EVDO_B:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "5Mbps";
                    return false;
                case ConnectionCheck.NETWORK_TYPE_HSPAP:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "10-20 Mbps";
                    return false;
                case ConnectionCheck.NETWORK_TYPE_IDEN:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "25kbps";
                return false;
                case ConnectionCheck.NETWORK_TYPE_LTE:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "10+ Mbps";
                    return true;
                default:
                    this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "NETWORK TYPE UNKNOWN";
                    return false;
            }
        }
        this.connection_strentgh_actual = this.CONNECTION_STRENGTH_STRING + "CONNECTION TYPE UNKNOWN";
        return false;
    }
}
//Andi end