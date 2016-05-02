package com.torkqd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

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
    String fileuri = null;
    private ProgressDialog dialog;
    private String deviceId;
    private Bitmap bitmap;

    public GridViewAdapter(Context context, int layoutResourceId, List<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.fLst = data;
        imageLoader=new ImageLoader(context.getApplicationContext());
        deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
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
        //web.setBackgroundColor(Color.parseColor("#919191"));



        holder.web.setOnTouchListener(new View.OnTouchListener() {

            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               /* Toast.makeText(context, "/ clicked?=" + fileName,
                        Toast.LENGTH_LONG).show();*/
                long TheImpactPoint=0;

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) {
                            fingerState = FINGER_TOUCHED;

                            TheImpactPoint= (long) motionEvent.getX();
                            /*Toast.makeText(context, "/ touched then relaesed?=" + fileName,
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_UP:
                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;


                            if (fileName.contains(".mp4") || fileName.contains(".MP4")) {

                                fileuri = fileName;
                                new VideoUploadTask().execute();

                                SystemClock.sleep(1000);


                                new VideoUploadTaskupdatelocation().execute();
                                new VideoUploadTaskfull().execute();


                                Context context = getContext();

                                Intent cameraintent = new Intent(context, MainActivity.class);
                                cameraintent.putExtra("vlocalfileuril", fileuri);

                                // Launch default browser

                                context.startActivity(cameraintent);


                            } else {

                                decodeFile(fileName);
                                SystemClock.sleep(1000);
                                new ImageUploadTask().execute();
                                Context contextc = getContext();
                                Intent cameraintent = new Intent(contextc, MainActivity.class);


                                // Launch default browser
                                contextc.startActivity(cameraintent);

                            }

                        }


                            // Your onClick codes

                        //}
                        else if (fingerState == FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;
                           /* Toast.makeText(context, "/ dragged?=" + fileName,
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) {
                            fingerState = FINGER_DRAGGING;
                            /*Toast.makeText(context, "/ move n drag?=" + fileName,
                                    Toast.LENGTH_LONG).show();*/
                        }
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;

                }

                return false;
            }
        });

        holder.but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Toast.makeText(context, "/ clicked?=" + fileName,
                        Toast.LENGTH_LONG).show();*/


               /* dialog = ProgressDialog.show(context,
                        "Uploading", "Please wait...", true);*/
                if (fileName.contains(".mp4") || fileName.contains(".MP4")) {

                    fileuri = fileName;
                    new VideoUploadTask().execute();

                    SystemClock.sleep(1000);


                    new VideoUploadTaskupdatelocation().execute();
                    new VideoUploadTaskfull().execute();



                    Context context = getContext();

                    Intent cameraintent = new Intent(context, MainActivity.class);
                    cameraintent.putExtra("vlocalfileuril", fileuri);

                    // Launch default browser

                    context.startActivity(cameraintent);




                } else {

                    decodeFile(fileName);
                    SystemClock.sleep(1000);
                    new ImageUploadTask().execute();
                    Context contextc = getContext();
                    Intent cameraintent = new Intent(contextc, MainActivity.class);


                    // Launch default browser
                    contextc.startActivity(cameraintent);

                }


                /*Context c = v.getContext();

                Intent cameraintent = new Intent(c, uploadfinal.class);
                cameraintent.putExtra("fileurl", fileName);
                v.getContext().startActivity(cameraintent);*/
                //c.startActivity(cameraintent);
                //v.performClick();
                //h=(ViewHolder) row.getTag();
                //final TextView t=holder.imageTitle;
                 //t.performClick();
               // upload up=new upload();
               // up.startuploader1(context.getApplicationContext(),fileName);

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
            //loadericon.setVisibility(View.VISIBLE);
            url = fileName;



            File folder = new File(Environment.getExternalStorageDirectory() + "/tempvideothumbnails");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            String[] myFiles;


            myFiles = folder.list();
            for (int i=0; i<myFiles.length; i++) {
                File myFile = new File(folder, myFiles[i]);
                myFile.delete();
            }

            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            Random r = new Random();
            Random r1 = new Random();
            int i1 = r.nextInt(80 - 65) + 65;
            int i2 = r1.nextInt(340 - 65) + 65;
            long unixTime = System.currentTimeMillis();
            File file = new File(folder, "img_"+i1+i2+unixTime+".jpg"); // the File to save to
            try {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap pictureBitmap = ThumbnailUtils.createVideoThumbnail(fileName,
                    MediaStore.Video.Thumbnails.MICRO_KIND); // obtaining the Bitmap
            pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }



             /*Toast.makeText(context, "/ vfile=" + fileName,
                    Toast.LENGTH_LONG).show();*/
            try {
                imageLoader.DisplayImage(file.getAbsolutePath(), image,2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // new loadvideothumb().execute();



           /* holder.image.setImageURI(Uri.parse(fileName));

            Resources res = context.getResources();
            holder.image.setImageDrawable(res.getDrawable(R.drawable.stub));*/

           /* String html = new String();
            final String URI_PREFIX = "file://";
            html = ("<html><body><img height=150px width=150px src=\""+URI_PREFIX+folder+file+ "\" align=left></body></html>");*/

            /*web.loadDataWithBaseURL(URI_PREFIX,
                    html,
                    "text/html",
                    "utf-8",
                    "");*/





            /*Bitmap bitmapv = ThumbnailUtils.createVideoThumbnail(fileName,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);;
            String html="<html><body><img src='{IMAGE_PLACEHOLDER}' /></body></html>";

// Convert bitmap to Base64 encoded image for web
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapv.compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String image = "data:image/png;base64," + imgageBase64;

// Use image for the img src parameter in your html and load to webview
            html = html.replace("{IMAGE_PLACEHOLDER}", image);
            web.loadDataWithBaseURL("file://", html, "text/html", "utf-8", "");*/

        } else {
            holder.picon.setVisibility(View.INVISIBLE);

            //bitmap = BitmapFactory.decodeFile(fileName);
           // loadImage(fileName, holder.image);

            url = fileName;
            image = holder.image;
            web = holder.web;
            loadericon = holder.loadericon;
            try {
                imageLoader.DisplayImage(fileName, image,2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            /*Toast.makeText(context, "/ html=" + html,
                    Toast.LENGTH_LONG).show();*/

             ///new loadimagethumb().execute();


            // holder.image.setImageURI(Uri.parse(fileName));

           // holder.image.setImageURI(Uri.parse(fileName));

            //Resources res = context.getResources();
            //holder.image.setImageDrawable(res.getDrawable(R.drawable.stub));

            /*String html = new String();
            final String URI_PREFIX = "file://";
            html = ("<html><body><img height=150px width=150px src=\""+URI_PREFIX+url+ "\" align=left></body></html>");*/

            /*web.loadDataWithBaseURL(URI_PREFIX,
                    html,
                    "text/html",
                    "utf-8",
                    "");*/

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
       /* Toast.makeText(context, "/ touched?=" + v.getId(),
                Toast.LENGTH_LONG).show();*/

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




    class VideoUploadTaskfull extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                // publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });


                File sourceFile = new File(fileuri);


                // Adding file data to http body
                entity.addPart("videofile", new FileBody(sourceFile));
                String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

                try {
                    entity.addPart("basepath", new StringBody(base));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    entity.addPart("localfilepath", new StringBody(fileuri));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    entity.addPart("name", new StringBody(deviceId));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //FileBody bin = new FileBody(new File("C:/ABC.txt"));


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new
                            // Here you need to put your server file address
                            HttpPost("http://torqkd.com/user/ajs/uploadvideoupdate");
                    // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    //HttpEntity entity = response.getEntity();
                    //is = entity.getContent();

                    //Context context = videoActivity.this;
                    //Intent cameraintent = new Intent(context, MainActivity.class);

                    // Launch default browser
                    // context.startActivity(cameraintent);
                    Log.v("log_tag", "In the try Loop");
                } catch (Exception e) {
                    Log.v("log_tag", "Error in http connection " + e.toString());
                }


            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }

            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
               /* if (dialog.isShowing())
                    dialog.dismiss();*/
            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    class VideoUploadTaskupdatelocation extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {

            SystemClock.sleep(2000);

            try {
                AndroidMultiPartEntity entityl = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                // publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

/*
                File sourceFile = new File(fileuri);


                // Adding file data to http body
                entity.addPart("videofile", new FileBody(sourceFile));*/
                String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

                try {
                    entityl.addPart("basepath", new StringBody(base));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    entityl.addPart("localfilepath", new StringBody(fileuri));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    entityl.addPart("name", new StringBody(deviceId));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //FileBody bin = new FileBody(new File("C:/ABC.txt"));


                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new
                            // Here you need to put your server file address
                            HttpPost("http://torqkd.com/user/ajs/uploadvideoupdatelocation");
                    // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    httppost.setEntity(entityl);
                    HttpResponse response = httpclient.execute(httppost);
                    //HttpEntity entity = response.getEntity();
                    //is = entity.getContent();

                    //Context context = videoActivity.this;
                    //Intent cameraintent = new Intent(context, MainActivity.class);

                    // Launch default browser
                    // context.startActivity(cameraintent);
                    Log.v("log_tag", "In the try Loop");
                } catch (Exception e) {
                    Log.v("log_tag", "Error in http connection " + e.toString());
                }


            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }

            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
                /*if (dialog.isShowing())
                    dialog.dismiss();*/
            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    class VideoUploadTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {

            com.torkqd.MultipartEntity reqEntity = new com.torkqd.MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(fileuri,
                    MediaStore.Images.Thumbnails.MINI_KIND);


            InputStream is;
            BitmapFactory.Options bfo;
            Bitmap bitmapOrg;
            ByteArrayOutputStream bao;

            bfo = new BitmapFactory.Options();
            bfo.inSampleSize = 2;
            /*bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
             + "/" + customImage, bfo);*/

            bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();

            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            is = new ByteArrayInputStream(bao.toByteArray());




            /*reqEntity.addPart("myFile",
                    deviceId+ ".jpg", is);*/


            //File sourceFile = new File(fileuri);

            reqEntity.addPart("myFile",
                    deviceId + ".jpg", is);

            // Adding file data to http body
            // reqEntity.addPart("thumb", new FileBody(sourceFile));
            String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

            try {
                reqEntity.addPart("basepath", new StringBody(base));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                reqEntity.addPart("localfilepath", new StringBody(fileuri));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                reqEntity.addPart("name", new StringBody(deviceId));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //FileBody bin = new FileBody(new File("C:/ABC.txt"));


            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new
                        // Here you need to put your server file address
                        HttpPost("http://torqkd.com/user/ajs/uploadvideo");
                // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                //HttpEntity entity = response.getEntity();
                //is = entity.getContent();



                Log.v("log_tag", "In the try Loop");
            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }

            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
               /* if (dialog.isShowing())
                    dialog.dismiss();*/
            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    class ImageUploadTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {
            InputStream is;
            BitmapFactory.Options bfo;
            Bitmap bitmapOrg;
            ByteArrayOutputStream bao;

            bfo = new BitmapFactory.Options();
            bfo.inSampleSize = 2;
            /*bitmapOrg = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
             + "/" + customImage, bfo);*/

            bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
            byte[] ba = bao.toByteArray();

            String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
            is=new ByteArrayInputStream(bao.toByteArray());
            ArrayList<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("image", ba1));
            nameValuePairs.add(new BasicNameValuePair("cmd", "image_android"));
            Log.v("log_tag", System.currentTimeMillis() + ".jpg");

            /*Toast.makeText(getApplicationContext(), "Unknown path",
                    Toast.LENGTH_LONG).show();*/
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            reqEntity.addPart("myFile",
                    deviceId+ ".jpg", is);

            //FileBody bin = new FileBody(new File("C:/ABC.txt"));





            String uploadurl="http://torqkd.com/user/ajs/AddTempTable";
           /* if(uploadtype.matches("group")){
                uploadurl="http://torqkd.com/user/ajs/groupimage";
            }
            if(uploadtype.matches("editp")){
                uploadurl="http://torqkd.com/user/ajs/profileimage";
            }
            if(uploadtype.matches("editpb")){
                uploadurl="http://torqkd.com/user/ajs/profileimagebg";
            }*/


            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new
                        // Here you need to put your server file address
                        HttpPost(uploadurl);
                // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();


                Log.v("log_tag", "In the try Loop");
            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";
            // (null);
        }

        @Override
        protected void onProgressUpdate(Void... unused) {

        }

        @Override
        protected void onPostExecute(String sResponse) {
            try {
               /* if (dialog.isShowing())
                    dialog.dismiss();*/
            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

        public class GifView extends WebView {

            /**
             * @param context
             * @param attrs
             */
            public GifView(Context context, AttributeSet attrs) {
                super(context, attrs);
                setClickable(false);
                setFocusable(false);
                setFocusableInTouchMode(false);
                setLongClickable(false);

            }

            // actions

        }

    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
        /*Toast.makeText(getApplicationContext(), filePath,
                Toast.LENGTH_LONG).show();*/




        // imgView.setImageBitmap(bitmap);

    }



}
