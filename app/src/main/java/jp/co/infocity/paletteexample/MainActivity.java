package jp.co.infocity.paletteexample;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUESTCODE_IMAGE = 1111;

    @Bind(R.id.tabs)  TabLayout mTabs;
    @Bind(R.id.pager) ViewPager mPager;

    private FragmentPagerAdapter mPagerAdapter;
    private List<File> mImageFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return PageFragment.newInstance(position, mImageFiles.get(position).getAbsolutePath());
            }

            @Override
            public int getCount() {
                return mImageFiles.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "IMAGE " + (position + 1);
            }
        };
        mPager.setAdapter(mPagerAdapter);

        mTabs.setTabTextColors(Color.LTGRAY, Color.WHITE);
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabs.setupWithViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.buttonAdd)
    public void onClickButtonAdd() {
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, REQUESTCODE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUESTCODE_IMAGE && resultCode == Activity.RESULT_OK) {
            ContentResolver cr = getContentResolver();
            String[] columns = { MediaStore.Images.Media.DATA };
            Cursor c = cr.query(data.getData(), columns, null, null, null);
            if (c != null) {
                if (c.moveToFirst()) {
                    addImageFile(new File(c.getString(0)));
                }
                c.close();
            }
        }
    }

    private void addImageFile(File srcFile) {
        mImageFiles.add(srcFile);
        mPagerAdapter.notifyDataSetChanged();
        mTabs.addTab(mTabs.newTab().setText("IMAGE " + mImageFiles.size()));
        mPager.setCurrentItem(mPagerAdapter.getCount()-1);
    }

}
