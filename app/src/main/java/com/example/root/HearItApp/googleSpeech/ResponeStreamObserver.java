package com.example.root.HearItApp.googleSpeech;

import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;

/**
 * ResponseStreamObserver
 * listens to the ResponeStream of the SpeechRecognitionRequests
 * performed in GoogleRecorder
 * Created by root on 12/28/16.
 */

public class ResponeStreamObserver
        implements io.grpc.stub.StreamObserver<StreamingRecognizeResponse>{

    private GoogleSpeechStreamer client;

    public ResponeStreamObserver(GoogleSpeechStreamer client){
        this.client = client;
    }

    @Override
    public void onNext(StreamingRecognizeResponse response) {
        //Log.e("Received response: " + TextFormat.printToString(response));
        client.makeToast("gotresponse!");
    }

    @Override
    public void onError(Throwable error) {
        client.makeToast( "recognize failed "
                        + error);
        client.countDownFinishLatch();
    }

    @Override
    public void onCompleted() {
        client.makeToast( "recognize complete");
        client.countDownFinishLatch();
    }
}
