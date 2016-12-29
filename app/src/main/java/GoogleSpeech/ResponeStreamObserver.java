package GoogleSpeech;

import com.google.cloud.speech.v1beta1.StreamingRecognizeResponse;

/**
 * ResponseStreamObserver
 * listens to the ResponeStream of the SpeechRecognitionRequests
 * performed in CloudStreamClient
 * Created by root on 12/28/16.
 */

public class ResponeStreamObserver implements io.grpc.stub.StreamObserver<StreamingRecognizeResponse>{

    private CloudStreamClient client;

    public ResponeStreamObserver(CloudStreamClient client){
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
        client.countDownLatch();
    }

    @Override
    public void onCompleted() {
        client.makeToast( "recognize complete");
        client.countDownLatch();
    }
}
