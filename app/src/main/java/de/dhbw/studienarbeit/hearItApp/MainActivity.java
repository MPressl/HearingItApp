package de.dhbw.studienarbeit.hearItApp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import de.dhbw.studienarbeit.hearItApp.printer.AbstractPrinter;
import java.util.ArrayList;

import de.dhbw.studienarbeit.hearItApp.InternetConnection.ConnectionCheck;
import de.dhbw.studienarbeit.hearItApp.printer.PrinterFactory;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.RecorderFactory;

/**
 * Main Activity controlling the user interaction. Selecting a recorder and printer
 * and setting other options is controlled from here
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String APP_NAME = "Hearing";
    public static final String LOG_TAF = "HearItApp";


    private int RECORD_MODE;
    private int PRINTER_MODE;

    private TextView label_internet_connection;
    private ConnectionCheck connectionCheck;

    private boolean isRecording;

    private AbstractPrinter arPrinter;
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
        initialize_Components();
        startConnectionChecks();


    }
    /**
     * initializing gui components
     * @return success
     */
    private boolean initialize_Components(){

        generate_Menu();

        ((EditText) findViewById(R.id.edit_printer)).setVisibility(View.INVISIBLE);
        ((TextView)findViewById(R.id.label_text_printer)).setVisibility(View.INVISIBLE);

        ((EditText) findViewById(R.id.edit_recorder)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.label_text_recorder)).setVisibility(View.INVISIBLE);

        //Andi start
        label_internet_connection = ((TextView)findViewById(R.id.label_internet_connection));
        //Andi end

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //init the spinner to select a recorder. take entries from map in Constants
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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //button to start recording
        this.btnSpeech = (Button) findViewById(R.id.btnStartSpeech);
        this.btnSpeech.setOnClickListener(this);

        return true;
    }

    /**
     * generates the side menu
     */
    private void generate_Menu(){
        String[] menuItems = {"Language..."};
        this.lstVSideMenu = (ListView) findViewById(R.id.navList);
        this.adaptMenu =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        this.lstVSideMenu.setAdapter(adaptMenu);
    }

    /**
     * Select the recorder mode. This method is called when the user selects a recorder
     * through the spinner. The method calls the RecorderFactory. If the Recorder cannot be created
     * the standard textRecorder is selected
     *
     * @param mode
     * @param name
     */
    private void setRecorderMode(int mode, String name){

        if(this.isRecording){
            //if recording at the moment the recorder cannot be changed
            Log.e(LOG_TAF, "Cannot change recorder while recording. Stop the record first.");
            return;
        }
        this.RECORD_MODE = mode;

        if(recorder != null) {
            this.recorder.shutdown();
        }

        this.recorder = RecorderFactory.generate(mode, this);

        if(this.recorder == null) {
            //if null is record (because recorder cannot be initialized, the standard text recorder
            // is selected
            String[] recorders = Constants.RECORDER_MAP.keySet()
                    .toArray(new String[Constants.RECORDER_MAP.keySet().size()]);
            int textFieldIndex = 0;
            for(int i = 0 ; i < recorders.length ; ++i){
                //get the index of the text recorder within the array
                if(recorders[i].equals(Constants.RECORDER_TEXT_FIELD_CLIENT_TEXT)){
                    textFieldIndex = i;
                    break;
                }
            }
            this.spinner_recorder.setSelection(textFieldIndex);

        }else {
            Log.i(LOG_TAF, "Selected recorder: id: " + RECORD_MODE +
                    " Name: " + name);
        }
    }

    /**
     * Select the printer mode. This method is called when the user selects a printer
     * through the spinner. The method calls the PrinterFactory.
     *
     * @param mode
     * @param name
     */
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
            //Result of android recognition intent requested by AndroidVoiceRecorder
            case Constants.RESULT_SPEECH:

                if(resultCode == RESULT_OK && data != null){
                    this.receiveResult(data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                    this.isRecording = false;
                    this.arPrinter.stopPrinting();
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
    public void onClick(View view) {
            if(this.arPrinter == null ){
                this.showToast("Please select a printer first.");
                return;
            }
            if(this.recorder == null){
                this.showToast("Please select a recorder first.");
                return;
            }
            switch(view.getId()){
                case R.id.btnStartSpeech:
                    if(this.isRecording){
                        this.notifyStopRecord();
                    }else {
                        this.isRecording = true;
                        this.recorder.startRecording();
                        this.arPrinter.startPrinting();
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
    }
    @Override
    protected  void onPause(){
        super.onPause();
    }
    @Override
    protected void onResume(){
        super.onResume();
       // this.gPrinter.onResume();
    }

    /**
     * method calls the selected printer to print out the received result
     *
     * @param result
     */
    public void receiveResult(String result){
        this.arPrinter.addToMessageBuffer(result);
    }

    /**
     * method displays a Toast message to the user
     */
    public void showToast(String msg){
        final String message = msg;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast can only be shown from UI Thread
                Toast.makeText(getApplicationContext(),
                        message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * getter for the start and stop speech button
     * @return start/stop speech button
     */
    public Button getSpeechBtn(){return this.btnSpeech;}

    /**
     * method is called when the stopp button is pressed
     * and also when the stopRecording method of the recorder is called from the
     * conversion client to notify this activity
     * if the stopRecording method of the recorder is called from here, then this.isRecording
     * is already false and the method will return without doing anything
     */
    public boolean notifyStopRecord() {
        if(this.isRecording) {
            this.isRecording = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnSpeech.setText("Start Speech Recording");
                }
            });
            this.recorder.stopRecording();
            this.arPrinter.stopPrinting();
            return true;
        }
        return false;
    }
}
    //Andi start
    public void startConnectionChecks(){
        this.connectionCheck = new ConnectionCheck(this);
        new Thread(this.connectionCheck).start();
    }
    //Andi end
}
