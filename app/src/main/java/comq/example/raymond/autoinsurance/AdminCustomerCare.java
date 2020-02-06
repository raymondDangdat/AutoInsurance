package comq.example.raymond.autoinsurance;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import comq.example.raymond.autoinsurance.Common.Common;
import comq.example.raymond.autoinsurance.Interface.ItemClickListener;
import comq.example.raymond.autoinsurance.Model.MessageModel;
import comq.example.raymond.autoinsurance.Utils.InsuranceUtils;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminCustomerCare extends AppCompatActivity {
    private Toolbar customerCareToolbar;

    private EditText editTextMessage;
    private Button btnSend;

    private FirebaseAuth mAuth;
    private DatabaseReference database, messages;

    
    private FirebaseRecyclerAdapter<MessageModel, ViewHolder>adapter;
    private RecyclerView recycler_messages;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_customer_care);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("users");
        messages = FirebaseDatabase.getInstance().getReference().child("autoInsurance").child("messages");

        messages.keepSynced(true);
        //toolbar
        //initialize our toolBar
        customerCareToolbar = findViewById(R.id.customer_toolbar);
        setSupportActionBar(customerCareToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Customer Complaints");

        recycler_messages = findViewById(R.id.recycler_complaints);
        recycler_messages.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_messages.setLayoutManager(layoutManager);
        
        loadMessages();

    }

    private void loadMessages() {
        FirebaseRecyclerOptions<MessageModel> options = new FirebaseRecyclerOptions.Builder<MessageModel>()
                .setQuery(messages, MessageModel.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<MessageModel, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MessageModel model) {
                holder.txt_date_sent.setText(InsuranceUtils.dateFromLong(model.getDateSent()));
                holder.txt_complaint.setText(model.getMessage());
                holder.txt_customer_name.setText(model.getUserName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(AdminCustomerCare.this, "Good", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cus_comp_layout, viewGroup,false);
                ViewHolder viewHolder = new ViewHolder(view);
                return viewHolder;
            }


        };
        recycler_messages.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.REPLY)){
            replyMessage(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void replyMessage(String key) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_customer_name, txt_date_sent, txt_complaint;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_customer_name = itemView.findViewById(R.id.txt_customer_name);
            txt_complaint = itemView.findViewById(R.id.txt_complaint);
            txt_date_sent = itemView.findViewById(R.id.txt_dated_sent);
            

            itemView.setOnClickListener(this);
            //itemView.setOnCreateContextMenuListener(this);

        }


        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }


//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Reply Message");
//            menu.add(0,0, getAdapterPosition(), Common.REPLY);
//
//        }
    }
}
