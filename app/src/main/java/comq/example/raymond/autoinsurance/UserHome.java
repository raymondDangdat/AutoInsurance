package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.ReportModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DatabaseReference users;

    private TextView txtFullname;
    private CircleImageView profilePic;

    private ProgressDialog dialogChangePassword;

    private String uId, userEmail = "";

    private FirebaseRecyclerAdapter<ReportModel, ClaimsViewHolder> adapter;
    private DatabaseReference claims;
    private RecyclerView recycler_claims;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dialogChangePassword = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        users = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("users");

        claims = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("claims");
        claims.keepSynced(true);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                View headerView = navigationView.getHeaderView(0);
                txtFullname = headerView.findViewById(R.id.user_name);
                profilePic = headerView.findViewById(R.id.imageView);

                String fullname = dataSnapshot.child(uId).child("name").getValue(String.class);
                String profilePicture = dataSnapshot.child(uId).child("profile_pix").getValue(String.class);

                //set text
                txtFullname.setText(fullname);
                Picasso.get().load(profilePicture).placeholder(R.drawable.benz).into(profilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        recycler_claims = findViewById(R.id.recycler_claims);
        recycler_claims.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_claims.setLayoutManager(layoutManager);


        loadClaims();
    }


    private void loadClaims() {
        FirebaseRecyclerOptions<ReportModel> options = new FirebaseRecyclerOptions.Builder<ReportModel>()
                .setQuery(claims.orderByChild("uId").equalTo(uId), ReportModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<ReportModel, ClaimsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ClaimsViewHolder holder, int position, @NonNull ReportModel model) {
                holder.txtModel.setText("Car Model: " + model.getModel());
                holder.txtName.setText("Car Owner: " + model.getName());
                holder.txtAmountPaid.setText("Amount Paid: " + String.valueOf(model.getAmountPaid()));
                holder.txtDatePaid.setText("Date Paid: " + InsuranceUtils.dateFromLong(model.getPaymentDate()));
                holder.txtMake.setText("Car Make: " + model.getMake());
                holder.txtReportDate.setText("Date Reported: " + InsuranceUtils.dateFromLong(model.getReportDate()));
                holder.txtPolicyType.setText("Policy Type: " + model.getPolicyType());
                holder.txtValue.setText("Car Value: " + String.valueOf(model.getValue()));
                holder.txtUse.setText("Car Use: " + model.getUse());
                holder.txtNoPlate.setText("No. Plate: " + model.getNoPlate());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(UserHome.this, "clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public ClaimsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.claims_layout, viewGroup,false);
                ClaimsViewHolder viewHolder = new ClaimsViewHolder(view);
                return viewHolder;
            }
        };
        recycler_claims.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_insure_car) {
            startActivity(new Intent(UserHome.this, RegisterCar.class));
            // Handle the camera action
        } else if (id == R.id.nav_cars_insured) {
            startActivity(new Intent(UserHome.this, UInsuredCars.class));

        } else if (id == R.id.nav_insurance_policy) {
            startActivity(new Intent(UserHome.this, InsurancePolicy.class));

        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(UserHome.this, UProfile.class));

        } else if (id == R.id.nav_customer_care) {
            startActivity(new Intent(UserHome.this, CustomerCare.class));

        } else if (id == R.id.nav_exit) {
            //logout
            mAuth.getCurrentUser();
            mAuth.signOut();
            finish();
            Intent signoutIntent = new Intent(UserHome.this, MainActivity.class);
            signoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signoutIntent);
            finish();

        }else if (id == R.id.nav_change_password){
            changePassword();
        }else if (id == R.id.nav_payments){
            startActivity(new Intent(UserHome.this, PaymentDetailsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePassword() {
        userEmail = mAuth.getCurrentUser().getEmail();
        dialogChangePassword.setMessage("Sending password reset link to " + userEmail);
        dialogChangePassword.show();


        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialogChangePassword.dismiss();
                    Toast.makeText(UserHome.this, "Reset email sent to "+ userEmail, Toast.LENGTH_SHORT).show();
                    //logout
                    Intent signIn = new Intent(UserHome.this, MainActivity.class);
                    signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mAuth.signOut();
                    startActivity(signIn);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialogChangePassword.dismiss();
                Toast.makeText(UserHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserHome.this, UserHome.class));
                finish();

            }
        });
    }


    public static class ClaimsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtName, txtAmountPaid, txtMake, txtModel, txtNoPlate, txtUse, txtPolicyType, txtValue,  txtDatePaid, txtReportDate;
        private ItemClickListener itemClickListener;
        public ClaimsViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMake = itemView.findViewById(R.id.make);
            txtAmountPaid = itemView.findViewById(R.id.txt_amount_paid);
            txtDatePaid = itemView.findViewById(R.id.txt_date_paid);
            txtName = itemView.findViewById(R.id.owner_name);
            txtModel = itemView.findViewById(R.id.model);
            txtNoPlate = itemView.findViewById(R.id.txt_no_plate);
            txtUse = itemView.findViewById(R.id.txt_use);
            txtPolicyType = itemView.findViewById(R.id.txt_policy_type);
            txtValue = itemView.findViewById(R.id.txt_value);
            txtReportDate = itemView.findViewById(R.id.txt_report_date);

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
