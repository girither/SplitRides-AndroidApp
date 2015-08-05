package com.example.foodiepipe.foodiepipe.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;

/**
 * This class has been used for loading image from URL into ImageView.
 * Created by apurwar on 8/5/15.
 */
public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    private String imageURLStr;
    private ImageView profileImgView;

    public ImageLoadTask(String imageUrlStr, ImageView profileImage) {
        this.imageURLStr = imageUrlStr;
        this.profileImgView = profileImage;
    }

    @Override
    protected Bitmap doInBackground(Void... param) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        URL imageURL = null;
        Bitmap imageBitmap = null;

        try {
            imageURL = new URL(imageURLStr);
            imageBitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream(), null, bmOptions);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error occured while loading image", "Resource error occured");
        }

        return imageBitmap;
    }

    @Override
    protected void onPostExecute(final Bitmap imageAsBitMap) {
        this.profileImgView.setImageBitmap(imageAsBitMap);
    }
}
