package comq.example.raymond.autoinsurance;

import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.RegisterCarModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;

public class UInsuredCars extends AppCompatActivity {
    private Toolbar uInsuredToolbar;

    private DatabaseReference cars;
    private FirebaseAuth mAuth;

    private String uId = "";




    private RecyclerView recycler_cars_insured;
    RecyclerView.LayoutManager layoutManager;

    private FirebaseRecyclerAdapter<RegisterCarModel, UInsuredViewHolder>adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uinsured_cars);

        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");
        cars.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();



        //toolbar
        //initialize our toolBar
        uInsuredToolbar = findViewById(R.id.u_insured_toolbar);
        setSupportActionBar(uInsuredToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Cars Under Insurance");

        recycler_cars_insured = findViewById(R.id.recycler_cars_insured);
        recycler_cars_insured.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_cars_insured.setLayoutManager(layoutManager);


        loadCars();

    }

    private void loadCars() {
        FirebaseRecyclerOptions<RegisterCarModel>options = new FirebaseRecyclerOptions.Builder<RegisterCarModel>()
                .setQuery(cars.orderByChild("uId").equalTo(uId), RegisterCarModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RegisterCarModel, UInsuredViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UInsuredViewHolder holder, int position, @NonNull RegisterCarModel model) {
                holder.txtMake.setText(" Car Make: "+ model.getMake());
                holder.txtModel.setText("Car Model: " +model.getModel());
                holder.txtDateInsured.setText(InsuranceUtils.dateFromLong(model.getInsuranceDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //get crime id to new activity
                        Intent carDetail = new Intent(UInsuredCars.this, InsuredCarsDetails.class);
                        carDetail.putExtra("carId", adapter.getRef(position).getKey());
                        startActivity(carDetail);
                    }
                });

            }

            @NonNull
            @Override
            public UInsuredViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cars_insured, viewGroup,false);
                UInsuredViewHolder viewHolder = new UInsuredViewHolder(view);
                return viewHolder;
            }
        };
        recycler_cars_insured.setAdapter(adapter);
        adapter.startListening();
    }


    public static class UInsuredViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtMake, txtModel, txtDateInsured;
        private ItemClickListener itemClickListener;
        public UInsuredViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMake = itemView.findViewById(R.id.make);
            txtModel = itemView.findViewById(R.id.txt_model);
            txtDateInsured = itemView.findViewById(R.id.txt_date_insured);

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
