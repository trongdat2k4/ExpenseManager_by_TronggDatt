package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import java.text.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


public class DashBoardFragment extends Fragment {

    //Floating button

    private FloatingActionButton fab_main_btn;
    private FloatingActionButton fab_income_btn;
    private FloatingActionButton fab_expense_btn;

    //Floating button textview...

    private TextView fab_income_txt;
    private TextView fab_expense_txt;

    //boolean

    private boolean isOpen=false;

    //Animation

    private Animation FadeOpen,FadeClose;

    //Dasboard income and expense

    private TextView totalIncomeResult;
    private TextView totalExpenseResult;

    //Firebase...

    private FirebaseAuth mAuth;
    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;

    //Recycler view

    private RecyclerView mRecyclerIncome;
    private RecyclerView mRecyclerExpense;


    private FirebaseRecyclerAdapter<Data, IncomeViewHolder> incomeAdapter;
    private FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_dash_board, container, false);

        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();

        mIncomeDatabase=FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        //Connect floating button to layout

        fab_main_btn=myview.findViewById(R.id.fb_main_plus_btn);
        fab_income_btn=myview.findViewById(R.id.income_Ft_btn);
        fab_expense_btn=myview.findViewById(R.id.expense_Ft_btn);

        //Connect floating button textview to layout

        fab_income_txt=myview.findViewById(R.id.income_ft_text);
        fab_expense_txt=myview.findViewById(R.id.expense_ft_text);

        //Total income and expense result set...

        totalIncomeResult=myview.findViewById(R.id.income_set_result);
        totalExpenseResult=myview.findViewById(R.id.expense_set_result);

        //Recycler

        mRecyclerIncome=myview.findViewById(R.id.recycler_income);
        mRecyclerExpense=myview.findViewById(R.id.recycler_expense);

        //Animation connect...

        FadeOpen= AnimationUtils.loadAnimation(getActivity(),R.anim.fade_open);
        FadeClose=AnimationUtils.loadAnimation(getActivity(),R.anim.fade_close);

        fab_main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addData();

                if (isOpen){

                    fab_income_btn.startAnimation(FadeClose);
                    fab_expense_btn.startAnimation(FadeClose);
                    fab_income_btn.setClickable(false);
                    fab_expense_btn.setClickable(false);

                    fab_income_txt.startAnimation(FadeClose);
                    fab_expense_txt.startAnimation(FadeClose);
                    fab_income_txt.setClickable(false);
                    fab_expense_txt.setClickable(false);

                    isOpen=false;

                }else {
                    fab_income_btn.startAnimation(FadeOpen);
                    fab_expense_btn.startAnimation(FadeOpen);
                    fab_income_btn.setClickable(true);
                    fab_expense_btn.setClickable(true);

                    fab_income_txt.startAnimation(FadeOpen);
                    fab_expense_txt.startAnimation(FadeOpen);
                    fab_income_txt.setClickable(true);
                    fab_expense_txt.setClickable(true);
                    isOpen=true;
                }
            }
        });

        //Calculate total income...

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalsum=0;

                for (DataSnapshot mysnap:dataSnapshot.getChildren()){

                    Data data=mysnap.getValue(Data.class);

                    totalsum+=data.getAmount();

                    String stResult=String.valueOf(totalsum);

                    totalIncomeResult.setText(stResult+".000 VND");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Calculate total expense...

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int totalsum=0;

                for (DataSnapshot mysnapshot:snapshot.getChildren()){

                    Data data=mysnapshot.getValue(Data.class);
                    totalsum+= data.getAmount();

                    String strTotalSum=String.valueOf(totalsum);

                    totalExpenseResult.setText(strTotalSum+".000 VND");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Recycler

        LinearLayoutManager layoutManagerIncome=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerIncome.setStackFromEnd(true);
        layoutManagerIncome.setReverseLayout(true);
        mRecyclerIncome.setHasFixedSize(true);
        mRecyclerIncome.setLayoutManager(layoutManagerIncome);

        LinearLayoutManager layoutManagerExpense=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);

        layoutManagerExpense.setStackFromEnd(true);
        layoutManagerExpense.setReverseLayout(true);
        mRecyclerExpense.setHasFixedSize(true);
        mRecyclerExpense.setLayoutManager(layoutManagerExpense);

        return myview;
    }

    //Floating button animation

    private void ftAnimation(){
        if (isOpen){

            fab_income_btn.startAnimation(FadeClose);
            fab_expense_btn.startAnimation(FadeClose);
            fab_income_btn.setClickable(false);
            fab_expense_btn.setClickable(false);

            fab_income_txt.startAnimation(FadeClose);
            fab_expense_txt.startAnimation(FadeClose);
            fab_income_txt.setClickable(false);
            fab_expense_txt.setClickable(false);

            isOpen=false;

        }else {
            fab_income_btn.startAnimation(FadeOpen);
            fab_expense_btn.startAnimation(FadeOpen);
            fab_income_btn.setClickable(true);
            fab_expense_btn.setClickable(true);

            fab_income_txt.startAnimation(FadeOpen);
            fab_expense_txt.startAnimation(FadeOpen);
            fab_income_txt.setClickable(true);
            fab_expense_txt.setClickable(true);
            isOpen = true;
        }
    }

    private void addData(){

        //Fab Button income...

        fab_income_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                incomeDataInsert();
            }
        });

        fab_expense_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                expenseDataInsert();

            }
        });

    }

    public void incomeDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());

        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myviewm=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myviewm);

        final AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText edtAmmount=myviewm.findViewById(R.id.ammount_edt);
        EditText edtType=myviewm.findViewById(R.id.type_edt);
        EditText edtNote=myviewm.findViewById(R.id.note_edt);

        Button btnSave=myviewm.findViewById(R.id.btnSave);
        Button btnCancel=myviewm.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type=edtType.getText().toString().trim();
                String ammount=edtAmmount.getText().toString().trim();
                String note=edtNote.getText().toString().trim();

                if (TextUtils.isEmpty(type)){
                    edtType.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(ammount)){
                    edtAmmount.setError("Required Field...");
                    return;
                }

                int ourammontint=Integer.parseInt(ammount);

                if (TextUtils.isEmpty(note)){
                    edtNote.setError("Required Field....");
                    return;
                }

                String id=mIncomeDatabase.push().getKey();

                String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(ourammontint,type,note,id,mDate);

                mIncomeDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data Insert Successfully",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ftAnimation();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void expenseDataInsert(){

        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_insertdata,null);
        mydialog.setView(myview);

        final  AlertDialog dialog=mydialog.create();

        dialog.setCancelable(false);

        EditText ammount=myview.findViewById(R.id.ammount_edt);
        EditText type=myview.findViewById(R.id.type_edt);
        EditText note=myview.findViewById(R.id.note_edt);

        Button btnSave=myview.findViewById(R.id.btnSave);
        Button btnCancel=myview.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tmAmmount=ammount.getText().toString().trim();
                String tmType=type.getText().toString().trim();
                String tmNote=note.getText().toString().trim();

                if (TextUtils.isEmpty(tmAmmount)){
                    ammount.setError("Required Field...");
                    return;
                }

                int inamount=Integer.parseInt(tmAmmount);

                if (TextUtils.isEmpty(tmType)){
                    type.setError("Required Field...");
                    return;
                }

                if (TextUtils.isEmpty(tmNote)){
                    note.setError("Required Field...");
                    return;
                }

                String id=mExpenseDatabase.push().getKey();

                String mDate=DateFormat.getDateInstance().format(new Date());

                Data data=new Data(inamount,tmType,tmNote,id,mDate);

                mExpenseDatabase.child(id).setValue(data);

                Toast.makeText(getActivity(),"Data Insert Successfully",Toast.LENGTH_SHORT).show();

                ftAnimation();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftAnimation();
                dialog.dismiss();
            }
    });
        dialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();

        // Set up Income Adapter

        FirebaseRecyclerOptions<Data> incomeOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mIncomeDatabase, Data.class)
                .build();

        incomeAdapter = new FirebaseRecyclerAdapter<Data, IncomeViewHolder>(incomeOptions) {
            @NonNull
            @Override
            public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashbroad_income, parent, false);
                return new IncomeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull IncomeViewHolder holder, int position, @NonNull Data model) {
                holder.setIncomeType(model.getType());  // Use holder instead of viewHolder
                holder.setIncomeAmmount(model.getAmount()); // Make sure the method name is correct
                holder.setIncomeDate(model.getDate()); // Use the correct method
            }
        };

        mRecyclerIncome.setAdapter(incomeAdapter);
        incomeAdapter.startListening();

        // Set up Expense Adapter

        FirebaseRecyclerOptions<Data> expenseOptions = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(mExpenseDatabase, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, ExpenseViewHolder> expenseAdapter = new FirebaseRecyclerAdapter<Data, ExpenseViewHolder>(expenseOptions) {
            @NonNull
            @Override
            public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dashboard_expense, parent, false);
                return new ExpenseViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Data model) {
                holder.setExpenseType(model.getType());  // Use holder instead of viewHolder
                holder.setExpenseAmmount(model.getAmount()); // Make sure the method name is correct
                holder.setExpenseDate(model.getDate()); // Use the correct method
            }
        };

        mRecyclerExpense.setAdapter(expenseAdapter);
        expenseAdapter.startListening();
    }

    //For Income Data

    public static class IncomeViewHolder extends RecyclerView.ViewHolder{

        View mIncomeView;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            mIncomeView=itemView;
        }

        public void setIncomeType(String type){
            TextView incomeType=mIncomeView.findViewById(R.id.type_Income_ds);
            incomeType.setText(type);
        }

        public void setIncomeAmmount(int ammount){
            TextView mAmmount=mIncomeView.findViewById(R.id.ammoun_income_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);

        }

        public void setIncomeDate(String date) {
            TextView mDate=mIncomeView.findViewById(R.id.date_income_ds);
            mDate.setText(date);
        }
    }

    //For Expense Data
    public static class ExpenseViewHolder extends RecyclerView.ViewHolder{

        View mExpenseView;

        public ExpenseViewHolder(View itemView) {
            super(itemView);
            mExpenseView=itemView;
        }

        public void setExpenseType(String type){
            TextView expenseType=mExpenseView.findViewById(R.id.type_Expense_ds);
            expenseType.setText(type);
        }

        public void setExpenseAmmount(int ammount){
            TextView mAmmount=mExpenseView.findViewById(R.id.ammoun_expense_ds);
            String strAmmount=String.valueOf(ammount);
            mAmmount.setText(strAmmount);

        }

        public void setExpenseDate(String date) {
            TextView mDate=mExpenseView.findViewById(R.id.date_expense_ds);
            mDate.setText(date);
        }
    }
}