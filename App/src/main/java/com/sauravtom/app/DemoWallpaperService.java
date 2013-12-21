package com.sauravtom.app;

/**
 * Created by saurav on 21/12/13.
 */

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;


public class DemoWallpaperService extends WallpaperService {

    public int count = 0;
    public int TIME = 500;

    @Override
    public Engine onCreateEngine() {
        return new DemoWallpaperEngine();
    }
    private class DemoWallpaperEngine extends Engine {
        private boolean mVisible = false;
        private final Handler mHandler = new Handler();
        private final Runnable mUpdateDisplay = new Runnable() {
            @Override
            public void run() {
                draw();
            }};
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    Paint p = new Paint();
                    p.setTextSize(20);
                    p.setAntiAlias(true);
                    String text = "system time: "+Long.toString(System.currentTimeMillis());
                    float d = p.measureText(text, 0, text.length());
                    int offset = (int) d / 2;
                    int w = c.getWidth();
                    int h = c.getHeight();
                    p.setColor(Color.BLACK);
                    c.drawRect(0, 0, w, h, p);
                    p.setColor(Color.WHITE);
                    c.drawText(text, w/2- offset, h/2, p);

                    int radius  = w/8;
                    int[][] arr = new int[][]{
                         {w/4,h/4},{3*w/4,h/4},{3*w/4,3*h/4},{w/4,3*h/4}
                    };

                    for(int i=0;i<4;i++){
                        if(i != count){
                             c.drawCircle( arr[i][0] , arr[i][1], radius ,p);
                        }
                    }
                    if(++count == 4) count = 0;

                }
            } finally {
                if (c != null)
                    holder.unlockCanvasAndPost(c);
            }
            mHandler.removeCallbacks(mUpdateDisplay);
            if (mVisible) {
                mHandler.postDelayed(mUpdateDisplay, TIME);
            }
        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            mVisible = visible;
            if (visible) {
                draw();
            } else {
                mHandler.removeCallbacks(mUpdateDisplay);
            }
        }
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            draw();
        }
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            mVisible = false;
            mHandler.removeCallbacks(mUpdateDisplay);
        }
    }
}
