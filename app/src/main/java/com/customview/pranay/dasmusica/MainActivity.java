package com.customview.pranay.dasmusica;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.customview.pranay.dasmusica.fragment.DashBoardFragment;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity implements DashBoardFragment.BtnmoreClicked{

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;
    private HollyViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);

        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.primaryFrame,new DashBoardFragment(),"DashBoard Fragment");
        transaction.commit();

       /* viewPager = (HollyViewPager) findViewById(R.id.hollyViewPager);
        viewPager.setConfigurator(new HollyViewPagerConfigurator() {
            @Override
            public float getHeightPercentForPage(int page) {
                return ((page+4)%10)/10f;
            }
        });
        viewPager.setAdapter(new MyAdapter(this));*/
    }

    @Override
    public void btnClicked(boolean clicked) {
        if (clicked){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.primaryFrame,new SongsListFragment(),"SongsList Fragment");
            transaction.addToBackStack(null);
            transaction.commit();
           // viewPager.setVisibility(View.VISIBLE);
        }
    }

    public class MyAdapter extends PagerAdapter {

        Context context;
        LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return MusicPOJO.getInstance().getSongsList()==null?0:MusicPOJO.getInstance().getSongsList().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((LinearLayout) object);

        }

        @Override
        public Object instantiateItem(View container, int position) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.viewpager_image,null,true);
            ImageView imgImg= (ImageView) view.findViewById(R.id.ivp);
            getAlbumArtWithoutLibrary(MusicPOJO.getInstance().getSongsList().get(position).getPath(),imgImg);
            ((ViewPager) container).addView(view);
            return view;
        }
        private void getAlbumArtWithoutLibrary(String s, ImageView albumArt) {
            android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(s);

            byte [] data = mmr.getEmbeddedPicture();
            if(data != null)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                albumArt.setImageBitmap(bitmap);
                albumArt.setAdjustViewBounds(true);
            }
            else
            {
                albumArt.setImageResource(R.drawable.guitar);
                albumArt.setAdjustViewBounds(true);
            }
        }

    }
}
