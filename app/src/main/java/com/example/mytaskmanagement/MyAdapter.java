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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataTasks> listTask;


    public MyAdapter(Context context, List<DataTasks> listTask) {
        this.context = context;
        this.listTask = listTask;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(listTask.get(position).getDataImage());
        Glide.with(context).load(listTask.get(position).getDataImage()).into(holder.recImage);
        holder.recTitle.setText(listTask.get(position).getDataTitle());
        holder.recDeadline.setText(listTask.get(position).getDataDeadline());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                String owner = listTask.get(holder.getAdapterPosition()).getOwner();
                if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(owner)){
                    Toast.makeText(context, "Going to detail Activity", Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, Task_Details.class);
                    intent.putExtra("Image",listTask.get(holder.getAdapterPosition()).getDataImage());
                    intent.putExtra("title",listTask.get(holder.getAdapterPosition()).getDataTitle());
                    intent.putExtra("description",listTask.get(holder.getAdapterPosition()).getDataDesc());
                    intent.putExtra("deadline",listTask.get(holder.getAdapterPosition()).getDataDeadline());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return listTask.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView recTitle;
    TextView recDeadline;
    CardView recCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDeadline = itemView.findViewById(R.id.recDeadline);
        recCard = itemView.findViewById(R.id.recCard);
    }
}