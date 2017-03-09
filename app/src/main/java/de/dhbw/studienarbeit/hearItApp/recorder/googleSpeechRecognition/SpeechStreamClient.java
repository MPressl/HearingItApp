package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1beta1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import de.dhbw.studienarbeit.hearItApp.MainActivity;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.okhttp.OkHttpChannelBuilder;
import io.grpc.okhttp.OkHttpChannelProvider;
import io.grpc.stub.StreamObserver;


/**
 * Created by root on 1/13/17.
 */

public class SpeechStreamClient {


    private static final List<String> OAUTH2_SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/cloud-platform");

    private GoogleRecorder manager;

    private SpeechGrpc.SpeechStub speechRPCStub;

    private StreamObserver<StreamingRecognizeResponse> responseObserver;

    private StreamObserver<StreamingRecognizeRequest> requestObserver;

    private final String authentication_key = "authkey.json";

    private final String HOST = "speech.googleapis.com";

    private final Integer PORT = 443;

    private boolean isInititalized;

    public SpeechStreamClient(GoogleRecorder parent)
            throws InterruptedException, IOException, GeneralSecurityException {

        this.manager = parent;
        // Required to support Android 4.x.x (patches for OpenSSL from Google-Play-Services)
        try {
            ProviderInstaller.installIfNeeded(parent.getMainView().getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {

            // Indicates that Google Play services is out of date, disabled, etc.
            e.printStackTrace();
            // Prompt the user to install/update/enable Google Play services.
            GooglePlayServicesUtil.showErrorNotification(
                    e.getConnectionStatusCode(), parent.getMainView().getApplicationContext());
            return;

        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates a non-recoverable error; the ProviderInstaller is not able
            // to install an up-to-date Provider.
            e.printStackTrace();
            return;
        }
        //create channel and speech stub
        Channel channel = createChannel();
        this.speechRPCStub = SpeechGrpc.newStub(channel);
    }

    /**
     * createChannel()
     * creates a ManagedChannel to google speech api
     * using the File authentication_key for authentication
     * @return
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private Channel createChannel() throws IOException, GeneralSecurityException {

        InputStream credentials = this.manager.getMainView().getAssets().open(authentication_key);
        GoogleCredentials creds = GoogleCredentials.fromStream(credentials)
                .createScoped(this.OAUTH2_SCOPES);
        OkHttpChannelProvider provider = new OkHttpChannelProvider();
        OkHttpChannelBuilder builder = provider.builderForAddress(this.HOST, this.PORT);
        
        ManagedChannel channel = //ManagedChannelBuilder.forAddress(HOST, PORT)
                builder.intercept(
                        new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor())).build();

        credentials.close();
        return channel;
    }

    public void initializeStreaming() {
        this.responseObserver =
                new ResponeStreamObserver();

        //create a request stream with the just created respone observer as answer stream
        this.requestObserver =
                this.speechRPCStub.streamingRecognize(this.responseObserver);
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
            this.requestObserver.onNext(initialRequest);
        }catch (RuntimeException e) {
            this.requestObserver.onError(e);
            throw e;
        }
    }

    /**
     * recognizeSpeech()
     *
     */
    public void recognizeBytes(byte[] buffer, int size){

        if (!this.isInititalized) {
            this.initializeStreaming();
            this.isInititalized = true;
        }
        try {
            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(ByteString.copyFrom(buffer, 0, size))
                            .build();
            requestObserver.onNext(request);
        } catch (RuntimeException e) {
            Log.e(MainActivity.LOG_TAF, "Error while recognizing speech. Stopping." + e.getMessage());
            requestObserver.onError(e);
            throw e;
        }
    }

}
