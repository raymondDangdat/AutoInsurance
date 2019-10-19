package comq.example.raymond.autoinsurance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class InsuredCarsDetails extends AppCompatActivity {
    private Toolbar insuredCarsToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insured_cars_details);


        //initialize toolBar
        insuredCarsToolBar = findViewById(R.id.insured_cars_detalis_toolbar);
        setSupportActionBar(insuredCarsToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Car Detalis");
    }
}
