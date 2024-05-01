package com.example.mytaskmanagement;

import android.app.AlertDialog;
import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ListofContacts extends Fragment {

    private RecyclerView contactRecycler;
    private Context context;
    private List<Contact> contactList;
    private AdapterContact adapterContact;
    private SearchView contactSearch;
    FirebaseAuth mAuth;
    FirebaseFirestore db;



    public ListofContacts() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listof_contacts, container, false);
        contactRecycler = view.findViewById(R.id.contactRecyclerView);
        contactSearch = view.findViewById(R.id.contact_Search);
        contactList = new LinkedList<Contact>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        contactSearch.clearFocus();

        contactSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContact(newText);
                return false;
            }
        });

        getContact();

        return view;
    }

    private void searchContact(String text) {
        ArrayList<Contact> searchContacts = new ArrayList<>();
        for (Contact contact : contactList){
            if(contact.getUsername().toLowerCase().contains(text)){
                searchContacts.add(contact);
            }
        }
        adapterContact.searchDataList(searchContacts);
    }

    public void refreshContacts() {
        Log.d("ListofContacts", "Refreshing contacts");
        contactList.clear(); // Clear the existing list
        getContact(); // Re-fetch contacts and update the RecyclerView
    }

    private void getContact() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("User").document(currentUser.getEmail());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        docRef.collection("Contacts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()){
                                Contact C = new Contact(document.get("userName").toString(),
                                        document.get("Email").toString(),
                                        document.get("Description").toString(),document.get("Telephone").toString(),document.get("Image").toString(),
                                        document.get("owner").toString(),(Boolean) document.get("isFavoris"));
                                contactList.add(C);
                            }

                            contactRecycler.setHasFixedSize(true);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            contactRecycler.setLayoutManager(linearLayoutManager);
                            adapterContact = new AdapterContact(contactList,getActivity());
                            contactRecycler.setAdapter(adapterContact);

                        }else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}