package comq.example.raymond.autoinsurance;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.PaymentModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;

public class PaymentDetailsActivity extends AppCompatActivity {
    private Toolbar paymentDetailsToolbar;
    private DatabaseReference payments;
    private FirebaseAuth mAuth;

    private String uId = "";

    private RecyclerView recycler_payments;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<PaymentModel, PaymentViewHolder>adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        //toolbar
        //initialize our toolBar
        paymentDetailsToolbar = findViewById(R.id.payments_toolbar);
        setSupportActionBar(paymentDetailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Your Payments");

        payments = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("payments");
        payments.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        recycler_payments = findViewById(R.id.recycler_payments);
        recycler_payments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_payments.setLayoutManager(layoutManager);


        loadPayments();


    }

    private void loadPayments() {
        FirebaseRecyclerOptions<PaymentModel> options = new FirebaseRecyclerOptions.Builder<PaymentModel>()
                .setQuery(payments.orderByChild("uId").equalTo(uId), PaymentModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<PaymentModel, PaymentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PaymentViewHolder holder, int position, @NonNull PaymentModel model) {
                holder.txtMake.setText("Car Make: " + model.getMake());
                holder.txtAmount.setText("Amount Paid: " + String.valueOf(model.getValue()));
                holder.txtDatePaid.setText("Date Paid" +  InsuranceUtils.dateFromLong(model.getPaymentDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(PaymentDetailsActivity.this, "Work in progress", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.payment_layout, viewGroup,false);
                PaymentViewHolder viewHolder = new PaymentViewHolder(view);
                return viewHolder;
            }
        };
        recycler_payments.setAdapter(adapter);
        adapter.startListening();

    }



    public static class PaymentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtMake, txtAmount,  txtDatePaid;
        private ItemClickListener itemClickListener;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMake = itemView.findViewById(R.id.make);
            txtAmount = itemView.findViewById(R.id.txt_amount_paid);
            txtDatePaid = itemView.findViewById(R.id.txt_date_paid);

            itemView.setOnClickListener(this);
        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }
}
