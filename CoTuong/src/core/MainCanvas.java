/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import main.Context;

/**
 *
 * @author dong
 */
public class MainCanvas extends Canvas implements Runnable {
    
    private Context mContext;
    private boolean mRunning;
    
    public MainCanvas(Context aContext) {
        mContext = aContext;
        setFullScreenMode(true);
        mRunning = false;
    }
    
    public void start() {
        mRunning = true;
        Thread t = new Thread(this);
        t.start();
    }
    
    public void stop() {
        mRunning = false;
    }
    
    public void paint(Graphics g) {
        mContext.renderScreen(g);
    }
    
    public void keyPressed(int aKeyCode)
    {
        mContext.onKeyPressed(aKeyCode);
    }

    public void onTick(long aMilliseconds) {
        mContext.onTick(aMilliseconds);
    }    
    
    public void run() {
        long clock = System.currentTimeMillis();
        long now;
        long period;
        try {
            while (mRunning) {
                now = System.currentTimeMillis();
                period = now - clock;
                clock = now;      
                
                mContext.onTick(period);
                
                repaint();
                serviceRepaints();

                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
