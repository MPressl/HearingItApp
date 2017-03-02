package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.util.Log;


import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


/**
 * Created by root on 1/13/17.
 */

public class RequestStreamClient implements Runnable{


    private GoogleRecorder manager;

    private CountDownLatch finishLatch;

    SpeechGrpc.SpeechStub speechRPCStub;

    private final String authentication_key = "authkey.json";

    private final String HOST = "speech.googleapis.com";

    private final Integer PORT = 443;

    public RequestStreamClient(GoogleRecorder parent){
        this.manager = parent;

    }


    @Override
    public void run() {
    //create channel and speech stub
        try{
            Channel channel = createChannel();
            this.speechRPCStub = SpeechGrpc.newStub(channel);
            this.recognizeSpeech();

        } catch (Exception exception) {
            if(exception.getClass() == FileNotFoundException.class){
                Log.d("TAG","No authentication key for google Cloud found, channel not created");
            }else{
                Log.d("Tag", exception.toString());
            }
        }
    }

    /**
     * createChannel()
     * creates a ManagedChannel to google speech api
     * using the File authentication_key for authentication
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public Channel createChannel() throws IOException, GeneralSecurityException {

       // manager.getMainView().getAssets().open(authentication_key);
        //if(!new File(authentication_key).exists()){
       //     throw new FileNotFoundException();
        //}
        //TODO: add scoped auth
       // GoogleCredential creds = GoogleCredential.fromStream(new FileInputStream(authentication_key));


        //JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        //HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        //speech = new Speech.Builder(httpTransport,JSON_FACTORY, creds)
          //      .setApplicationName(MainActivity.APP_NAME).build();

        Channel channel = ManagedChannelBuilder.forAddress(HOST,PORT).build();
//        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
//                .intercept(new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor()))
//                .build();
        return channel;
    }


    public void shutdownChannel() {
        //shutdown channel
    }

    /**
     * recognizeSpeech()
     *
     */
    public void recognizeSpeech(){
        finishLatch = new CountDownLatch(1);

        //Create a respone stream observer
        StreamObserver<StreamingRecognizeResponse> responseObserver =
                new ResponeStreamObserver();

        //create a request stream with the just created respone observer as answer stream
        StreamObserver<StreamingRecognizeRequest> requestObserver =
                this.speechRPCStub.streamingRecognize(responseObserver);
        try {
            // Build and send a StreamingRecognizeRequest containing the parameters for
            // processing the audio.
            RecognitionConfig audioConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setSampleRate(GoogleRecorder.SAMPLING)
                            .build();

            StreamingRecognitionConfig streamingConfig =
                    StreamingRecognitionConfig.newBuilder()
                            .setConfig(audioConfig)
                            .setInterimResults(true)
                            .setSingleUtterance(true)
                            .build();

            StreamingRecognizeRequest initialRequest =
                    StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
            //send initial request with config
            requestObserver.onNext(initialRequest);
        }catch (RuntimeException e) {
                requestObserver.onError(e);
                throw e;
            }

/*
            // Open audio file. Read and send sequential buffers of audio as additional RecognizeRequests.
            FileInputStream in = new FileInputStream(GoogleRecorder.AUDIO_FILE);
            // For LINEAR16 at 16000 Hz sample rate, 3200 bytes corresponds to 100 milliseconds of audio.
            byte[] buffer = new byte[GoogleRecorder.BYTES_PER_BUFFER];
            int bytesRead;
            int totalBytes = 0;
            int samplesPerBuffer = GoogleRecorder.BYTES_PER_BUFFER / GoogleRecorder.BYTES_PER_SAMPLE;
            int samplesPerMillis = GoogleRecorder.SAMPLING / 1000;

            while ((bytesRead = in.read(buffer)) != -1) {
                totalBytes += bytesRead;
                StreamingRecognizeRequest request =
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(buffer, 0, bytesRead))
                                .build();
                requestObserver.onNext(request);
                // To simulate real-time audio, sleep after sending each audio buffer.
                Thread.sleep(samplesPerBuffer / samplesPerMillis);
            }
            Log.d("GoogleRecorder: ", "Sent " + totalBytes + " bytes from audio file" );
        } catch (RuntimeException e) {
            // Cancel RPC.
            requestObserver.onError(e);
            throw e;
        }
        //TODO: Exception handling
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        // Mark the end of requests.
        requestObserver.onCompleted();

        // Receiving happens asynchronously.
        try {
            finishLatch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void countDownFinishLatch(){
        finishLatch.countDown();
    }
}
