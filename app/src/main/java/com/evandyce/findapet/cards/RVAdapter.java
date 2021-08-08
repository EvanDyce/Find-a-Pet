package com.evandyce.findapet.cards;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.evandyce.findapet.Utils;
import com.evandyce.findapet.main.fragments.FavoritesFragment;
import com.evandyce.findapet.R;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.AnimalViewHolder> {

    /**
     * class for each object inside of the recyclerview on liked fragment
     */
    public static class AnimalViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView animalName;
        TextView animalLocation;
        TextView animalContact;
        TextView animalDescription;
        ImageView animalImage;
        Button seeMore;
        Button contact;
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
            contact = (Button) itemView.findViewById(R.id.contact_button);
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

    /**
     * called when teh viewholder is inflated into the fragment
     *
     * @param parent viewgroup for the layout to be inflated into
     * @param viewType int type of view
     * @return returns a new animal view holder. The layout for each dog in liked.
     */
    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_cardview, parent, false);
        AnimalViewHolder avh = new AnimalViewHolder(v);
        return avh;
    }

    /**
     * called when each animal is bound to a animal view holder and puts them in the recycler view
     *
     * @param holder AnimalViewHolder for the animal in question. Represents the whole card. Has attributes for the buttons and imageview
     * @param position position of the animal in the list. Used to remove proper view holder and update the list
     */
    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {

        // on click for the delete button on each list.
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animals.remove(position);
                notifyDataSetChanged();

                FavoritesFragment.emptyList(animals);
            }
        });

        // on lick for teh see more button
        // opens the url link to the petfinder url
        holder.seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = animals.get(position).getPetfinderURL();
                if (url == null || url.length() == 0) {
                    Utils.popupMessageFailure(context, "No additional information is available for this dog.");
                    return;
                }
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        // on click for the contact button
        // opens a mailto for the email that the api retrieved
        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = animals.get(position).getEmail();
                String name = animals.get(position).getName();
                if (email == null || email.length() == 0) {
                    Utils.popupMessageFailure(context, "No contact information for this dog.");
                    return;
                }
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email + "?subject=Inquiry About " + name)));
            }
        });

        // when teh image is clicked on then it fullscreens the image in a different imageview and tints the background
        holder.animalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FavoritesFragment.setFullScreenImage(animals.get(position).getImageUrl(), context);
            }
        });

        // sets the information for each of the cards in the recycler view
        holder.animalName.setText(animals.get(position).getName());
        holder.animalLocation.setText(animals.get(position).getLocation());
        if (animals.get(position).getEmail() == null || animals.get(position).getEmail().length() == 0) {
            holder.animalContact.setText(R.string.NoEmail);
        } else {
            holder.animalContact.setText(animals.get(position).getEmail());
        }
        holder.animalDescription.setText(animals.get(position).getDescription());
        Glide.with(this.context).load(animals.get(position).getImageUrl()).into(holder.animalImage);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
