package com.example.expensetrackerapp;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.expensetrackerapp.Model.Cat2;
import com.example.expensetrackerapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  method to
 * create an instance of this fragment.
 */
public class ExpenseFragment extends Fragment {

    //Firebase database...

    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;
    private DatabaseReference mExpCatDiv;
    private DatabaseReference mCategoryDatabase;
    private int ans,intammount;

    //Recyclerview
    private RecyclerView recyclerView;

    //Text View
    private TextView expenseSumResult;

    //Update
    private EditText edtAmmount;
    private Spinner edtType;
    private  EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;


    private String type;
    private String note;
    private String date;
    private int ammount;

    private String post_key;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview= inflater.inflate(R.layout.fragment_expense, container, false);
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser mUser=mAuth.getCurrentUser();
        String uid=mUser.getUid();
        mExpenseDatabase= FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        mExpCatDiv=FirebaseDatabase.getInstance().getReference().child("ExpenseCategoryDivision").child(uid);
        mCategoryDatabase=FirebaseDatabase.getInstance().getReference().child("Category").child(uid);

        expenseSumResult=myview.findViewById(R.id.expense_txt_result);

        recyclerView=myview.findViewById(R.id.recycler_id_expense);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());

        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int expenseSum=0;

                for(DataSnapshot mysnapshot:dataSnapshot.getChildren())
                {
                    Data data=mysnapshot.getValue(Data.class);
                    expenseSum+=data.getAmount();
                    String strExpensesum=String.valueOf(expenseSum);
                    expenseSumResult.setText(strExpensesum+".00");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return myview;

    }
    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder> adapter=new FirebaseRecyclerAdapter<Data, ExpenseFragment.MyViewHolder>(
                Data.class,
                R.layout.expense_recycler_data,
                ExpenseFragment.MyViewHolder.class,
                mExpenseDatabase
        )

        {

            @Override
            protected void populateViewHolder(ExpenseFragment.MyViewHolder viewHolder, Data model, int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                viewHolder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view) {

                        post_key=getRef(position).getKey();
                        type=model.getType();
                        note=model.getNote();
                        ammount=model.getAmount();
                        date=model.getDate();

                        updateDataItem();
                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView=itemView;
        }

        private void setType(String type){
            TextView mType=mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        private void setNote(String note)
        {
            TextView mNote=mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);

        }

        private void setDate(String date){
            TextView mDate=mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        private void setAmount(int amount)
        {
            TextView mAAmount=mView.findViewById(R.id.amount_txt_expense);
            String stramount=String.valueOf(amount);//strammount->stramount
            mAAmount.setText(stramount);
        }

    }

    private void updateDataItem()
    {

        ans=0;
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());
        View mview=inflater.inflate(R.layout.update_data_item,null);
        mydialog.setView(mview);

        edtAmmount=mview.findViewById(R.id.amount_edit);
        edtType=mview.findViewById(R.id.type_edit);
        edtNote=mview.findViewById(R.id.note_edit);

        // edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmmount.setText(String.valueOf(ammount));
        edtAmmount.setSelection(String.valueOf(ammount).length());


        btnUpdate=mview.findViewById(R.id.btn_upd_Update);
        btnDelete=mview.findViewById(R.id.btnuPD_Delete);

        AlertDialog dialog=mydialog.create();


        mCategoryDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> list = new ArrayList<>();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    String c=ds.child("category").getValue(String.class);
                    list.add(c);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item,list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    edtType.setAdapter(adapter);
                    edtType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                            ((TextView)adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String prevType=type;

                type=edtType.getSelectedItem().toString().trim();
                note=edtNote.getText().toString().trim();

                String stammount=String.valueOf(ammount);
                stammount=edtAmmount.getText().toString().trim();
                intammount=Integer.parseInt(stammount);

                if(prevType.equals(type))
                {
                    mExpCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 c=mysnap.getValue(Cat2.class);

                                if(c.getType().equals(type))
                                {
                                    mExpCatDiv.child(type).child("amount").setValue(c.getAmount()-ammount+intammount);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else if(!prevType.equals(type))
                {

                    mExpCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            for(DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 c=mysnap.getValue(Cat2.class);
                                if(c.getType().equals(prevType))
                                {
                                    ans+=c.getAmount();
                                    if(ans==intammount)
                                        mExpCatDiv.child(prevType).removeValue();
                                    else
                                        mExpCatDiv.child(prevType).child("amount").setValue(ans-intammount);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mExpCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            boolean flag = false;
                            for (DataSnapshot mysnap:snapshot.getChildren())
                            {
                                Cat2 t=mysnap.getValue(Cat2.class);
                                if(t.getType().equals(type))
                                {
                                    flag=true;
                                    int ans2=t.getAmount()+intammount;
                                    Cat2 c=new Cat2(ans2,type,type);
                                    mExpCatDiv.child(type).setValue(c);

                                }

                            }
                            if (flag==false)
                            {
                                Cat2 c=new Cat2(intammount,type,type);
                                mExpCatDiv.child(type).setValue(c);

                            }

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

               // String mDate= DateFormat.getDateInstance().format(new Date());

                Data data=new Data(intammount,type,note,post_key,date);

                mExpenseDatabase.child(post_key).setValue(data);

                dialog.dismiss();

            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {





                mExpCatDiv.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        for(DataSnapshot mysnap:snapshot.getChildren())
                        {
                            Cat2 c=mysnap.getValue(Cat2.class);
                            if(c.getType().equals(type))
                            {
                                ans+=c.getAmount();
                                if(ans==ammount)
                                    mExpCatDiv.child(type).removeValue();
                                else
                                    mExpCatDiv.child(type).child("amount").setValue(ans-ammount);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                mExpenseDatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
