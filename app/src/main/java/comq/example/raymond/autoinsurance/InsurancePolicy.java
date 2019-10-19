package comq.example.raymond.autoinsurance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class InsurancePolicy extends AppCompatActivity {
    private Toolbar insurancePolicyToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_policy);


        //toolbar
        //initialize our toolBar
        insurancePolicyToolbar = findViewById(R.id.insurance_policy_toolbar);
        setSupportActionBar(insurancePolicyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Insurance Policy");
    }
}
