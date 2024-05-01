package com.example.mytaskmanagement;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class AdapterContact extends RecyclerView.Adapter<AdapterContact.HolderContact> {
    private List<Contact> contactList;
    private Context context;

    public AdapterContact(List<Contact> contactList, Context context) {
        this.contactList = contactList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterContact.HolderContact onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item,parent,false);

        return new HolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterContact.HolderContact holder, int position) {
        Glide.with(context).load(contactList.get(position).getImg_url()).into(holder.ImageUser);
        holder.userName.setText(contactList.get(position).getUsername());
        holder.numberUser.setText(contactList.get(position).getTelephoneContact());

        holder.recCardContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String owner = contactList.get(holder.getAdapterPosition()).getOwner();
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(owner)){
                    Toast.makeText(context, "Going to detail Activity", Toast.LENGTH_SHORT).show();
                    intent = new Intent(context,Contact_Details.class);
                    intent.putExtra("Image",contactList.get(holder.getAdapterPosition()).getImg_url());
                    intent.putExtra("Username",contactList.get(holder.getAdapterPosition()).getUsername());
                    intent.putExtra("Email",contactList.get(holder.getAdapterPosition()).getEmailContact());
                    intent.putExtra("Telephone",contactList.get(holder.getAdapterPosition()).getTelephoneContact());
                    intent.putExtra("Description",contactList.get(holder.getAdapterPosition()).getDescriptionContact());
                    //intent.putExtra("Favoris",contactList.get(holder.getAdapterPosition()).getFavoris());
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void searchDataList(ArrayList<Contact> searchList){
        contactList = searchList;
        notifyDataSetChanged();
    }

    class HolderContact extends RecyclerView.ViewHolder{

        CardView recCardContact;
        ImageView ImageUser;
        TextView userName;
        TextView numberUser;
        public HolderContact(@NonNull View itemView) {
            super(itemView);

            recCardContact = itemView.findViewById(R.id.recCardContact);
            ImageUser = itemView.findViewById(R.id.imgUser);
            userName = itemView.findViewById(R.id.UserName_id);
            numberUser = itemView.findViewById(R.id.contact_number);
        }
    }
}
