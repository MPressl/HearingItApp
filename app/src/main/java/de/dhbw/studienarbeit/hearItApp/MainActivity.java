package de.dhbw.studienarbeit.hearItApp;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import de.dhbw.studienarbeit.hearItApp.soundAnimation.SoundAnimationView;
import de.dhbw.studienarbeit.hearItApp.printer.AbstractPrinter;
import de.dhbw.studienarbeit.hearItApp.printer.PrinterFactory;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.RecorderFactory;

import static com.google.android.gms.analytics.internal.zzy.v;

/**
 * Main Activity controlling the user interaction. Selecting a recorder and printer
 * and setting other options is controlled from here
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String APP_NAME = "Hearing";
    public static final String LOG_TAG = "HearItApp";

    private int RECORD_MODE;
    private int PRINTER_MODE;

    private boolean isRecording;

    private AbstractPrinter arPrinter;
    private IRecorder recorder;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;;
    private NavigationView navigationMenu;

    private int languageId = Constants.LANGUAGE_GERMAN;

    private SoundAnimationView soundAnimationView;

    private TextView txtViewRecInfo;
    private EditText editPrinter;
    private ImageButton btnSpeech;
    private Spinner spinner_recorder;
    private Spinner spinner_printer;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize_Components();
    }
    /**
     * initializing gui components
     * @return success
     */
    private boolean initialize_Components(){

        generateNavMenu();

        editPrinter = ((EditText) findViewById(R.id.edit_printer));
        ((TextView)findViewById(R.id.label_text_printer)).setVisibility(View.INVISIBLE);

        ((EditText) findViewById(R.id.edit_recorder)).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.label_text_recorder)).setVisibility(View.INVISIBLE);

        txtViewRecInfo = (TextView)findViewById(R.id.label_recording_information);
        this.txtViewRecInfo.setText("Start Speech Recognition!");

        this.spinner_printer = (Spinner) findViewById(R.id.spinner_printer);
        final String[] printerArray = Constants.PRINTER_MAP.keySet().toArray(
                new String[Constants.PRINTER_MAP.keySet().size()]);

        ArrayAdapter adapt_printer = new ArrayAdapter(
                this,R.layout.spinner_item,  printerArray);

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
                this,R.layout.spinner_item,  recorderArray);
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
        this.btnSpeech = (ImageButton) findViewById(R.id.btnStartSpeech);
        this.btnSpeech.setOnClickListener(this);

        this.soundAnimationView = (SoundAnimationView) findViewById(R.id.sound_animation_view);

        return true;
    }

    /**
     * generates the side menu
     */
    /*private void generate_Menu(){
        final String[] menuItems = {"language..."};
        this.lstVSideMenu = (ListView) findViewById(R.id.navList);
        this.adaptMenu =  new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuItems);
        this.lstVSideMenu.setAdapter(adaptMenu);
        lstVSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("hallo");
            }
        });
    }*/


    private void generateNavMenu(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav_menu, R.string.close_nav_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationMenu = (NavigationView) findViewById(R.id.navigation_view);
        navigationMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if(!isRecording) {
                    new LanguageDialoge().onCreateDialog(getIntent().getExtras(), MainActivity.this).show();
                }
                else {
                    showToast("Cant switch language while recording!");
                }
                return false;
            }
        });



        /*languageSelectionItem = (MenuItem) findViewById(R.id.language_selection);
        languageSelectionItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                System.out.println("Hallo");
                return false;
            }
        });
        navigationMenu = (NavigationView) findViewById(R.id.navigation_view);*/
    }

    public void createSoundAnimationView(){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            Log.e(LOG_TAG, "Cannot change recorder while recording. Stop the record first.");
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
            Log.i(LOG_TAG, "Selected recorder: id: " + RECORD_MODE +
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
            Log.e(LOG_TAG, "Cannot change printer while recording. Stop the record first.");
            return;
        }
        this.PRINTER_MODE = mode;
        if(arPrinter != null) {
            this.arPrinter.shutdown();
        }
        this.arPrinter = PrinterFactory.generate(mode, this);
        Log.i(LOG_TAG, "Selected printer: id: " + PRINTER_MODE +
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
                }else{
                    this.showToast("couldn't parse speech to text");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Error while sleeping");
                }
                this.notifyStopRecord();
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
                        this.startRecord();
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

    public void startRecord() {
        this.isRecording = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                upDateView();
            }
        });
        this.soundAnimationView.startDrawingThread();
        this.arPrinter.startPrinting();
        this.recorder.startRecording();
    }

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
                   upDateView();
                }
            });
            this.recorder.stopRecording();
            this.soundAnimationView.stopDrawingThread();
            this.arPrinter.stopPrinting();
            return true;
        }
        return false;
    }

    private void upDateView() {
        if(isRecording){
            this.btnSpeech.setBackgroundResource(R.drawable.mic_start_recording_recording_circle);
            this.txtViewRecInfo.setText("Recording...");
            this.txtViewRecInfo.setTextColor(ContextCompat.getColor(
                    getApplicationContext(), R.color.colorPrimaryDark));
            this.spinner_recorder.setEnabled(false);
            this.spinner_printer.setEnabled(false);
        }else{
            this.btnSpeech.setBackgroundResource(R.drawable.mic_start_recording_not_recording_circle);
            this.txtViewRecInfo.setText("Start Speech Recognition!");
            this.txtViewRecInfo.setTextColor(ContextCompat.getColor(
                    getApplicationContext(), R.color.colorPrimary));
            this.spinner_recorder.setEnabled(true);
            this.spinner_printer.setEnabled(true);
        }
    }

    public void showSoundAnimation(short[] audioInput) {
        //show canvas to animate incoming audio
        int scalingValue = calculatePowerDb(audioInput, 0, audioInput.length)+60; // Loud Voice = -10, quiet Voice = -60
        if(scalingValue<0) scalingValue= -scalingValue;
        if(scalingValue>200) scalingValue = 0;//to prevent negative values
        this.soundAnimationView.setScalingValue( scalingValue );


        //loop through shortBuffer and calculate the db valueos for each short value (sample)

        //animate a canvas depending on each single db value
    }



    public int calculatePowerDb(short[] sdata, int off, int samples)
    {
       float max_16_bit = 32768;
        final float fudge = 0.6f;
        double sum = 0;
        double sqsum = 0;
        for (int i = 0; i < samples; i++)
        {
            final long v = sdata[off + i];
            sum += v;
            sqsum += v * v;
        }
        double power = (sqsum - sum * sum / samples) / samples;

        power /= max_16_bit * max_16_bit;

        double result = Math.log10(power) * 10f + fudge;

        return (int)result;
    }

    //Andi start
    public void setLanguageId(int languageId){
        this.languageId = languageId;
        showToast("Language-ID: " + Integer.toString(languageId));
    }
    //Andi end
    public String getSpokenLanguage() {
        switch(this.languageId){
            case Constants.LANGUAGE_ENGLISH:
                return "en-US";
            case Constants.LANGUAGE_FRANCE:
                return "fr-FR";
            case Constants.LANGUAGE_GERMAN:
                return "de-DE";
            default:
                return "es-ES";
        }
    }

    public ImageButton getSpeechBtn() {
        return btnSpeech;
    }
}
