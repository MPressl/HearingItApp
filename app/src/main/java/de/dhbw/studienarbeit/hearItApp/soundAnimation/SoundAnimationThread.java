package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Andi on 28.03.2017.
 */

public class SoundAnimationThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private SoundAnimationView soundAnimationView;
    private boolean running;
    public static Canvas canvas;



    public SoundAnimationThread(SurfaceHolder surfaceHolder, SoundAnimationView soundAnimationView){
        this.surfaceHolder = surfaceHolder;
        this.soundAnimationView = soundAnimationView;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    @Override
    public void run(){

        while(running){
            canvas = null;

            try{
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.soundAnimationView.update();
                    this.soundAnimationView.draw(canvas);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(canvas != null) try{
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            try {
                this.sleep(1000/100);  //100FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
