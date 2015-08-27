package jp.co.infocity.paletteexample;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author kaji
 * @since 15/08/27.
 */
public class PageFragment extends Fragment {

    @Bind(R.id.image)  ImageView mImageView;
    @Bind(R.id.colors) LinearLayout mColorsLayout;
    @Bind(R.id.title)  TextView mTitleView;
    @Bind(R.id.body)   TextView mBodyView;

    public static PageFragment newInstance(int position, String path) {
        PageFragment f = new PageFragment();
        f.setArguments(new Bundle());
        f.getArguments().putInt("position", position);
        f.getArguments().putString("path", path);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());

        int position = getArguments().getInt("position");

        File imageFile = new File(getArguments().getString("path"));
        Glide.with(this)
                .load(imageFile)
                .asBitmap()
                .listener(new RequestListener<File, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, File model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, File model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette p) {
                                Palette.Swatch vibrant = p.getVibrantSwatch();
                                if (vibrant != null) {
                                    mColorsLayout.setBackgroundColor(vibrant.getRgb());
                                    mTitleView.setTextColor(vibrant.getTitleTextColor());
                                    mBodyView.setTextColor(vibrant.getBodyTextColor());
                                } else {
                                    Palette.Swatch swatch = p.getSwatches().get(0);
                                    mColorsLayout.setBackgroundColor(swatch.getRgb());
                                    mTitleView.setTextColor(swatch.getTitleTextColor());
                                    mBodyView.setTextColor(swatch.getBodyTextColor());
                                }
                            }
                        });
                        return false;
                    }
                })
                .into(mImageView);
    }

}
