package com.example.mytaskmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ListdesTasksFragment extends Fragment{

    private List<DataTasks> listTasks;
    private RecyclerView taskRecyclerView;
    private MyAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    public ListdesTasksFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_listdes_tasks, container, false);

        taskRecyclerView = rootview.findViewById(R.id.taskRecyclerView);
        listTasks = new LinkedList<DataTasks>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getTasks();

        return rootview;
    }

    private void getTasks() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("User").document(currentUser.getEmail());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
        docRef.collection("Tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                DataTasks tsk = new DataTasks(document.get("title").toString(),document.get("description").toString(),document.get("deadline").toString(),document.get("image").toString(),document.get("owner").toString());
                                listTasks.add(tsk);
                            }
                            taskRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            taskRecyclerView.setLayoutManager(layoutManager);

                            adapter = new MyAdapter(getActivity(),listTasks);
                            taskRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();


                        }else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                        dialog.dismiss();
                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();
    }
}