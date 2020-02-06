package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.RegisterCarModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;

public class InsuranceApplications extends AppCompatActivity {
    //toolbar
    private android.support.v7.widget.Toolbar insurance_application_toolbar;

    private DatabaseReference cars;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private FloatingActionButton fab;

    private FirebaseRecyclerAdapter<RegisterCarModel, ViewHolder>adapter;
    private RecyclerView recycler_insurance_application;
    RecyclerView.LayoutManager layoutManager;

    private MaterialEditText editTextToRefresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_applications);


        //initialize toolBar
        insurance_application_toolbar = findViewById(R.id.insurance_applications_toolbar);
        setSupportActionBar(insurance_application_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Insurance Applications");

        cars = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("cars");
        mAuth = FirebaseAuth.getInstance();

        fab = findViewById(R.id.fab);

        recycler_insurance_application = findViewById(R.id.recycler_insurance_applications);
        recycler_insurance_application.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_insurance_application.setLayoutManager(layoutManager);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshInsuranceApplications();
            }
        });

        loadInsuranceApplications();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sort_menu, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            //display alert to choose sort type
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showSortDialog() {
        //Options to display
        String[] sortOptions = {"Awaiting Approval", "Approved", "Declined"};
        //create alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by application status:")
                .setIcon(R.drawable.ic_sort_black)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //the which contains the index position of the selected item
                        if (which==0){
                            //Male selected
                            loadInsuranceApplications();
                        }else if (which==1){
                            loadApprovedApplications();
                        }else if (which==2){
                            loadDeclinedApplications();
                            //to be sorted according to chalet number
                        }
                    }
                });
        builder.show();
    }

    private void loadDeclinedApplications() {
        FirebaseRecyclerOptions<RegisterCarModel>options = new FirebaseRecyclerOptions.Builder<RegisterCarModel>()
                .setQuery(cars.orderByChild("status").equalTo("Declined"), RegisterCarModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RegisterCarModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RegisterCarModel model) {
                holder.make.setText("Car Make: " + model.getMake());
                holder.model.setText("Car Model: " + model.getModel());
                holder.date_insured.setText(InsuranceUtils.dateFromLong(model.getInsuranceDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get crime id to new activity
                        Intent carDetail = new Intent(InsuranceApplications.this, AdminInsuranceApplicationDetails.class);
                        carDetail.putExtra("carId", adapter.getRef(position).getKey());
                        startActivity(carDetail);
                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cars_insured, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_insurance_application.setAdapter(adapter);
        adapter.startListening();
    }

    private void loadApprovedApplications() {
        FirebaseRecyclerOptions<RegisterCarModel>options = new FirebaseRecyclerOptions.Builder<RegisterCarModel>()
                .setQuery(cars.orderByChild("status").equalTo("Approved"), RegisterCarModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RegisterCarModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RegisterCarModel model) {
                holder.make.setText("Car Make: " + model.getMake());
                holder.model.setText("Car Model: " + model.getModel());
                holder.date_insured.setText(InsuranceUtils.dateFromLong(model.getInsuranceDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get crime id to new activity
                        Intent carDetail = new Intent(InsuranceApplications.this, AdminInsuranceApplicationDetails.class);
                        carDetail.putExtra("carId", adapter.getRef(position).getKey());
                        startActivity(carDetail);
                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cars_insured, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_insurance_application.setAdapter(adapter);
        adapter.startListening();
    }

    private void refreshInsuranceApplications() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InsuranceApplications.this);
        alertDialog.setTitle("Refresh");
        alertDialog.setMessage("Please fill the information correctly");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_chalet_layout = inflater.inflate(R.layout.refresh_insurance_applications_layout, null);

        editTextToRefresh = add_chalet_layout.findViewById(R.id.edtRefresh);
        // btnSelect = add_chalet_layout.findViewById(R.id.btnSelect);
        //btnUpload = add_chalet_layout.findViewById(R.id.btnUpload);

        alertDialog.setView(add_chalet_layout);
        //alertDialog.setIcon(R.drawable.ic_home_black_24dp);


        //set button
        alertDialog.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();



            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void loadInsuranceApplications() {
        FirebaseRecyclerOptions<RegisterCarModel>options = new FirebaseRecyclerOptions.Builder<RegisterCarModel>()
                .setQuery(cars.orderByChild("status").equalTo("Awaiting Approval"), RegisterCarModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<RegisterCarModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RegisterCarModel model) {
                holder.make.setText("Car Make: " + model.getMake());
                holder.model.setText("Car Model: " + model.getModel());
                holder.date_insured.setText(InsuranceUtils.dateFromLong(model.getInsuranceDate()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get crime id to new activity
                        Intent carDetail = new Intent(InsuranceApplications.this, AdminInsuranceApplicationDetails.class);
                        carDetail.putExtra("carId", adapter.getRef(position).getKey());
                        startActivity(carDetail);
                    }
                });

            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cars_insured, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }
        };
        recycler_insurance_application.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView make, model, date_insured;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            make = itemView.findViewById(R.id.make);
            model = itemView.findViewById(R.id.txt_model);
            date_insured = itemView.findViewById(R.id.txt_date_insured);

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
