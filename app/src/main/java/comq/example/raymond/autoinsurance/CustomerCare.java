package comq.example.raymond.autoinsurance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Date;

import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.MessageModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;

public class CustomerCare extends AppCompatActivity {
    private Toolbar customerCareToolbar;

    private EditText editTextMessage;
    private Button btnSend;

    private FirebaseAuth mAuth;
    private DatabaseReference database, messages;

    private RecyclerView recycler_messages;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseRecyclerAdapter<MessageModel, CustomerCareViewHolder> adapter;


    private String uId = "";
    private String uName = "";
    private ProgressDialog dialog;
    private MessageModel newMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_care);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("users");
        messages = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("messages");
        uId = mAuth.getCurrentUser().getUid();

        //toolbar
        //initialize our toolBar
        customerCareToolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(customerCareToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Contact Customer Care");

        recycler_messages = findViewById(R.id.recycler_complaints);
        recycler_messages.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_messages.setLayoutManager(layoutManager);




        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.buttonSend);

        dialog = new ProgressDialog(this);


        //getUser details
        getUserDetails();

        loadMessages();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendComplaint();
            }
        });
    }

    private void loadMessages() {

        FirebaseRecyclerOptions<MessageModel> options = new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(messages.orderByChild("uId").equalTo(uId), MessageModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<MessageModel, CustomerCareViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CustomerCareViewHolder holder, int position, @NonNull MessageModel model) {
                holder.txtMessage.setText(model.getMessage());
                String name = model.getUserName().toString();
                if (name.equals(uName)){
                    holder.txtUserName.setText("You Sent:");
                }else {
                    holder.txtUserName.setText("Customer Care:");
                }

                //holder.txtUserName.setText(model.getUserName());
                holder.txtDate.setText(InsuranceUtils.dateFromLong(model.getDateSent()));

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

            }

            @NonNull
            @Override
            public CustomerCareViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.customer_complaint_layout, viewGroup,false);
                CustomerCareViewHolder viewHolder = new CustomerCareViewHolder(view);
                return viewHolder;
            }
        };
        recycler_messages.setAdapter(adapter);
        adapter.startListening();

    }

    private void sendComplaint() {
        String message = editTextMessage.getText().toString().trim();

        final long dateSent = new Date().getTime();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Type your message ", Toast.LENGTH_SHORT).show();
        }else {
            dialog.setMessage("Sending message...");
            dialog.show();
            newMessage = new MessageModel(uId, uName, message, dateSent);

            messages.push().setValue(newMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    Toast.makeText(CustomerCare.this, "Message sent!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CustomerCare.this, CustomerCare.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(CustomerCare.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getUserDetails() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                uName = dataSnapshot.child(uId).child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static class CustomerCareViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUserName, txtDate, txtMessage;
        private ItemClickListener itemClickListener;
        public CustomerCareViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.txt_user_name);
            txtDate = itemView.findViewById(R.id.txt_time);
            txtMessage= itemView.findViewById(R.id.txt_message);

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
