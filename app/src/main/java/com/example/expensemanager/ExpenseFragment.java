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

public class ExpenseFragment extends Fragment {

    private static final String TAG = "ExpenseFragment";

    private DatabaseReference mExpenseDatabase;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Data, MyViewHolder> adapter;

    //Text view

    private TextView expenseSumResult;

    //Edt data item;

    private EditText edtAmmount;
    private EditText edtType;
    private EditText edtNote;

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

        View myview = inflater.inflate(R.layout.fragment_expense, container, false);

        // Convert FirebaseAuth mAuth to a local variable
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            String uid = mUser.getUid();
            Log.d(TAG, "onCreateView: User ID: " + uid);

            mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);
        } else {
            Log.e(TAG, "onCreateView: User is null, cannot get UID");
            Toast.makeText(getActivity(), "User not signed in. Please sign in first.", Toast.LENGTH_SHORT).show();
            return myview;
        }

        expenseSumResult = myview.findViewById(R.id.expense_txt_result);

        recyclerView = myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int Expensesum = 0;

                for (DataSnapshot mysanapshot:dataSnapshot.getChildren()){

                    Data data=mysanapshot.getValue(Data.class);
                    Expensesum+=data.getAmount();
                    String strExpensesum=String.valueOf(Expensesum);

                    expenseSumResult.setText(Expensesum+".000 VND");
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
                        .setQuery(mExpenseDatabase, Data.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                Log.d(TAG, "onCreateViewHolder: creating view holder");
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.expense_recycle_data, parent, false);
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

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ExpenseFragment.this.postKey = postKey;
                        amount = model.getAmount();
                        type = model.getType();
                        note = model.getNote();
                        date = model.getDate();

                        // Handle click event here
                        updateDataItem(); // Call update method
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setType(String type) {
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        public void setNote(String note) {
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        public void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        public void setAmount(int amount) {
            TextView mAmount = mView.findViewById(R.id.ammount_txt_expense);
            mAmount.setText(String.valueOf(amount));
        }
    }

    private void updateDataItem(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View myview=inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);

        edtAmmount=myview.findViewById(R.id.ammount_edt);
        edtNote=myview.findViewById(R.id.note_edt);
        edtType=myview.findViewById(R.id.type_edt);

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
                mExpenseDatabase.child(postKey).setValue(updatedData).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mExpenseDatabase.child(postKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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

