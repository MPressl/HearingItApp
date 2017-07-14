package de.dhbw.studienarbeit.hearItApp.soundAnimation;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * SoundAnimationThread is the Thread, that creates an animation of static shapes.
 *
 * created by Andreas
 */

public class SoundAnimationThread extends Thread {

    private SurfaceHolder surfaceHolder;
    private SoundAnimationView soundAnimationView;
    private boolean running;
    public static Canvas canvas;



    public SoundAnimationThread(SurfaceHolder surfaceHolder, SoundAnimationView soundAnimationView){
        this.setName("SoundAnimationDrawer");
        this.surfaceHolder = surfaceHolder;
        this.soundAnimationView = soundAnimationView;
    }

    /**
     * setRunning()
     * sets the boolean running true,
     * its the condition for the while-loop in run()
     */
    public void setRunning(boolean running)
    {
        this.running = running;
    }

    /**
     * run()
     * frequently draws the SoundAnimation and updates the SurfaceView and
     * the SoundAnimations parameters
     */
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
