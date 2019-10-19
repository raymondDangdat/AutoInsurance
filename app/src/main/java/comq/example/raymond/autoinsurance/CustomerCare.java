package comq.example.raymond.autoinsurance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class CustomerCare extends AppCompatActivity {
    private Toolbar customerCareToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_care);

        //toolbar
        //initialize our toolBar
        customerCareToolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(customerCareToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contact Customer Care");
    }
}
