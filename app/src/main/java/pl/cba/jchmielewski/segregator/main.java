package pl.cba.jchmielewski.segregator;

import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Okno gry, algorytmy odpowiadające za rozgrywkę
 */
public class main extends AppCompatActivity {

    private TextView scoreLabel;
    private TextView startLabel;
    /** Obiekt śmietnik */
    private ImageView box;
    /** Obiekt śmieć (złapany, dodaje punkt) */
    private ImageView drop1;
    /** Obiekt śmieć (złapany, dodaje punkt) */
    private ImageView drop2;
    /** Obiekt śmieć (złapany, kończy grę) */
    private ImageView drop3;

    //Position
    private float boxX;
    private int drop1Y;
    private int drop1X;
    private int drop2Y;
    private int drop2X;
    private int drop3Y;
    private int drop3X;

    //Points
    /** Zebrane punkty */
    private int score = 0;

    //Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;

    //Status check
    /** Wykrycie startu gry */
    private boolean start_flg = false;

    //size
    private int frameHeight;
    private int frameWidth;
    private int boxHeight;
    private int boxWidth;
    /** Kategoria śmietnika i śmieci */
    private int category;
    /** Prędkość spadania śmieci */
    private int speed;

    /**
     * Inicjalizacja, pobranie id elementów, zapisanie rozmiaru(rozdzielczości) ekranu.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sound = new SoundPlayer(this);

        scoreLabel = findViewById(R.id.scoreLabel);
        startLabel = findViewById(R.id.startLabel);
        box = findViewById(R.id.box);
        drop1 = findViewById(R.id.drop1);
        drop2 = findViewById(R.id.drop2);
        drop3 = findViewById(R.id.drop3);

        //Move to out of screen
        drop1.setX(20);
        drop2.setX(20);
        drop3.setX(20);

        scoreLabel.setText("Punkty:0");

    }


    /**
     * Klasa odpowiada za zmiane położenia wszystkich elementów, blokuje przesówanie kosza poza ekran,
     * losuje położenie smieci na osi X
     */
    public void changePos(){

       hitCheck();

        //śmieć 1
        drop1Y += speed+1;
        // Jeśli śmieć wyszedł poza ekran - ustaw go nad górną krawędzią ekranu (niewidoczny)
        if (drop1Y > frameHeight || start_flg == false){
            drop1Y = -drop1.getHeight() - 21;
            score -= 1;//jeśli nie zostałe złapany odejmi jeden punkt score (-1+2) lub (-1)

            //wylosuj położenie na osi X, wykonuj dopóki pokrywa się z położeniem śmiecia3(koczczącego gre)
            do {
                drop1X = (int) Math.floor(Math.random() * (frameWidth - drop1.getWidth())); //X = [0,1) * cały zakres ekranu
            } while (drop1X >= (drop3X - drop1.getWidth()-10) && drop1X <= (drop3X + drop3.getWidth()+10));

            scoreLabel.setText("Punkty:"+score);
            drop1.setX(drop1X);
        }
        drop1.setY(drop1Y);

        //śmieć 2
        drop2Y += speed;
        if (drop2Y > frameHeight || start_flg == false){
            drop2Y = -drop2.getHeight() - 5;
            score -= 1;

            do {
                drop2X = (int) Math.floor(Math.random() * (frameWidth - drop2.getWidth()));
            } while (drop2X >= (drop3X - drop2.getWidth()-10) && drop2X <= (drop3X + drop3.getWidth()+10));

            scoreLabel.setText("Punkty:"+score);
            drop2.setX(drop2X);
        }
        drop2.setY(drop2Y);

        //śmieć 3 (zły)
        drop3Y += speed-1;
        if (drop3Y > frameHeight || start_flg == false){
            drop3Y = -drop3.getHeight() - 18;

            do {
                drop3X = (int) Math.floor(Math.random() * (frameWidth - drop3.getWidth()));
            } while ((drop3X >= (drop1X - drop3.getWidth()-10) && drop3X <= (drop1X + drop1.getWidth()+10)) ||
                    (drop3X >= (drop2X - drop3.getWidth()-10) && drop3X <= (drop2X + drop2.getWidth()+10)));

            drop3.setX(drop3X);
        }
        drop3.setY(drop3Y);

        //blokowanie smietnika w ekranie
        if (boxX < 0) boxX = 0;
        if (boxX > frameWidth - boxWidth) boxX = frameWidth - boxWidth;

        box.setX(boxX);

        //Gra się rozpoczeła (domyślnie =false)
        start_flg = true;
    }

    /**
     * Sprawdza czy doszło do złapania obiektu do kosza,
     * jeśli złapano drop3 - kończy gre,
     * jeśli załapano drop1 lub drop2 - dodaje 1 punkt
     */
    public void hitCheck(){

        // srodek x = polozenie x + polowa szerokości
        int drop1CenterX = drop1X + drop1.getWidth() / 2;
        // srodek y = polozenie y + polowa wysokości
        int drop1CenterY = drop1Y + drop1.getHeight() / 2;

        //jeśli środek obiektu trafił do kosza
        if ((frameHeight - boxHeight) <= drop1CenterY && drop1CenterY <= (frameHeight - (2*boxHeight/3)) &&
                boxX <= drop1CenterX && drop1CenterX <= boxX + boxWidth){

            score += 2;
            drop1Y = frameHeight-10;
            sound.playHitSound();
        }

        int drop2CenterX = drop2X + drop2.getWidth() / 2;
        int drop2CenterY = drop2Y + drop2.getHeight() / 2;

        if ((frameHeight - boxHeight) <= drop2CenterY && drop2CenterY <= (frameHeight - (2*boxHeight/3)) &&
                boxX <= drop2CenterX && drop2CenterX <= boxX + boxWidth){

            score += 2;
            drop2Y = frameHeight-10;
            sound.playHitSound();
        }

        int drop3CenterX = drop3X + drop3.getWidth() / 2;
        int drop3CenterY = drop3Y + drop3.getHeight() / 2;

        if ((frameHeight - boxHeight) <= drop3CenterY && drop3CenterY <= (frameHeight - (2*boxHeight/3)) &&
                boxX <= drop3CenterX && drop3CenterX <= boxX + boxWidth){

            drop3Y = frameHeight-10;
            timer.cancel();
            timer = null;
            sound.playOverSound();

            Intent intent = new Intent(getApplicationContext(), result.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
        }

    }

    /**
     * Zmienia kategorie, wygląd śmieci i kosza.
     *
     */
    public void switchCategory(){
        switch (category){
            case 0:
                category = 1;
                drop1.setImageResource(R.drawable.cebula);
                drop2.setImageResource(R.drawable.chleb);
                drop3.setImageResource(R.drawable.kosc);
                box.setImageResource(R.drawable.bio);
                speed += speed/3; //przyspieszenie spadania

                break;
            case 1:
                category = 2;
                drop1.setImageResource(R.drawable.gazeta);
                drop2.setImageResource(R.drawable.karton);
                drop3.setImageResource(R.drawable.metal);
                box.setImageResource(R.drawable.papier);

                break;
            case 2:
                category = 3;
                drop1.setImageResource(R.drawable.szloik);
                drop2.setImageResource(R.drawable.butelka_szklana);
                drop3.setImageResource(R.drawable.butelka);
                box.setImageResource(R.drawable.szklo);

                break;
            case 3:
                category = 4;
                drop1.setImageResource(R.drawable.kosc);
                drop2.setImageResource(R.drawable.mis);
                drop3.setImageResource(R.drawable.gazeta);
                box.setImageResource(R.drawable.resztkowe);

                break;
            case 4:
                category = 0;
                drop1.setImageResource(R.drawable.butelka);
                drop2.setImageResource(R.drawable.metal);
                drop3.setImageResource(R.drawable.cebula);
                box.setImageResource(R.drawable.sztuczne);

                break;
        }
        drop1Y = -frameHeight*2+25;
        drop1.setY(drop1Y);
        drop2Y = -frameHeight*2-2;
        drop2.setY(drop2Y);
        drop3Y = -frameHeight*2-50;
        drop3.setY(drop3Y);

    }

    /**
     * Klasa reaguje na dotyk ekranu. Pierwsze dotknięcie pobiera dane o ekranie,
     * ustawia startowe wartości zmiennych i uruchamia timer.
     * Kolejne dotknięcia pozwalają sterować położeniem kosza.
     */
    public boolean onTouchEvent(MotionEvent me){
        if (start_flg == false){

            FrameLayout frame = findViewById(R.id.frame);
            TextView scoreLabel = findViewById(R.id.scoreLabel);

            frameHeight = frame.getHeight();
            frameWidth = frame.getWidth();

            boxX = (int)box.getX();

            boxHeight = box.getHeight() + box.getPaddingBottom();
            boxWidth = box.getWidth();

            startLabel.setVisibility(View.GONE);

            speed = frameHeight/150;

            category = 0;

            score = 2;

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
            }, 0,20); //20ms odpowiada 50Hz

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            switchCategory();
                        }
                    });
                }
            }, 0,20000); //20sekund

        }else {
            switch (me.getAction()){
                case MotionEvent.ACTION_DOWN:
                    boxX = me.getX() - boxWidth/2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    boxX = me.getX() - boxWidth/2;
                    break;
                case MotionEvent.ACTION_UP:
                    //action_flg = false;
                    boxX = me.getX() - boxWidth/2;
                    break;
            }

        }

        return true;
    }

    /**
     * Blokuje użycie przycisku "powrót"
     */
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
