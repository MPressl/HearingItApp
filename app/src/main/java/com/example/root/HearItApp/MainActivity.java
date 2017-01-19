package com.example.root.HearItApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String APP_NAME = "Hearing";
    public static final int RESULT_SPEECH = 1;

    private static final int GOOGLE_CLOUD_CLIENT = 0;
    private static final int ANDROID_VOICE_CLIENT = 999;

    private static final int RECORD_MODE = GOOGLE_CLOUD_CLIENT;
    private boolean isRecording;

    private GlassUpPrinter gPrinter;
    private Recorder recorder;

    private ListView lstVSideMenu;
    private ArrayAdapter<String> adaptMenu;

    private Button btnSpeech;
    private TextView txt;
    private EditText txtField;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initialize_Components();
    }

    @Override
    public void onClick(View view) {
            if(gPrinter == null ){
                gPrinter = new GlassUpPrinter(this);
            }
            switch(view.getId()){
                case R.id.btnStartSpeech:
                   // sendAsMessageFlow(txtField.getText().toString());
                    if(this.isRecording){
                        this.isRecording = false;
                        recorder.stopRecording();
                    }else {
                        this.isRecording = true;
                        recorder.startRecording();
                    }

                    break;
                default:
                    showToast("unknown View");
                    break;
            }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        this.gPrinter.destroy();

    }
    @Override
    protected  void onPause(){
        super.onPause();
       this.gPrinter.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
       // this.gPrinter.onResume();
    }

    public void receiveResult(ArrayList<String> result){
        txt.setText(result.get(0));
        gPrinter.sendAsMessageFlow(result.get(0));

    }

    public GlassUpPrinter getPrinter(){
        return gPrinter;
    }

    public void showToast(String msg){
        Toast t = Toast.makeText(this.getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT);
        t.show();
    }

    public TextView getTxt(){
        return this.txt;
    }



//------------------PRIVATE


    /**
     * initializing components, including the glassUp AR Agent
     * @return success
     */
    private boolean initialize_Components(){

        generate_Menu();
       // this.gPrinter = new GlassUpPrinter(this);
        this.recorder = RecorderFactory.generate(RECORD_MODE, this);
        this.txtField = (EditText) findViewById(R.id.editText);
        //textview
        this.txt = (TextView) findViewById(R.id.textView);

        //button to start recording
        this.btnSpeech = (Button) findViewById(R.id.btnStartSpeech);
        this.btnSpeech.setOnClickListener(this);

        /*
        if(!glassAgent.isConnected()){
            showToast("Could not connect to Agent");
            return false;
        }
        if(!glassAgent.isConfigured()){
            showToast("Agent not configured");
        }
        */
        return true;
    }
    //generates the side menu
    private void generate_Menu(){
        String[] menuItems = {"Language..."};
        lstVSideMenu = (ListView) findViewById(R.id.navList);
        adaptMenu =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        lstVSideMenu.setAdapter(adaptMenu);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
    */
}
