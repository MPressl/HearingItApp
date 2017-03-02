package de.dhbw.studienarbeit.hearItApp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.HearItApp.R;

import java.util.ArrayList;

import de.dhbw.studienarbeit.hearItApp.printer.IPrinter;
import de.dhbw.studienarbeit.hearItApp.printer.PrinterFactory;
import de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter.GlassUpPrinter;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.RecorderFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String APP_NAME = "Hearing";
    public static final String LOG_TAF = "HearItApp";
    public static final int RESULT_SPEECH = 1;

    /** Factory options for speech recognition **/
    public static final int GOOGLE_CLOUD_CLIENT = 0;
    public static final int ANDROID_VOICE_CLIENT = 1;
    public static final int TEXT_FIELD_INPUT = 999;

    /** Factory options for AR printer variants **/
    public static final int GLASSUP_AR_PRINTER = 0;

    private static final int RECORD_MODE = GOOGLE_CLOUD_CLIENT;
    private static final int PRINTER_MODE = GLASSUP_AR_PRINTER;

    private static final int REQUEST_APP_PERMISSIONS = 100;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private boolean isRecording;

    private IPrinter arPrinter;
    private GlassUpPrinter gPrinter;
    private IRecorder recorder;

    private ListView lstVSideMenu;
    private ArrayAdapter<String> adaptMenu;

    private Button btnSpeech;
    private TextView txt;
    private EditText txtField;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //request permissions
        ActivityCompat.requestPermissions(this, permissions, REQUEST_APP_PERMISSIONS);
        initialize_Components();
    }
    /**
     * initializing gui components, including GUI, AR Printer and SpeechRecognition Client
     * @return success
     */
    private boolean initialize_Components(){

        generate_Menu();
        // this.gPrinter = new GlassUpPrinter(this);
        this.arPrinter = PrinterFactory.generate(MainActivity.PRINTER_MODE, this);
        this.recorder = RecorderFactory.generate(MainActivity.RECORD_MODE, this);
        this.txtField = (EditText) findViewById(R.id.editText);
        this.txtField.setVisibility(View.INVISIBLE);

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
        this.lstVSideMenu = (ListView) findViewById(R.id.navList);
        this.adaptMenu =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        this.lstVSideMenu.setAdapter(adaptMenu);
    }

   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
       switch(requestCode){
           case REQUEST_APP_PERMISSIONS:
               if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                       grantResults[1] == PackageManager.PERMISSION_GRANTED){

               }else{
                   Toast.makeText(this, "Permissions not grante", Toast.LENGTH_LONG);
               }

       }
   }
    @Override
    public void onClick(View view) {
            if(this.arPrinter == null ){
                this.showToast("Could not find a device to print");
                return;
            }
            switch(view.getId()){
                case R.id.btnStartSpeech:
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
        this.txt.setText(result.get(0));
        this.arPrinter.printMessage(result.get(0));
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

    public EditText getTextField(){return this.txtField;}

    public Button getSpeechBtn(){return this.btnSpeech;}


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
