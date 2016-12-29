package GoogleSpeech;

import android.util.Log;

import com.example.root.hearviaandroidspeech.MainActivity;
import com.example.root.hearviaandroidspeech.Recorder;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.speech.v1beta1.Speech;
import com.google.api.services.speech.v1beta1.SpeechScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.stub.StreamObserver;

/**
 *Created by Martin on 12/15/16.
 */

public class CloudStreamClient implements Recorder {

    private MainActivity mainView;

    public static final String AUDIO_FILE_PATH = "path";
    /**
     * Audio File to parse
     */
    private File audio;

    private SpeechGrpc.SpeechStub speechClient;

    private Speech speech;

    private ManagedChannel channel;

    private CountDownLatch finishLatch;

    private final String authentication_key = "authkey.json";

    private final String HOST = "speech.googleapis.com";

    private final Integer PORT = 443;

    private final Integer sampling = 16000;

    private final int BYTES_PER_BUFFER = 3200; //buffer size in bytes

    private final int BYTES_PER_SAMPLE = 2; //bytes per sample for LINEAR16

    /**
     * Constructor
     */
    public CloudStreamClient(MainActivity mainView){

        this.mainView = mainView;

        try{
            this.channel = createChannel();
        } catch (Exception exception) {
            if(exception.getClass() == FileNotFoundException.class){
                Log.d("TAG","No authentication key for google Cloud found, channel not created");
            }else{
                Log.d("Tag", exception.toString());
            }
        }
        this.speechClient = SpeechGrpc.newStub(channel);
    }

    /**
     * createChannel()
     * creates a ManagedChannel to google speech api
     * using the File authentication_key for authentication
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public ManagedChannel createChannel() throws IOException, GeneralSecurityException {

        if(!new File(authentication_key).exists()){
            throw new FileNotFoundException();
        }
        GoogleCredential creds = GoogleCredential.fromStream(new FileInputStream(authentication_key))
                .createScoped(Collections.singleton(SpeechScopes.CLOUD_PLATFORM));

        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        speech = new Speech.Builder(httpTransport,JSON_FACTORY, creds)
                .setApplicationName(MainActivity.APP_NAME).build();

        GoogleCredentials creds2 = GoogleCredentials.
                fromStream(new FileInputStream(authentication_key))
                .createScoped(Collections.singleton(SpeechScopes.CLOUD_PLATFORM));

        ManagedChannel channel = ManagedChannelBuilder.forAddress(HOST, PORT)
                .intercept(new ClientAuthInterceptor(creds2, Executors.newSingleThreadExecutor()))
                .build();
        return channel;
    }

    public Speech getSpeech(){
        return speech;
    }

    public void setAudio(File audio){
        this.audio = audio;
    }

    public void parseAudio(){

    }
    public void shutdown() {
        //shutdown channel
    }

    @Override
    public void startRecording() {
        //record audio file in one thread
    }

    /**
     * recognizeSpeech()
     *
     */
    public void recognizeSpeech(){
        finishLatch = new CountDownLatch(1);

        StreamObserver<StreamingRecognizeResponse> responseObserver =
                new ResponeStreamObserver(this);

        StreamObserver<StreamingRecognizeRequest> requestObserver =
                speechClient.streamingRecognize(responseObserver);
        try {
            // Build and send a StreamingRecognizeRequest containing the parameters for
            // processing the audio.
            RecognitionConfig config =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setSampleRate(sampling)
                            .build();

            StreamingRecognitionConfig streamingConfig =
                    StreamingRecognitionConfig.newBuilder()
                            .setConfig(config)
                            .setInterimResults(true)
                            .setSingleUtterance(true)
                            .build();

            StreamingRecognizeRequest initial =
                    StreamingRecognizeRequest.newBuilder().setStreamingConfig(streamingConfig).build();
            //send initial request with config
            requestObserver.onNext(initial);


            // Open audio file. Read and send sequential buffers of audio as additional RecognizeRequests.
            FileInputStream in = new FileInputStream(audio);
            // For LINEAR16 at 16000 Hz sample rate, 3200 bytes corresponds to 100 milliseconds of audio.
            byte[] buffer = new byte[BYTES_PER_BUFFER];
            int bytesRead;
            int totalBytes = 0;
            int samplesPerBuffer = BYTES_PER_BUFFER / BYTES_PER_SAMPLE;
            int samplesPerMillis = sampling / 1000;

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
            Log.d("CloudStreamClient: ", "Sent " + totalBytes + " bytes from audio file" );
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
        // Mark the end of requests.
        requestObserver.onCompleted();

        // Receiving happens asynchronously.
        finishLatch.await(1, TimeUnit.MINUTES);
    }
        //stream the audio file to speech api
    public void makeToast(String msg){
        mainView.showToast(msg);
    }
    public void countDownFinishLatch(){
        finishLatch.countDown();
    }
}
