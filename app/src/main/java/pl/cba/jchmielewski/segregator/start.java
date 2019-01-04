package pl.cba.jchmielewski.segregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * Okno startowe (menu)
 */
public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    /**
     * Przejdź do ekranu gry
     */
    public void startGame(View view){
        startActivity(new Intent(getApplicationContext(), main.class));
    }

    /**
     * Przejdź do zasad gry
     */
    public void infoGame(View view){
        startActivity(new Intent(getApplicationContext(), info.class));
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
