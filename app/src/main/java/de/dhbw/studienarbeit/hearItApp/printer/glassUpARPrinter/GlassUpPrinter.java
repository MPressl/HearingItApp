package de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.printer.AbstractPrinter;
import glassup.service.GlassUpAgentInterface;
import glassup.service.GlassUpEvent;

/**
 * Printer for the GlassUp AR device
 */
public class GlassUpPrinter extends AbstractPrinter {


    /** Layouts supported by the glassUp AR device **/
    private final int lTextOnly = 0;

    private final int lGraphsOne = 1;

    private final int lTextRightSide = 2;

    private final int lTextRightSideHeading = 3;

    private final int lGraphsFour =4;

    public int CONTENT_ID = 0;

    /** Connector to the AR device **/
    public static GlassUpAgentVersionSupport glassAgent;

    private ConfigurationHandle configHandler;

    private MainActivity mainView;

    private List<String> linesToPrint;

    /**
     * Constructor
     */
    public GlassUpPrinter(MainActivity activity) {
        super();
        this.mainView = activity;
        this.linesToPrint = Collections.synchronizedList(
                new ArrayList<String>());
        //GlassUp Agent
        this.glassAgent = new GlassUpAgentVersionSupport();
        this.glassAgent.onCreate(mainView);
        this.glassAgent.setEventListener(new GlassUpAgentInterface.EventListener() {
            @Override
            public void onButtonEvent(GlassUpEvent glassUpEvent, int i) {}
            @Override
            public void onEvent(GlassUpEvent glassUpEvent) {}
        });

        this.glassAgent.setContentResultListener(new GlassUpAgentInterface.ContentResultListener() {
            @Override
            public void onContentResult(int iContent, int iStatus, String sMessage){}
        });

  /*      glassAgent.setConnectionListener(new GlassUpAgentInterface.ConnectionListener() {
            @Override
            public void onConnectionChanged(int connectionStatus) {
                if (connectionStatus == GlassUpServiceInterface.CONNECTION_STATUS_CONNECTED)
                {
                    isGlassUpConnected = true;
                }
                else
                {
                    isGlassUpConnected = false;
                }
            }
        });
    */
        //configurate glass up
        this.configHandler = new ConfigurationHandle(glassAgent);

        if(!this.glassAgent.isConfigured()){
            /*Not configured*/
            Log.d("TAG","App not configured, Scheduling send configure");
            /*Send the configuration message*/
            this.configHandler.sendEmptyMessage(1);
        }else{
            /*Already configured*/
            Log.d(MainActivity.LOG_TAF,"App Already configured");
            Toast.makeText(this.mainView.getApplicationContext(), "App already configured", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void startPrinting(){
        super.startPrinting();
        this.linesToPrint.clear();
        new Thread("Send-Lines-To-GlassUp Thread"){
            @Override
            public void run(){
                sendLinesToGlassUp();
            }
        }.start();
    }

    /**
     * method waits for new lines and prints them to the AR device
     */
    private void sendLinesToGlassUp() {
        while(this.isProcessing || linesToPrint.size() > 0){
            if(this.linesToPrint.size() == 0 ){
                continue;
            }
            String display = "";
            for(int i = 0 ; i < linesToPrint.size() && i < 6 ; ++i){
                display += linesToPrint.get(i) + " ";
            }
            this.glassAgent.sendContent(this.CONTENT_ID, this.lTextOnly, null, new String[]{display});
            linesToPrint.remove(0);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void shutdown() {
        this.glassAgent.onDestroy();
    }

    /**
     * Splits the message into lines and adds them to the line queue
     * @param message
     * @return
     */
    @Override
    protected boolean printMessage(String message){
        return this.addMessageToLineList(message);
    }

    public void destroy() {
        this.glassAgent.onDestroy();
    }

    public void onPause(){
        this.glassAgent.onPause();
    }

    public void onResume(){
        this.glassAgent.onResume();
    }


    /**
     * method takes a message and splits it in lines of 16 chars
     * to be printed to the AR device. The lines are added to this.linesToPrint
     * @param messageBuffer
     */
    private boolean addMessageToLineList(String messageBuffer){
        int counter = 0;

        if(messageBuffer == null){
            return false;
        }

        while(counter < messageBuffer.length()) {

            if (messageBuffer.length() - (counter + 17) <= 0) {
                //rest of buffer is smaller than one line, -> prepare buffer and send
                //then break
                this.linesToPrint.add(messageBuffer.substring(counter));
                break;
            }
            //after 17 signs there is a space --> perfect line
            if (messageBuffer.charAt(counter + 17) == ' ') {
                this.linesToPrint.add(messageBuffer.substring(counter, counter + 18));
                counter += 18;
            } else {
                //check next ' ' before 17
                boolean foundSpace = false;

                for (int i = counter + 17; i > counter; i--) {
                    //space found?
                    if (messageBuffer.charAt(i) == ' ') {
                        this.linesToPrint.add(messageBuffer.substring(counter, i+1));
                        counter = i + 1;
                        foundSpace = true;
                        break;
                    }
                }

                //check if a space was found in the line
                if (!foundSpace) {
                    //if no space in whole line just break on letter 17
                    this.linesToPrint.add(messageBuffer.substring(counter, counter+17));
                    counter += 17;
                }
            }
        }
        return true;
    }
}
