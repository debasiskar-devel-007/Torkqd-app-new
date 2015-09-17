package com.torkqd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KTA-PC 21 on 7/15/2015.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();
    private List<String> fLst;
    private String url;
    private ImageView image;
    private ImageView loadericon;
    public ImageLoader imageLoader;

    public GridViewAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.fLst = data;
        imageLoader=new ImageLoader(context.getApplicationContext());
        //this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.picon = (ImageView) row.findViewById(R.id.picon);
            holder.loadericon = (ImageView) row.findViewById(R.id.loadericon);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //ImageItem item = (ImageItem) data.get(position);
        String fileName = fLst.get(position);
        holder.imageTitle.setText(fileName);
        Bitmap bitmap;
        // holder.image.setImageBitmap(item.getImage());
        if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
            holder.picon.setVisibility(View.VISIBLE);
            /*bitmap = ThumbnailUtils.createVideoThumbnail(fileName,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            holder.image.setImageBitmap(bitmap);*/
           // vloadImage(fileName, holder.image);

            //loadImage(fileName, holder.image);
            image = holder.image;
            loadericon = holder.loadericon;
            url = fileName;
            //imageLoader.DisplayImage(fileName, image,1);
            new loadvideothumb().execute();

        } else {
            holder.picon.setVisibility(View.INVISIBLE);

            //bitmap = BitmapFactory.decodeFile(fileName);
           // loadImage(fileName, holder.image);
            url = fileName;
            image = holder.image;
            loadericon = holder.loadericon;
            imageLoader.DisplayImage(fileName, image,2);

            // new loadimagethumb().execute();


            // holder.image.setImageURI(Uri.parse(fileName));

        }
        return row;

    }

    public void loadImage(final String url, final ImageView image) {
        //super(url, image);
        // load an image (maybe do this using an AsyncTask
        // if you're loading from network
        Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(url), 100, 100);
        //image.setImageURI(Uri.parse(url));
        image.setImageBitmap(ThumbImage);


    }

    public void vloadImage(final String url, final ImageView image) {
        //super(url, image);
        // load an image (maybe do this using an AsyncTask
        // if you're loading from network
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(url,
                MediaStore.Video.Thumbnails.MICRO_KIND);
        image.setImageBitmap(bitmap);


    }



    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
        ImageView picon;
        ImageView loadericon;
    }


    class loadimagethumb extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {


            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {


            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            loadericon.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            if (imageHeight > 400 && imageWidth > 400) {
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(url), 150, 150);
                //image.setImageURI(Uri.parse(url));
                image.setImageBitmap(ThumbImage);

            } else {
                image.setVisibility(View.GONE);
            }


        }

    }

    class loadvideothumb extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {


            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            Bitmap bitmap;
            bitmap = ThumbnailUtils.createVideoThumbnail(url,
                    MediaStore.Video.Thumbnails.MINI_KIND);
            //loadericon.setVisibility(View.GONE);
            //image.setVisibility(View.VISIBLE);
            image.setImageBitmap(bitmap);


        }

    }



}
