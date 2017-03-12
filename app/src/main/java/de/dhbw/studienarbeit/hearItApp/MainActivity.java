package de.dhbw.studienarbeit.hearItApp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.dhbw.studienarbeit.hearItApp.printer.IPrinter;
import de.dhbw.studienarbeit.hearItApp.printer.PrinterFactory;
import de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter.GlassUpPrinter;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.RecorderFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String APP_NAME = "Hearing";
    public static final String LOG_TAF = "HearItApp";


    private int RECORD_MODE;
    private int PRINTER_MODE;

    private final String[] permissions = {Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private boolean isRecording;

    private IPrinter arPrinter;
    private IRecorder recorder;

    private ListView lstVSideMenu;
    private ArrayAdapter<String> adaptMenu;

    private Button btnSpeech;
    private Spinner spinner_recorder;
    private Spinner spinner_printer;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //request permissions
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_APP_PERMISSIONS);
        initialize_Components();
    }
    /**
     * initializing gui components, including GUI, AR Printer and SpeechRecognition Client
     * @return success
     */
    private boolean initialize_Components(){

        generate_Menu();

        ((EditText) findViewById(R.id.edit_printer)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.label_text_printer)).setVisibility(View.INVISIBLE);

        ((EditText) findViewById(R.id.edit_recorder)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.label_text_recorder)).setVisibility(View.INVISIBLE);

        this.spinner_printer = (Spinner) findViewById(R.id.spinner_printer);

        final String[] printerArray = Constants.PRINTER_MAP.keySet().toArray(
                new String[Constants.PRINTER_MAP.keySet().size()]);

        ArrayAdapter adapt_printer = new ArrayAdapter(
                this,android.R.layout.simple_spinner_item,  printerArray);

        this.spinner_printer.setAdapter(adapt_printer);

        this.spinner_printer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                int mode = Constants.PRINTER_MAP.get(selected);
                setPrinterMode( mode, selected );

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.spinner_recorder = (Spinner) findViewById(R.id.spinner_recorder);
        final String[] recorderArray = Constants.RECORDER_MAP.keySet().toArray(
                new String[Constants.RECORDER_MAP.keySet().size()]);
        ArrayAdapter adapt_recorder = new ArrayAdapter(
                this,android.R.layout.simple_spinner_item,  recorderArray);
        this.spinner_recorder.setAdapter(adapt_recorder);
        this.spinner_recorder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setRecorderMode(Constants.RECORDER_MAP.get(parent.getItemAtPosition(position).toString()),
                        parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        this.setRecorderMode(Constants.RECORDER_MAP.get(
                Constants.RECORDER_TEXT_FIELD_CLIENT_TEXT), Constants.RECORDER_TEXT_FIELD_CLIENT_TEXT);

        this.setPrinterMode(
                Constants.PRINTER_MAP.get(Constants.PRINTER_GLASSUP_AR_TEXT),
                Constants.PRINTER_GLASSUP_AR_TEXT);

        return true;
    }
    //generates the side menu
    private void generate_Menu(){
        String[] menuItems = {"Language..."};
        this.lstVSideMenu = (ListView) findViewById(R.id.navList);
        this.adaptMenu =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        this.lstVSideMenu.setAdapter(adaptMenu);
    }

    private void setRecorderMode(int mode, String name){
        if(this.isRecording){
            Log.e(LOG_TAF, "Cannot change recorder while recording. Stop the record first.");
            return;
        }
        this.RECORD_MODE = mode;
        if(recorder != null) {
            this.recorder.shutdown();
        }
        this.recorder = RecorderFactory.generate(mode, this);
        Log.i(LOG_TAF, "Selected recorder: id: " + RECORD_MODE +
                " Name: " + name);
    }

    private void setPrinterMode(int mode, String name){
        if(this.isRecording){
            Log.e(LOG_TAF, "Cannot change printer while recording. Stop the record first.");
            return;
        }
        this.PRINTER_MODE = mode;
        if(arPrinter != null) {
            this.arPrinter.shutdown();
        }
        this.arPrinter = PrinterFactory.generate(mode, this);
        Log.i(LOG_TAF, "Selected printer: id: " + PRINTER_MODE +
                " Name: " + name);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case Constants.RESULT_SPEECH:

                if(resultCode == RESULT_OK && data != null){
                    this.receiveResult(data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));

                }else{
                    this.showToast("couldn't parse speech to text");
                }
                break;
            default:
                this.showToast("Intent request code unknown");
                break;
        }
    }

    @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
       switch(requestCode){
           case Constants.REQUEST_APP_PERMISSIONS:
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
       // this.arPrinter.destroy();

    }
    @Override
    protected  void onPause(){
        super.onPause();
      // this.arPrinter.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
       // this.gPrinter.onResume();
    }

    public void receiveResult(ArrayList<String> result){
        this.arPrinter.printMessage(result.get(0));
    }

    public void showToast(String msg){
        Toast t = Toast.makeText(this.getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT);
        t.show();
    }

   // public TextView getTxt(){
   //     return this.txt;
   // }

   // public EditText getTextField(){return this.edit_printer;}

    public Button getSpeechBtn(){return this.btnSpeech;}
}
