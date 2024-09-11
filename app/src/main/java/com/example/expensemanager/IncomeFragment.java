package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;


public class IncomeFragment extends Fragment {

    private static final String TAG = "IncomeFragment";

    private DatabaseReference mIncomeDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;

    //Text view

    private TextView incomeTotalSum;

    ///Update edit text.

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

    //button for update and delete;

    private Button btnUpdate;
    private Button btnDelete;

    private String postKey;
    private int amount;
    private String type;
    private String note;
    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");

        View myview = inflater.inflate(R.layout.fragment_income, container, false);

        // Convert FirebaseAuth mAuth to a local variable
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            String uid = mUser.getUid();
            mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        } else {
            Toast.makeText(getActivity(), "User not signed in. Please sign in first.", Toast.LENGTH_SHORT).show();
            return myview;
        }

        incomeTotalSum = myview.findViewById(R.id.income_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_income);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totlatvalue = 0;

                for (DataSnapshot mysanapshot:dataSnapshot.getChildren()){

                    Data data=mysanapshot.getValue(Data.class);

                    totlatvalue+=data.getAmount();

                    String stTotavale=String.valueOf(totlatvalue);

                    incomeTotalSum.setText(stTotavale+".000 VND");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d(TAG, "onCreateView: RecyclerView initialized");

        return myview;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onStart: started");

        FirebaseRecyclerOptions<Data> options =
                new FirebaseRecyclerOptions.Builder<Data>()
                        .setQuery(mIncomeDatabase, Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: creating view holder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.income_recycle_data, parent, false);
                return new MyViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                Log.d(TAG, "onBindViewHolder: binding data at position " + position);

                holder.setType(model.getType());
                holder.setNote(model.getNote());
                holder.setDate(model.getDate());
                holder.setAmount(model.getAmount());

                String postKey = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IncomeFragment.this.postKey = postKey;
                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        date = model.getDate();

                        // Handle click event here
                        updateDateItem(); // Call update method
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    //ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }

        public void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.ammount_txt_income);
            mAmount.setText(String.valueOf(amount));
        }
    }

    private void updateDateItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtType=myview.findViewById(R.id.type_edt);
        edtNote=myview.findViewById(R.id.note_edt);

        btnUpdate=myview.findViewById(R.id.btn_upd_Update);
        btnDelete=myview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog=mydialog.create();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(edtAmmount.getText().toString().trim());
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                Data updatedData = new Data(amount, type, note, postKey, date);
                mIncomeDatabase.child(postKey).setValue(updatedData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Data Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        dialog.show();
    }
});
        // Handle Delete button click
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIncomeDatabase.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Data Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Delete Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}














