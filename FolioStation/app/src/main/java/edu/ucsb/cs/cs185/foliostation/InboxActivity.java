package edu.ucsb.cs.cs185.foliostation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import edu.ucsb.cs.cs185.foliostation.Inbox.InboxGridAdapter;
import edu.ucsb.cs.cs185.foliostation.collectiondetails.CollectionDetailsActivity;
import edu.ucsb.cs.cs185.foliostation.models.ItemCards;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardsFragment;
import edu.ucsb.cs.cs185.foliostation.mycollections.DetailBlurDialog;
import edu.ucsb.cs.cs185.foliostation.mycollections.GridCardAdapter;



public class InboxActivity extends AppCompatActivity {
    private final Activity mInboxActivity = this;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    InboxGridAdapter mGridCardAdapter;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("Shared With Me");

        mRecyclerView = (RecyclerView) findViewById(R.id.cards_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setItemPrefetchEnabled(true);

        //TODO: replace with your own adapter
        mGridCardAdapter = new InboxGridAdapter(ItemCards.getInstance(this).cards, mInboxActivity);
        mGridCardAdapter.setHasStableIds(true);

        mGridCardAdapter.setOnItemClickListener(new GridCardAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                ItemCards.Card card = ItemCards.getInstance(mInboxActivity).cards.get(position);

                if (card.hasMultiPics()){
                    Intent intent = new Intent(mInboxActivity, CollectionDetailsActivity.class);
                    intent.putExtra("CARD_INDEX", position);
                    startActivity(intent);
                } else {
                    startDetailDialog(position);
                }
            }
        });
    }

    protected void startDetailDialog(int position){
        Bundle arguments = new Bundle();
        arguments.putInt("CARD_INDEX", position);
        arguments.putString("FROM", "GRID");

        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = getSupportFragmentManager();

        fragment.show(ft, "dialog");

        Bitmap map = takeScreenShot(mInboxActivity);
        Bitmap fast = CardsFragment.BlurBuilder.blur(mInboxActivity, map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);
    }

    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }
}