package com.evandyce.pettinder.cards;

import android.content.Context;
import android.graphics.Point;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.evandyce.pettinder.R;
import com.evandyce.pettinder.classes.APIConnector;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import com.evandyce.pettinder.classes.Dog;

import org.json.JSONArray;

import java.util.List;
import java.util.Objects;

public class CardsActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_activty);

        // initialize view and context
        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        // get screen size
        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());

        // adjust the swipeview to fit the size of window and other configurations
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
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

        // for each dog in the list add the card to the view
        for(Dog dog : Objects.requireNonNull(APIConnector.getDogList())){
            //dog.setImageUrl("https://user-images.githubusercontent.com/24848110/33519396-7e56363c-d79d-11e7-969b-09782f5ccbab.png");
            mSwipeView.addView(new TinderCard(mContext, dog, mSwipeView));
        }

        // adds the end card that shows that no more are around
        Dog end = new Dog();
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
}
