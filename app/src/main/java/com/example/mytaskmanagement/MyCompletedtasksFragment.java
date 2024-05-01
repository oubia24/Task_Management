package com.example.mytaskmanagement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class MyCompletedtasksFragment extends Fragment {

    private List<DataTasks> tasksList;
    private RecyclerView completedRecyclerView;
    private MyAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    SearchView completedTaskSearch;


    public MyCompletedtasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_completedtasks, container, false);
        completedRecyclerView = view.findViewById(R.id.completedRecyclerView);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tasksList = new LinkedList<DataTasks>();
        completedTaskSearch = view.findViewById(R.id.completedTask_search);
        completedTaskSearch.clearFocus();

        completedTaskSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCompletedTask(newText);
                return false;
            }
        });

        getcompltedTask();


        return view;

    }

    private void searchCompletedTask(String text) {
        ArrayList<DataTasks> searchCompleted = new ArrayList<>();

        for (DataTasks datask : tasksList){
            if ((datask.getDataTitle().toLowerCase().contains(text))){
                searchCompleted.add(datask);
            }
        }
        adapter.searchDatalist(searchCompleted);
    }

    private void getcompltedTask() {
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
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if ((Boolean) document.get("done") == true){
                                    DataTasks tsk = new DataTasks(document.get("title").toString(),document.get("description").toString(),document.get("deadline").toString(),document.get("image").toString(),document.get("owner").toString(),(Boolean) document.get("done"));
                                    tasksList.add(tsk);
                                }
                            }

                            completedRecyclerView.setHasFixedSize(false);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                            completedRecyclerView.setLayoutManager(layoutManager);
                            adapter = new MyAdapter(getActivity(),tasksList);
                            completedRecyclerView.setAdapter(adapter);
                        }
                        else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                        dialog.dismiss();
                    }
                });
    }
}