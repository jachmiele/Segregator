package pl.cba.jchmielewski.segregator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Okno zawierające zasady gry
 */
public class info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }

    /**
     * Powrót do Menu
     */
    public void getBack(View view){
        //przekierowanie po kliknieciu try again
        startActivity(new Intent(getApplicationContext(), start.class));
    }
}
