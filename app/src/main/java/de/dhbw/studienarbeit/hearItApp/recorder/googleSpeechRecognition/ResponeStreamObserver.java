package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.util.Log;

import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;
import com.google.protobuf.TextFormat;

/**
 * ResponseStreamObserver
 * listens to the ResponeStream of the SpeechRecognitionRequests
 * performed in GoogleRecorder
 * Created by root on 12/28/16.
 */

public class ResponeStreamObserver
        implements io.grpc.stub.StreamObserver<StreamingRecognizeResponse>{

//    private RequestStreamClient client;

    public ResponeStreamObserver(RequestStreamClient client){
//        this.client = client;
    }

    public ResponeStreamObserver() {

    }

    @Override
    public void onNext(StreamingRecognizeResponse response) {
        Log.e("RESPONSESTREAM: ","Received response: " + TextFormat.printToString(response));
       // client.makeToast("gotresponse!");
    }

    @Override
    public void onError(Throwable error) {
        Log.e("RESPONSESTREAM: ","Received response: " + error.getMessage());
        //client.makeToast( "recognize failed "
                       // + error);
        //client.countDownFinishLatch();
    }

    @Override
    public void onCompleted() {
        Log.e("RESPONSESTREAM: ","completed");
        //client.makeToast( "recognize complete");
        //client.countDownFinishLatch();
    }

}
