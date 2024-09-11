package com.example.expensemanager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.expensemanager.Model.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StaticsFragment extends Fragment {

    //Firebase...

    private DatabaseReference mIncomeDatabase;
    private DatabaseReference mExpenseDatabase;
    private AnyChartView anyChartView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statics, container, false);

        // Initialize Firebase
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Toast.makeText(getContext(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return view;
        }

        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseDatabase").child(uid);

        // Initialize AnyChartView
        anyChartView = view.findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(view.findViewById(R.id.progress_bar));

        // Set click listeners for Income and Expense buttons
        Button btnIncome = view.findViewById(R.id.btn_income);
        Button btnExpense = view.findViewById(R.id.btn_expense);

        btnIncome.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Income button clicked", Toast.LENGTH_SHORT).show();
            loadIncomeData();
        });
        btnExpense.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Expense button clicked", Toast.LENGTH_SHORT).show();
            loadExpenseDatabase();
        });

        return view;
    }


    private void loadIncomeData() {
        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataEntry> data = new ArrayList<>();

                // Loop through all income entries in Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data income = snapshot.getValue(Data.class);
                    if (income != null) {
                        Log.d("StaticsFragment", "Income data: " + income.getType() + ", " + income.getAmount());
                        // Add income data to the chart
                        data.add(new ValueDataEntry(income.getType(), income.getAmount()));
                    }
                }

                // Update pie chart with income data
                Pie pie = AnyChart.pie();
                pie.data(data);
                pie.title("Income Statistics");

                anyChartView.setChart(pie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenseDatabase() {
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<DataEntry> data = new ArrayList<>();

                // Loop through all expense entries in Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Data expense = snapshot.getValue(Data.class);
                    if (expense != null) {
                        Log.d("StaticsFragment", "Expense data: " + expense.getType() + ", " + expense.getAmount());
                        // Add income data to the chart
                        data.add(new ValueDataEntry(expense.getType(), expense.getAmount()));
                    }
                }

                // Update pie chart with expense data
                Pie pie = AnyChart.pie();
                pie.data(data);
                pie.title("Expense Statistics");

                anyChartView.setChart(pie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
