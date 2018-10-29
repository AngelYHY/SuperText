package vip.freestar.supertext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click_view(final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setSelected(!view.isSelected());
            }
        }, 1000);
    }

}
