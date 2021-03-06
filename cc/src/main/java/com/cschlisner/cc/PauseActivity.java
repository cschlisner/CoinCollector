package com.cschlisner.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class PauseActivity extends Activity {
    private TextContainer resumeButton, menuButton, optionsButton;
    private int titleX, screenWidth, screenHeight;
    private boolean startNewActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PauseView pauseView = new PauseView(this);
        setContentView(pauseView);
    }
    public class PauseView extends View {
        private Paint paint = new Paint();
        private String title = "Paused";
        public PauseView(Context context){
            super(context);
            titleX = 20;
            screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            resumeButton = new TextContainer(context, "resume", 60, (screenWidth/2)-(screenWidth/20),
                    (screenHeight/2)-(screenHeight/6));
            menuButton = new TextContainer(context, "main menu", 60, (screenWidth/2), screenHeight/2);
            optionsButton = new TextContainer(context, "options", 60, (screenWidth/2)+(screenWidth/20),
                    (screenHeight/2)+(screenHeight/6));
            paint.setTypeface(Typeface.createFromAsset(getAssets(), "robotolight.ttf"));
            paint.setTextSize(120);
            paint.setColor(Color.WHITE);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            resumeButton.draw(canvas);
            menuButton.draw(canvas);
            optionsButton.draw(canvas);
            canvas.drawText(title, titleX, 120, paint);
            if (resumeButton.pressed || menuButton.pressed || optionsButton.pressed){
                update();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { }
                invalidate();
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e){
            float x = e.getX();
            float y = e.getY();

            if (resumeButton.bounds.contains((int)x, (int)y)){
                resumeButton.pressed = true;
                menuButton.paint.setAlpha(0);
                optionsButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (menuButton.bounds.contains((int)x, (int)y)){
                menuButton.pressed = true;
                resumeButton.paint.setAlpha(0);
                optionsButton.paint.setAlpha(0);
                postInvalidate();
            }
            else if (optionsButton.bounds.contains((int)x, (int)y)){
                optionsButton.pressed = true;
                menuButton.paint.setAlpha(0);
                resumeButton.paint.setAlpha(0);
                postInvalidate();
            }
            return true;
        }

        private void update(){
            if (resumeButton.pressed){
                if (resumeButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    resumeButton.bounds.left += 30;
                    titleX -= 30;
                }
                else {
                    onBackPressed();
                    finish();
                }
            }
            else if (menuButton.pressed){
                if (menuButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    menuButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startNewActivity){
                    startNewActivity = true;
                    Activity parent = (Activity)GameActivity.gamectx;
                    parent.finish();
                    Intent i = new Intent(getContext(), TitleScreenActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            else if (optionsButton.pressed){
                if (optionsButton.bounds.left <= screenWidth || titleX >= 0-paint.measureText(title)){
                    optionsButton.bounds.left += 30;
                    titleX -= 30;
                }
                else if (!startNewActivity){
                    startNewActivity = true;
                    Intent i = new Intent(getContext(), OptionsActivity.class);
                    i.putExtra("CALLER", "PauseActivity");
                    startActivity(i);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        startNewActivity = false;
        optionsButton.reset("options", 60, (screenWidth/2)+(screenWidth/20),
                (screenHeight/2)+(screenHeight/6));
        menuButton.reset("main menu", 60, (screenWidth/2), screenHeight/2);
        resumeButton.reset("resume", 60, (screenWidth/2)-(screenWidth/20),
                (screenHeight/2)-(screenHeight/6));
        titleX = 20;
        super.onResume();
    }
}
