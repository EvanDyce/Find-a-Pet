package com.evandyce.pettinder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evandyce.pettinder.cards.Animal;
import com.evandyce.pettinder.main.fragments.FavoritesFragment;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AnimalViewHolder> {

    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView animalName;
        TextView animalLocation;
        TextView animalContact;
        TextView animalDescription;
        ImageView animalImage;
        Button seeMore;
        ImageButton remove;

        AnimalViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardview);
            animalName = (TextView)itemView.findViewById(R.id.cardview_name);
            animalLocation = (TextView)itemView.findViewById(R.id.cardview_location);
            animalContact = (TextView)itemView.findViewById(R.id.cardview_contact);
            animalDescription = (TextView)itemView.findViewById(R.id.cardview_description_content);
            animalImage = (ImageView)itemView.findViewById(R.id.cardview_image);
            seeMore = (Button)itemView.findViewById(R.id.see_more_button);
            remove = (ImageButton) itemView.findViewById(R.id.remove_card_rv);
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
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animals.remove(position);
                notifyDataSetChanged();

                FavoritesFragment.emptyList(animals);
            }
        });

        holder.seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(animals.get(position).getPetfinderURL())));
            }
        });

        holder.animalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageDialog(animals.get(position).getImageUrl());
            }
        });

        holder.animalName.setText(animals.get(position).getName());
        holder.animalLocation.setText(animals.get(position).getLocation());
        holder.animalContact.setText(animals.get(position).getEmail());
        holder.animalDescription.setText(animals.get(position).getDescription());
        Glide.with(this.context).load(animals.get(position).getImageUrl()).into(holder.animalImage);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void showImageDialog(String url) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT)
        );

        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(url)
                .into(imageView);

        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }
}
