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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FavorisContact extends Fragment {

    private List<Contact> contactList;
    private Context context;
    private AdapterContact adapterContact;
    private RecyclerView favorisRecycler;
    private SearchView searchFavoris;
    FirebaseFirestore db;
    FirebaseAuth mAuth;



    public FavorisContact() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favoris_contact, container, false);
        favorisRecycler = view.findViewById(R.id.favorisRecycler);
        searchFavoris = view.findViewById(R.id.search_favoris);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        contactList = new LinkedList<Contact>();

        searchFavoris.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchFavorisContact(newText);
                return false;
            }
        });

        getFavorisContact();
        return view;
    }

    private void searchFavorisContact(String text) {
        ArrayList<Contact> searchFavori = new ArrayList<>();
        for (Contact contact : contactList){
            if (contact.getUsername().toLowerCase().contains(text)){
                searchFavori.add(contact);
            }
        }
        adapterContact.searchDataList(searchFavori);
    }

    private void getFavorisContact() {
        DocumentReference docRef = db.collection("User").document(mAuth.getCurrentUser().getEmail());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        docRef.collection("Contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dialog.dismiss();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        if ((Boolean)document.get("isFavoris") == true){
                            Contact c = new Contact(document.get("userName").toString(),
                                    document.get("Email").toString(),document.get("Description").toString(),
                                    document.get("Telephone").toString(),document.get("Image").toString(),
                                    document.get("owner").toString(),(Boolean) document.get("isFavoris"));
                            contactList.add(c);
                        }
                    }

                    favorisRecycler.setHasFixedSize(false);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    favorisRecycler.setLayoutManager(layoutManager);
                    adapterContact = new AdapterContact(contactList,getActivity());
                    favorisRecycler.setAdapter(adapterContact);
                }else {
                    Log.d("not ok", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}