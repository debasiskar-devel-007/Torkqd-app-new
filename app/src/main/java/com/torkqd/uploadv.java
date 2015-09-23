package com.torkqd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
//import org.apache.http.entity.mime.MultipartEntity;


public class uploadv extends Activity {
    ListView videolist;
    ListView imagelist;
    int count;
    int counti;
    String thumbPath;
    //private ProgressDialog dialog;
    //private String deviceId;
    String fileuri = null;
    ArrayList<ImageItem> resultIAV = new ArrayList<>();
    String state = Environment.getExternalStorageState();
    List<String> flLst = new ArrayList<String>();
    List<String> fnamelLst = new ArrayList<String>();
    int dircount;
    String[] thumbColumns = {Thumbnails.DATA, Thumbnails.VIDEO_ID};
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Bitmap bitmap;
    private ProgressDialog dialog;
    private String deviceId;
    private String uploadtype;
    private Cursor videoCursor;
    private Cursor imageCursor;
    private int videoColumnIndex;
    private int imageColumnIndex;
    private ListView mListView;
    private List<String> fileNameList;
    private List<Long> filecreatdtimelist;
    //private MainActivity.FlAdapter mAdapter;
    private File file;
    private File tfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.choose_uploader);


        // mListView = (ListView) findViewById(R.id.listView1);
        file = Environment.getExternalStorageDirectory();
        dircount = StringUtils.countMatches(file.getAbsolutePath(), "/");


        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Intent intent = getIntent();
        uploadtype =  intent.getStringExtra("uploadtype");

        uploadtype=uploadtype.valueOf(uploadtype);


       /* Toast.makeText(getApplicationContext(), "image clicked "+uploadtype,
         Toast.LENGTH_LONG).show();*/
        Button playButton = (Button) findViewById(R.id.imgcancelbtn);
        if(uploadtype.matches("group")){
            /*Toast.makeText(getApplicationContext(), "image clicked in group ",
                    Toast.LENGTH_LONG).show();*/
            playButton.setVisibility(View.GONE);


        }

        if(uploadtype.matches("editp")){
           /* Toast.makeText(getApplicationContext(), "image clicked in group ",
                    Toast.LENGTH_LONG).show();*/
            playButton.setVisibility(View.GONE);


        }
        if(uploadtype.matches("editpb")){
           /* Toast.makeText(getApplicationContext(), "image clicked in group ",
                    Toast.LENGTH_LONG).show();*/
            playButton.setVisibility(View.GONE);


        }


        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getFilePaths());
        ///gridView.setAdapter(gridAdapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                //ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                String prompt = (String) parent.getItemAtPosition(position);


                Toast.makeText(getApplicationContext(), "image clicked " + prompt,
                        Toast.LENGTH_LONG).show();



                dialog = ProgressDialog.show(uploadv.this,
                        "Uploading", "Please wait...", true);
                if (prompt.contains(".mp4") || prompt.contains(".MP4")) {

                    fileuri = prompt;
                    new VideoUploadTask().execute();

                    SystemClock.sleep(1000);


                    new VideoUploadTaskupdatelocation().execute();
                    new VideoUploadTaskfull().execute();

                } else {

                    decodeFile(prompt);
                    new ImageUploadTask().execute();

                }


                //Create intent
                /*Intent intent = new Intent(upload.this, cameraActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);*/
            }
        });




    }
    public static void startuploader1(Context context ,String url){


        uploadv nup=new uploadv();
        Toast.makeText(nup, "/ clicked?=" + url,
                Toast.LENGTH_LONG).show();
        //nup.startuploader(url);

    }
    public void startuploader(String url){
        String prompt=url;

        dialog = ProgressDialog.show(uploadv.this,
                "Uploading", "Please wait...", true);
        if (prompt.contains(".mp4") || prompt.contains(".MP4")) {

            fileuri = prompt;
            new VideoUploadTask().execute();

            SystemClock.sleep(1000);


            new VideoUploadTaskupdatelocation().execute();
            new VideoUploadTaskfull().execute();

        } else {

            decodeFile(prompt);
            new ImageUploadTask().execute();

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

    public ArrayList<ImageItem> getFilePathsold() {


        //Uri u = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;;
        String[] projection = {MediaStore.Images.Thumbnails.DATA};
        Cursor c = null;
        Context context;
        SortedSet<String> dirList = new TreeSet<>();
        ArrayList<ImageItem> resultIAV = new ArrayList<>();

        String[] directories = null;
        //if (u != null) {
        c = this.getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        //}

        if ((c != null) && (c.moveToFirst())) {
            do {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try {
                    dirList.add(tempDir);
                } catch (Exception e) {

                }
            }
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for (int i = 0; i < dirList.size(); i++) {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if (imageList == null)
                continue;
            for (File imagePath : imageList) {
                try {

                    if (imagePath.isDirectory()) {
                        imageList = imagePath.listFiles();

                    }
                    if (imagePath.getName().contains(".jpg") || imagePath.getName().contains(".JPG")
                            || imagePath.getName().contains(".jpeg") || imagePath.getName().contains(".JPEG")
                            || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                            || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                            || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")

                            ) {


                        String path = imagePath.getAbsolutePath();
                       /* Toast.makeText(getApplicationContext(), path,
                                Toast.LENGTH_LONG).show();*/
                        Bitmap bitmap;
                        bitmap = BitmapFactory.decodeFile(path);
                        resultIAV.add(new ImageItem(bitmap, path));

                    }

                    if (imagePath.getName().contains(".mp4") || imagePath.getName().contains(".MP4") || imagePath.getName().contains(".Mp4")) {
                        String path = imagePath.getAbsolutePath();
                        Toast.makeText(getApplicationContext(), path,
                                Toast.LENGTH_LONG).show();

                        bitmap = ThumbnailUtils.createVideoThumbnail(path,
                                Thumbnails.FULL_SCREEN_KIND);
                        ;
                        resultIAV.add(new ImageItem(bitmap, path));

                    }
                }
                //  }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultIAV;


    }

    public void populatefilelist(String fpath) {

        tfile = new File(fpath);
        File[] tfileArr = tfile.listFiles();
        int tlength = tfileArr.length;
        for (int i = 0; i < tlength; i++) {
            File tf = tfileArr[i];
            String filetype;
            if (tf.isDirectory()) {
                filetype = "directory";
                // flLst.add(tf.getName());
                int count = StringUtils.countMatches(tf.getAbsolutePath(), "/");
               /* Toast.makeText(getApplicationContext(), "/ count=" + count,
                        Toast.LENGTH_LONG).show();*/
                if (count < (dircount + 3)) populatefilelist(tf.getAbsolutePath());
            } else filetype = "file";
        /*Toast.makeText(getApplicationContext(),"filename="+ tf.getName()+"filetype="+filetype+"getabspath="+tf.getAbsolutePath(),
                Toast.LENGTH_LONG).show();*/
            if (tf.getName().contains(".mp4") ||
                    tf.getName().contains(".MP4")
                    ) {


                /*Toast.makeText(getApplicationContext(), "/ createdtime=" + tf.lastModified(),
                        Toast.LENGTH_LONG).show();*/





                        if (!Arrays.asList(fnamelLst).contains(tf.getName())) {
                            flLst.add(tf.getAbsolutePath());
                            //filecreatdtimelist.add(tf.lastModified());
                            fnamelLst.add(tf.getName());


                        }








            }
        }

    }

    public List<String> getFilePaths() {


        if (Environment.MEDIA_MOUNTED.equals(state) && file.isDirectory()) {
            File[] fileArr = file.listFiles();
            int length = fileArr.length;
            for (int i = 0; i < length; i++) {
                File f = fileArr[i];
                String filetype;
                if (f.isDirectory()) {
                    filetype = "directory";
                    populatefilelist(f.getAbsolutePath());
                    // flLst.add(f.getName());

                } else filetype = "file";
                /* Toast.makeText(getApplicationContext(),"filename="+ f.getName()+"filetype="+filetype+"getabspath="+f.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();*/
                if (f.getName().contains(".mp4") ||
                        f.getName().contains(".MP4")
                        ) {





                            if (!Arrays.asList(fnamelLst).contains(f.getName())) {
                                flLst.add(f.getAbsolutePath());
                                //filecreatdtimelist.add(tf.lastModified());
                                fnamelLst.add(f.getName());




                        }



                }


            }
        }

        Collections.reverse(flLst);


        return flLst;


    }

    public void opencamera(View view) {
        Intent intent = new Intent(this, cameraActivity.class);
        if (uploadtype.matches("group")) {
            intent.putExtra("uploadtype", "group");
        }
        if (uploadtype.matches("editp")) {
            intent.putExtra("uploadtype", "editp");
        }

        if (uploadtype.matches("editpb")) {
            intent.putExtra("uploadtype", "editpb");
        }


        startActivity(intent);
    }

    public void seeimage(View view) {

       /* Toast.makeText(getApplicationContext(), "image clicked "+view,
                Toast.LENGTH_LONG).show();*/

    }

    public void openvideo(View view) {
        Intent intent = new Intent(this, videoActivity.class);

        startActivity(intent);
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
                if (dialog.isShowing())
                    dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
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
                if (dialog.isShowing())
                    dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    class VideoUploadTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


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


                Context context = uploadv.this;

                Intent cameraintent = new Intent(context, MainActivity.class);
                cameraintent.putExtra("vlocalfileuril", fileuri);

                // Launch default browser

                context.startActivity(cameraintent);
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
                if (dialog.isShowing())
                    dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
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
            if(uploadtype.matches("group")){
                uploadurl="http://torqkd.com/user/ajs/groupimage";
            }
            if(uploadtype.matches("editp")){
                uploadurl="http://torqkd.com/user/ajs/profileimage";
            }
            if(uploadtype.matches("editpb")){
                uploadurl="http://torqkd.com/user/ajs/profileimagebg";
            }


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

                Context context = uploadv.this;
                Intent cameraintent = new Intent(context, MainActivity.class);


                // Launch default browser
                context.startActivity(cameraintent);
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
                if (dialog.isShowing())
                    dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(),
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



















}