package com.torkqd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Debasis-01 on 8/11/2015.
 */
public class videolist extends Activity
{
    private Cursor videoCursor;
    private Cursor imageCursor;
    private int videoColumnIndex;
    private int imageColumnIndex;
    ListView videolist;
    ListView imagelist;
    int count;
    int counti;
    String thumbPath;
    private ProgressDialog dialog;
    private String deviceId;
    String fileuri=null;

    String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,MediaStore.Video.Thumbnails.VIDEO_ID };
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.vlist);
        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        initialization();
    }

    private void initialization()
    {
        System.gc();
        String[] videoProjection = { MediaStore.Video.Media._ID,MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,MediaStore.Video.Media.SIZE };
        String[] imageProjection = { MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.SIZE };


        videoCursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
        imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
        count = videoCursor.getCount();
        counti = imageCursor.getCount();
        videolist = (ListView) findViewById(R.id.PhoneVideoList);
        imagelist = (ListView) findViewById(R.id.imagelist);

        videolist.setAdapter(new VideoListAdapter(this.getApplicationContext()));
        videolist.setOnItemClickListener(videogridlistener);



    }

    private AdapterView.OnItemClickListener videogridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,long id)
        {
            System.gc();
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            String filename = videoCursor.getString(videoColumnIndex);
            Log.i("FileName: ", filename);
            fileuri=filename;
            dialog = ProgressDialog.show(videolist.this,
                    "Uploading", "Please wait...", true);


            new VideoUploadTask().execute();

            /*Toast.makeText(getApplicationContext(), "image clicked " + filename,
                    Toast.LENGTH_LONG).show();*/
//Intent intent = new Intent(VideoActivity.this, ViewVideo.class);
//intent.putExtra("videofilename", filename);
//startActivity(intent);
        }};
private AdapterView.OnItemClickListener imagegridlistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position,long id)
        {
            System.gc();
            imageColumnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            imageCursor.moveToPosition(position);
            String filename = imageCursor.getString(imageColumnIndex);
            Log.i("FileName: ", filename);
            fileuri=filename;
            dialog = ProgressDialog.show(videolist.this,
                    "Uploading", "Please wait...", true);


           // new VideoUploadTask().execute();

            Toast.makeText(getApplicationContext(), "image clicked " + filename,
                    Toast.LENGTH_LONG).show();
//Intent intent = new Intent(VideoActivity.this, ViewVideo.class);
//intent.putExtra("videofilename", filename);
//startActivity(intent);
        }};

    public class VideoListAdapter extends BaseAdapter
    {
        private Context vContext;
        int layoutResourceId;

        public VideoListAdapter(Context c)
        {
            vContext = c;
        }

        public int getCount()
        {
            return videoCursor.getCount();
        }

        public Object getItem(int position)
        {
            return position;
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            View listItemRow = null;
            listItemRow = LayoutInflater.from(vContext).inflate(R.layout.listitem, parent, false);

            TextView txtTitle = (TextView)listItemRow.findViewById(R.id.txtTitle);
            TextView txtSize = (TextView)listItemRow.findViewById(R.id.txtSize);
            ImageView thumbImage = (ImageView)listItemRow.findViewById(R.id.imgIcon);

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(position);
            txtTitle.setText(videoCursor.getString(videoColumnIndex));

            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            videoCursor.moveToPosition(position);
            txtSize.setText(" Size(KB):" + videoCursor.getString(videoColumnIndex));

            int videoId = videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Cursor videoThumbnailCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID+ "=" + videoId, null, null);

            if (videoThumbnailCursor.moveToFirst())
            {
                thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                Log.i("ThumbPath: ",thumbPath);

            }
            thumbImage.setImageURI(Uri.parse(thumbPath));

            return listItemRow;

        }

    }


    public void openvideo(View view) {
        Intent intent = new Intent(this, videoActivity.class);

        startActivity(intent);
    }



    class VideoUploadTask extends AsyncTask<Void, Void, String> {
        @SuppressWarnings("unused")
        @Override
        protected String doInBackground(Void... unsued) {

            long totalSize = 0;

            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://torqkd.com/user/ajs/uploadvideo");

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
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                entity.addPart("name",
                        new StringBody(deviceId));
                entity.addPart("email", new StringBody("abc@gmail.com"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);



                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {

                    Context context = videolist.this;
                    Intent cameraintent = new Intent(context, MainActivity.class);
                    context.startActivity(cameraintent);
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }



                // Launch default browser


            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

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

}