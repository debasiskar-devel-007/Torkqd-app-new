package com.torkqd;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    public  GifView web;
    public  Button but;

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
        final  ViewHolder h =null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.picon = (ImageView) row.findViewById(R.id.picon);
            holder.web = (GifView) row.findViewById(R.id.webv);
            holder.loadericon = (ImageView) row.findViewById(R.id.loadericon);
            holder.but = (Button) row.findViewById(R.id.buttons);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        //ImageItem item = (ImageItem) data.get(position);
        final String fileName = fLst.get(position);
        holder.imageTitle.setText(fileName);
        //holder.but.setText("kjk");
        final ImageView t =holder.image;
        holder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "/ clicked?=" + fileName,
                        Toast.LENGTH_LONG).show();
                //h=(ViewHolder) row.getTag();
                //final TextView t=holder.imageTitle;
                 t.performClick();
                //upload up=new upload();
                //up.startuploader1(fileName);

            }
        });
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
           // new loadvideothumb().execute();

        } else {
            holder.picon.setVisibility(View.INVISIBLE);

            //bitmap = BitmapFactory.decodeFile(fileName);
           // loadImage(fileName, holder.image);

            url = fileName;
            image = holder.image;
            web = holder.web;
            loadericon = holder.loadericon;
            imageLoader.DisplayImage(fileName, image,2);


            /*Toast.makeText(context, "/ html=" + html,
                    Toast.LENGTH_LONG).show();*/

             ///new loadimagethumb().execute();


            // holder.image.setImageURI(Uri.parse(fileName));

            String html = new String();
            final String URI_PREFIX = "file://";
            html = ("<html><body><img height=150px width=150px src=\""+URI_PREFIX+url+ "\" align=left></body></html>");

            web.loadDataWithBaseURL(URI_PREFIX,
                    html,
                    "text/html",
                    "utf-8",
                    "");

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
    public void onTouch(View v, MotionEvent event) {
        Toast.makeText(context, "/ touched?=" + v.getId(),
                Toast.LENGTH_LONG).show();

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
        GifView web;
        Button but;
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


           /* BitmapFactory.Options options = new BitmapFactory.Options();
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
            }*/

            String html = new String();
            final String URI_PREFIX = "file://";
            html = ("<html><body><img height=150px width=150px src=\""+URI_PREFIX+url+ "\" align=left></body></html>");

            web.loadDataWithBaseURL(URI_PREFIX,
                    html,
                    "text/html",
                    "utf-8",
                    "");


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
