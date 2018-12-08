package pl.cba.jchmielewski.segregator;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class main extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    private ImageView box;
    private ImageView cebula;
    private ImageView chleb;
    private ImageView kosc;

    //Position
    private float boxX;
    private int cebulaY;
    private int cebulaX;
    private int chlebY;
    private int chlebX;
    private int koscY;
    private int koscX;

    //Points
    private int score = 0;

    //Initialize Class
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private Timer timer = new Timer();
    private Timer timer2 = new Timer();
    private SoundPlayer sound;

    //Status check
    private boolean action_flg = false;
    private boolean start_flg = false;

    //size
    private int frameHeight;
    private int frameWidth;
    private int boxSize;
    private int screenWidth;
    private int screenHeight;
    private int category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        cebula = findViewById(R.id.cebula);
        chleb = findViewById(R.id.chleb);
        kosc = findViewById(R.id.kosc);

        //Get screen size
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //Move to out of screen
        cebula.setX(-80);
        cebula.setY(-80);
        chleb.setX(-80);
        chleb.setY(-80);
        kosc.setX(-80);
        kosc.setY(-80);

        scoreLabel.setText("Score:0");

    }

    public void changePos(){

       hitCheck();

        //Cebula
        cebulaY += 5;
        if (cebulaY > frameHeight){
            cebulaY = -cebula.getHeight() - 20; //20 za prawa krawedzia ekranu
            cebulaX = (int) Math.floor(Math.random() * (frameWidth - cebula.getWidth()));//[0,1)*wysokosc layoutu
        }
        cebula.setX(cebulaX);
        cebula.setY(cebulaY);

        //Chleb
        chlebY += 5;
        if (chlebY > frameHeight){
            chlebY = -chleb.getHeight() - 20; //20 za prawa krawedzia ekranu
            chlebX = (int) Math.floor(Math.random() * (frameWidth - chleb.getWidth()));//[0,1)*wysokosc layoutu
        }
        chleb.setX(chlebX);
        chleb.setY(chlebY);

        //Kosc
        koscY += 5;
        if (koscY > frameHeight){
            koscY = -kosc.getHeight() - 20; //20 za prawa krawedzia ekranu
            koscX = (int) Math.floor(Math.random() * (frameWidth - kosc.getWidth()));//[0,1)*wysokosc layoutu
        }
        kosc.setX(koscX);
        kosc.setY(koscY);


        /*
        if (action_flg == true){
            //touching
            boxX += 20;
        }
        else {
            //releasing
            boxX -= 20;
        }
        */

        //blokowanie smietnika w ekranie
        if (boxX < 0) boxX = 0;
        if (boxX > frameWidth - boxSize) boxX = frameWidth - boxSize;

        box.setX(boxX);

        scoreLabel.setText("Score:"+score);
    }

    public void hitCheck(){
        //jesli srodek smiecia jest w koszu

        //cebulkia
        // srodek x = polozenie x + polowa szerokosci
        int cebulaCenterX = cebulaX + cebula.getWidth() / 2;
        int cebulaCenterY = cebulaY + cebula.getHeight() / 2;

        if ((frameHeight - boxSize) <= cebulaCenterY && cebulaCenterY <= frameHeight &&
                boxX <= cebulaCenterX && cebulaCenterX <= boxX + boxSize){

            score += 1;
            cebulaY = frameHeight-10;
            sound.playHitSound();
        }

        int chlebCenterX = chlebX + chleb.getWidth() / 2;
        int chlebCenterY = chlebY + chleb.getHeight() / 2;

        if ((frameHeight - boxSize) <= chlebCenterY && chlebCenterY <= frameHeight &&
                boxX <= chlebCenterX && chlebCenterX <= boxX + boxSize){

            score += 1;
            chlebY = frameHeight-10;
            sound.playHitSound();
        }

        int koscCenterX = koscX + kosc.getWidth() / 2;
        int koscCenterY = koscY + kosc.getHeight() / 2;

        if ((frameHeight - boxSize) <= koscCenterY && koscCenterY <= frameHeight &&
                boxX <= koscCenterX && koscCenterX <= boxX + boxSize){

            koscY = frameHeight-10;
            timer.cancel();
            timer2.cancel();
            timer = null;
            timer2 = null;
            sound.playOverSound();
            end();

            /*
            if (category == 0){
                timer.cancel();
                timer2.cancel();
                timer = null;
                timer2 = null;
                sound.playOverSound();
                end();
            }else{
                score += 1;
                sound.playHitSound();
            }
            */
        }

    }

    public void end(){
        Intent intent = new Intent(getApplicationContext(), result.class);
        intent.putExtra("SCORE", score);
        startActivity(intent);
    }

    public void switchCategory(){
        switch (category){
            case 0:
                category = 1;
                cebula.setImageResource(R.drawable.cebula);
                chleb.setImageResource(R.drawable.chleb);
                kosc.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.bio);
                break;
            case 1:
                category = 2;
                cebula.setImageResource(R.drawable.cebula);
                chleb.setImageResource(R.drawable.chleb);
                kosc.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.papier);

                break;
            case 2:
                category = 3;
                cebula.setImageResource(R.drawable.cebula);
                chleb.setImageResource(R.drawable.chleb);
                kosc.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.szklo);

                break;
            case 3:
                category = 4;
                cebula.setImageResource(R.drawable.cebula);
                chleb.setImageResource(R.drawable.chleb);
                kosc.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.resztkowe);

                break;
            case 4:
                category = 0;
                cebula.setImageResource(R.drawable.cebula);
                chleb.setImageResource(R.drawable.chleb);
                kosc.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.sztuczne);

                break;
        }
        cebulaY = -200;
        chlebY = -10;
        koscY = -80;
    }

    public boolean onTouchEvent(MotionEvent me){
        if (start_flg == false){
            start_flg = true;

            FrameLayout frame = findViewById(R.id.frame);
            TextView scoreLabel = findViewById(R.id.scoreLabel);

            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();

            boxX = (int)box.getX();

            boxSize = box.getHeight();

            startLabel.setVisibility(View.GONE);

            category = 0;

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0,10);

            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
                            switchCategory();
                        }
                    });
                }
            }, 0,5000);

        }else {
            switch (me.getAction()){
                case MotionEvent.ACTION_DOWN:
                    boxX = me.getX() - boxSize/2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    boxX = me.getX() - boxSize/2;
                    break;
                case MotionEvent.ACTION_UP:
                    //action_flg = false;
                    boxX = me.getX() - boxSize/2;
                    break;
            }

            /*
            if (me.getAction() == MotionEvent.ACTION_DOWN){
                action_flg = true;
            }else if (me.getAction() == MotionEvent.ACTION_UP){
                action_flg = false;
            }
            */
        }

        return true;
    }

    // Disable return button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){

        if (event.getAction() == KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

}
