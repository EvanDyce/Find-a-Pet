package com.evandyce.pettinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evandyce.pettinder.cards.Animal;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AnimalViewHolder> {

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView animalName;
        TextView animalLocation;
        TextView animalContact;
        TextView animalDescription;
        ImageView animalImage;

        AnimalViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardview);
            animalName = (TextView)itemView.findViewById(R.id.cardview_name);
            animalLocation = (TextView)itemView.findViewById(R.id.cardview_location);
            animalContact = (TextView)itemView.findViewById(R.id.cardview_contact);
            animalDescription = (TextView)itemView.findViewById(R.id.cardview_description_content);
            animalImage = (ImageView)itemView.findViewById(R.id.cardview_image);
        }
    }

    List<Animal> animals;
    Context context;

    public RVAdapter(Context context, List<Animal> animalList) {
        this.context = context;
        this.animals = animalList;
    }

    @Override
    public int getItemCount() {
        return this.animals.size();
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_cardview, parent, false);
        AnimalViewHolder avh = new AnimalViewHolder(v);
        return avh;
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        holder.animalName.setText(animals.get(position).getName());
        holder.animalLocation.setText(animals.get(position).getLocation());
        holder.animalContact.setText(animals.get(position).getEmail());
        holder.animalDescription.setText("This is the temp description.");
        Glide.with(this.context).load(animals.get(position).getImageUrl()).into(holder.animalImage);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
