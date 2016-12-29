package com.example.root.hearviaandroidspeech;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import glassup.service.GlassUpAgent;
import glassup.service.GlassUpAgentInterface;
import glassup.service.GlassUpEvent;

/**
 * Created by Martin on 12/15/16.
 */

public class GlassUpPrinter {


    private boolean isProcessing;

    private int lTextOnly = 0;

    private int lGraphsOne = 1;

    private int lTextRightSide = 2;

    private int lTextRightSideHeading = 3;

    private int lGraphsFour =4;

    public int CONTENT_ID = 0;

    public static GlassUpAgent glassAgent;

    private ConfigurationHandle configHandler;

    private MainActivity mainView;

    /**
     * Constructor
     */
    public GlassUpPrinter(MainActivity activity){
        mainView = activity;
        //GlassUp Agent
        glassAgent = new GlassUpAgent();
        glassAgent.onCreate(mainView);
        glassAgent.setEventListener(new GlassUpAgentInterface.EventListener() {
            @Override
            public void onButtonEvent(GlassUpEvent glassUpEvent, int i) {

            }

            @Override
            public void onEvent(GlassUpEvent glassUpEvent) {

            }
        });
        glassAgent.setContentResultListener(new GlassUpAgentInterface.ContentResultListener() {
            @Override
            public void onContentResult(int iContent, int iStatus, String sMessage){

            }
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
        configHandler = new ConfigurationHandle(glassAgent);


        if(glassAgent.isConfigured()){
            /*Not configured*/
            Log.d("TAG","App not configured, Scheduling send configure");
			/*Send the configuration message*/
            configHandler.sendEmptyMessage(1);
        }else{
            /*Already configured*/
            Log.d("TAG","App Already configured");
            Toast.makeText(mainView.getApplicationContext(), "App already configured", Toast.LENGTH_LONG).show();
        }
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
     *     idea:
     lines == 5? --> send
     --> view full
     --> arrayList.remove(0)
     --> line = 4
     --> parse next line
     --> wait one
     * @param messageBuffer
     */
    public boolean sendAsMessageFlow(String messageBuffer){


        ArrayList<String> lines = new ArrayList<String>();

        int counter = 0;

        int line = 0;

        if(messageBuffer == null){
            return false;
        }

        while(counter < messageBuffer.length()) {

            if (messageBuffer.length() - (counter + 17) < 0) {

                //rest of buffer is smaller than one line, -> prepare buffer and send
                //then break
                lines.add(messageBuffer);

                String display = new String();

                for(int i = 0 ; i < lines.size(); ++i){

                    display += lines.get(i);
                }

                this.glassAgent.sendContent(CONTENT_ID, lTextOnly, null, new String[]{display});
                break;
            }

            //after 17 signs there is a space --> perfect line
            if (messageBuffer.charAt(counter + 17) == ' ') {

                lines.add(messageBuffer.substring(counter, counter+18));

                counter += 18;

            } else {
                //check next ' ' before 17
                boolean foundSpace = false;

                for (int i = counter + 17; i > counter; i--) {

                    //space found?
                    if (messageBuffer.charAt(i) == ' ') {
                        lines.add(messageBuffer.substring(counter, i+1));
                        counter = i + 1;

                        foundSpace = true;

                        break;
                    }
                }
                //check if a space was found in the line
                if (!foundSpace) {
                    //if no space in whole line just break on letter 17
                    lines.add(messageBuffer.substring(counter, counter+17));
                    counter += 17;
                }
            }

            //already written 6 lines?
            if (line == 5) {

                String display = new String();
                //transform arraylist to string
                for(int i = 0; i < lines.size(); ++i){
                    display += lines.get(i);
                }
                this.glassAgent.sendContent(this.CONTENT_ID, this.lTextOnly,
                        null, new String[]{display});
                //new message buffer
                messageBuffer = messageBuffer.substring(counter, messageBuffer.length());
                counter = 0;
                lines.remove(0); // TODO: or remove, somehow delete first to save on last, move others further
                //sendStringArray(lines);
                try {
                    //sleep so user can read
                    Thread.sleep(2000);
                    //Thread.sleep(5);
                    //glassAgent.registerToEvent();
                    counter = 0;

                } catch (/*Interrupted*/Exception e) {
                    return false;
                }


            } else {

                line++;
            }
        }
        return true;
    }

    /**
     * Create small messages from buffer, each message maximum
     * 6 lines, 17 letters/line
     * @param messageBuffer
     */
    public void sendAsMessages(String messageBuffer) {

        int counter = 0;

        int line = 0;

        if (messageBuffer == null) {
            return;
        }

        while (counter < messageBuffer.length()) {

            if (messageBuffer.length() - (counter + 17) < 0) {

                //rest of buffer is smaller than one line, -> send
                glassAgent.sendContent(CONTENT_ID, lTextOnly, null, new String[]{messageBuffer.substring(0, messageBuffer.length())});
                break;
            }

            //after 17 signs there is a space --> perfect line
            if (messageBuffer.charAt(counter + 17) == ' ') {
                counter += 18;

            } else {
                //check next ' ' before 17
                boolean foundSpace = false;

                for (int i = counter + 17; i > counter; i--) {

                    //space found?
                    if (messageBuffer.charAt(i) == ' ') {

                        counter = i + 1;

                        foundSpace = true;

                        break;
                    }
                }
                //check if a space was found in the line
                if (!foundSpace) {
                    //if no space in whole line just break on letter 17
                    counter += 17;
                }
            }

            //already written 6 lines?
            if (line == 5) {
                glassAgent.sendContent(CONTENT_ID, lTextOnly, null, new String[]{messageBuffer.substring(0, counter)});
                //new message buffer
                messageBuffer = messageBuffer.substring(counter, messageBuffer.length());
                counter = 0;
                //sendStringArray(lines);
                try {
                    //sleep so user can read
                    Thread.sleep(5000);
                    //Thread.sleep(5);
                    //glassAgent.registerToEvent();
                    counter = 0;

                } catch (/*Interrupted*/Exception e) {


                }
                line = 0;

            } else {

                line++;
            }
        }
    }
}
