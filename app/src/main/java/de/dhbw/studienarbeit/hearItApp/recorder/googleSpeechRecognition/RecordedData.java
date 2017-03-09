package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

/**
 * Created by mpressl on 3/5/2017.
 */

public class RecordedData {

    private int size;

    private byte[] data;

    public RecordedData(int size, byte[] data){
        this.size = size;
        this.data = data;
    }

    public int getSize(){
        return this.size;
    }

    public byte[] getData(){
        return this.data;
    }
}
