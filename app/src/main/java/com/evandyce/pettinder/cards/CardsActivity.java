package com.evandyce.pettinder.cards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
//import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.evandyce.pettinder.R;
import com.evandyce.pettinder.api.APIConnector;
import com.evandyce.pettinder.main.MainActivity;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.List;
import java.util.Objects;

public class CardsActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private static List<Animal> animalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // reset the index tracker for favorites to 0
        TinderCard.setIndex(0);

        // initialize view and context
        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        // get screen size
        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());

        // add back button
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8000ff")));
        actionBar.setDisplayHomeAsUpEnabled(true);

        // adjust the swipeview to fit the size of window and other configurations
        mSwipeView.getBuilder()
                .setDisplayViewCount(5)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeMaxChangeAngle(2f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        animalList = APIConnector.getAnimalList();
        // for each dog in the list add the card to the view
        for(Animal animal : animalList) {
            mSwipeView.addView(new TinderCard(mContext, animal, mSwipeView));
        }

        // adds the end card that shows that no more are around
        Animal end = new Animal();
        end.setImageUrl("https://www.liveabout.com/thmb/uvBVFWHAjpClFQ18Sink0O8_9j0=/641x0/filters:no_upscale():max_bytes(150000):strip_icc():format(webp)/yes-this-is-dog-56a4f6725f9b58b7d0da1af4.png");
        end.setName("There are no more pets available at this location");
        end.setAge("To view more increase the range or search for a new city");
        end.setLocation("");
        mSwipeView.addView(new TinderCard(mContext, end, mSwipeView));

        // listeners for accept, reject, and undo buttons
        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        //Todo: add the liked dogs to a new list of favorited dogs to persist
        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.undoLastSwipe();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        this.startActivity(new Intent(this, MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    public static Animal getAnimalFromIndex(int index) {
        return animalList.get(index);
    }
}
