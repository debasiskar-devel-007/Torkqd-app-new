package com.torkqd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {

    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int GPS_RESULTCODE = 0;
    LocationManager locationManager;
    ProgressDialog progressDialog;
    double latitude;
    double longitude;
    private WebView myWebView;
    private LocationManager mLocMgr;
    private ValueCallback<Uri> mUploadMessage;
    private Button btn1;
    private String deviceId;
    private String vlocalfileuril;
    private String routeflag;

    private ListView mListView;
    private List<String> fileNameList;
    private List<Long> filecreatdtimelist;
    //private MainActivity.FlAdapter mAdapter;
    private File file;
    private File tfile;
    int dircount;
    List<String> flLst = new ArrayList<String>();
    List<String> fnamelLst = new ArrayList<String>();
    String state = Environment.getExternalStorageState();
    List<String> ls;
    private ImageView loaderImg;
    private ProgressBar pLoader;


    private final static Object methodInvoke(Object obj, String method,
                                             Class<?>[] parameterTypes, Object[] args) {
        try {
            Method m = obj.getClass().getMethod(method,
                    new Class[]{boolean.class});
            m.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
        file = Environment.getExternalStorageDirectory();
        ls=getFilePaths();
        if (progressDialog == null) {
            // in standard case YourActivity.this
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(true);
           // progressDialog.show();
        }
		// turnGPSOn();
		// turnGPSOn();

		myWebView = (WebView) findViewById(R.id.webView1);
		loaderImg = (ImageView) findViewById(R.id.loader);
        pLoader = (ProgressBar) findViewById(R.id.pbHeaderProgress);

        pLoader.getIndeterminateDrawable().setColorFilter(0xFFFF971B, android.graphics.PorterDuff.Mode.MULTIPLY);

        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");



        vlocalfileuril = null;
        Intent intent = getIntent();
        vlocalfileuril = intent.getStringExtra("vlocalfileuril");
        routeflag = intent.getStringExtra("routeflag");
        vlocalfileuril = vlocalfileuril.valueOf(vlocalfileuril);
        routeflag = routeflag.valueOf(routeflag);
        /*Toast.makeText(getApplicationContext(), " status " + vlocalfileuril,
                Toast.LENGTH_LONG).show();*/


        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
       // Toast.makeText(this, deviceId, Toast.LENGTH_SHORT).show();

        //myWebView.loadUrl("javascript:add_userdevice_with_session(" + deviceId + ")");




       /* String postReceiverUrl = "http://torqkd.com/user/ajs/add_userdevice_with_session";
        //Log.v(TAG, "postURL: " + postReceiverUrl);

// HttpClient
        HttpClient httpClient = new DefaultHttpClient();

// post header
        HttpPost httpPost = new HttpPost(postReceiverUrl);

// add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("deviceid", deviceId));
        nameValuePairs.add(new BasicNameValuePair("sessionid", "Dalisay"));


        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

// execute HTTP post request
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();*/












        /*myWebView.setWebViewClient(new WebViewController());

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setGeolocationEnabled(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        Context context = myWebView.getContext();
        myWebView.getSettings().setGeolocationDatabasePath( context.getFilesDir().getPath() );
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });*/

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }else{
            location = locationManager.getLastKnownLocation
                    (LocationManager.PASSIVE_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            onLocationChanged(location);

            deviceId = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            //Toast.makeText(this, location+"location not working", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, location+"location not working", Toast.LENGTH_SHORT).show();
            myWebView.loadUrl("javascript:setValue("+latitude+")");
            myWebView.loadUrl("javascript:setValuelong(" + longitude + ")");

            myWebView.loadUrl("javascript:add_userdevice_with_session('" + deviceId + "')");
        }

       /* locationManager.requestLocationUpdates(provider,1000,0, this);
         latitude = location.getLatitude();

        // Getting longitude of the current location
         longitude = location.getLongitude();



        myWebView.loadUrl("javascript:setValue(" + latitude + ")");
        myWebView.loadUrl("javascript:setValuelong("+longitude+")");

        myWebView.loadUrl("javascript:add_userdevice_with_session('"+deviceId +"')");*/



        // launchWebview();
		if (!isGPSEnable()) {
			startActivityForResult(new Intent(
                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    GPS_RESULTCODE);
		} else {
			launchWebview();
		}
	}




    public void sendMessage(View view) {
        Intent intent = new Intent(this, cameraActivity.class);

        startActivity(intent);
    }

    public void onLocationChanged(Location location) {

        //TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        latitude = location.getLatitude();

        // Getting longitude of the current location
        longitude = location.getLongitude();

        deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // Toast.makeText(this, deviceId+"location working", Toast.LENGTH_SHORT).show();

        myWebView.loadUrl("javascript:setValue("+latitude+")");
        myWebView.loadUrl("javascript:setValuelong("+longitude+")");

        myWebView.loadUrl("javascript:add_userdevice_with_session('" + deviceId + "')");

       // myWebView.loadUrl("javascript:loadImage('"+ ls+"')");





        /*Context context = myWebView.getContext();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(latitude+"&&"+longitude);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //dialog.cancel();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();*/


        //myWebView.loadUrl("javascript:setValuelong("+ longitude +")");




        // Creating a LatLng object for the current location
        //LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Setting latitude and longitude in the TextView tv_location
        //tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );

    }

    public void onPause(){

        super.onPause();
        //locationManager.removeUpdates(this);
        //locationManager = null;
    }

    public  void onResume(){
        super.onResume();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider,1000,0, this);

    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == 0) {
			launchWebview();
		}
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
        }
    }

    private void launchWebview() {
        myWebView.setWebViewClient(new WebViewController());
        myWebView.getSettings().setJavaScriptEnabled(true);


        myWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.getSettings().setSupportMultipleWindows(true);
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setAllowFileAccess(true);
        myWebView.getSettings().setGeolocationEnabled(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        Context context = myWebView.getContext();
        myWebView.getSettings().setGeolocationDatabasePath(context.getFilesDir().getPath());
        myWebView.getSettings().setLoadWithOverviewMode(true);
        myWebView.getSettings().setUseWideViewPort(true);
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

		myWebView.loadUrl("http://torqkd.com/torqkd_demo/#/");

        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // settings.setPluginsEnabled(true);
        methodInvoke(myWebView.getSettings(), "setPluginsEnabled", new Class[] { boolean.class }, new Object[] { true });
        // settings.setPluginState(PluginState.ON);
        methodInvoke(myWebView.getSettings(), "setPluginState", new Class[] { PluginState.class }, new Object[] { PluginState.ON });
        // settings.setPluginsEnabled(true);
        methodInvoke(myWebView.getSettings(), "setPluginsEnabled", new Class[] { boolean.class }, new Object[] { true });
        // settings.setAllowUniversalAccessFromFileURLs(true);
        methodInvoke(myWebView.getSettings(), "setAllowUniversalAccessFromFileURLs", new Class[] { boolean.class }, new Object[] { true });
        // settings.setAllowFileAccessFromFileURLs(true);
        methodInvoke(myWebView.getSettings(), "setAllowFileAccessFromFileURLs", new Class[]{boolean.class}, new Object[]{true});

        myWebView.getSettings().setJavaScriptEnabled(true);
        //getWindow().requestFeature(Window.FEATURE_PROGRESS);



        /*final String URI_PREFIX = "file://";

        String html = new String();
        html="";
       // html = ("<html><body><header><h1>Title</h1></header><section>");

        for(int i=0;i<2;i++) {
            html.concat("<img  src=\"" + URI_PREFIX + ls.get(i) + "\" align=left>");
        }*/
       // html.concat("</section></body></html>");

/*
        myWebView.loadDataWithBaseURL(URI_PREFIX,
                html,
                "text/html",
                "utf-8",
                "");*/



        /*String html = new String();*/
       /* html = ("<html><body><header><h1>Title</h1></header><section><img src=\""+URI_PREFIX+ls.get(1)+ "\" align=left><p>Content</p></section></body></html>");

        myWebView.loadDataWithBaseURL(URI_PREFIX,
                html,
                "text/html",
                "utf-8",
                "");*/




        final Activity activity = this;


		myWebView.setWebChromeClient(new WebChromeClient()
        {





            public void onProgressChanged(WebView view, int progress) {
                // Activities and WebViews measure progress with different scales.
                // The progress meter will automatically disappear when we reach 100%
                //activity.setProgress(progress * 1000);
                progressDialog.setProgress(progress);
                if (progress > 90) {

                    loaderImg.setVisibility(view.GONE);
                    pLoader.setVisibility(view.GONE);
                    myWebView.setVisibility(view.VISIBLE);
                    myWebView.loadUrl("javascript:setValue(" + latitude + ")");
                    myWebView.loadUrl("javascript:setValuelong(" + longitude + ")");

                    myWebView.loadUrl("javascript:add_userdevice_with_session('" + deviceId + "')");

                    try {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                   //
                }
                if(progress < 90 && myWebView.getVisibility() == view.GONE){

                    myWebView.setVisibility(view.GONE);
                    loaderImg.setVisibility(view.VISIBLE);
                    pLoader.setVisibility(view.VISIBLE);

                    myWebView.loadUrl("javascript:setValue("+latitude+")");
                    myWebView.loadUrl("javascript:setValuelong("+longitude+")");

                    myWebView.loadUrl("javascript:add_userdevice_with_session('" + deviceId + "')");


                    if (progressDialog == null) {
                        // in standard case YourActivity.this
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                    }
                }

            }




            // For Android 3.0+
            // For Android > 4.1
            //The undocumented magic method override
            //Eclipse will swear at you if you try to put @Override here
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
               startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

            }

            // For Android 3.0+
            public void openFileChooser( ValueCallback uploadMsg, String acceptType ) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FILECHOOSER_RESULTCODE );

            }

        });
	}

	private boolean isGPSEnable() {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean enabled = service
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// boolean enabled = false;

		if (!enabled) {
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);
			enabled = true;
		}
		return enabled;

	}

	public void turnGPSOn() {
		Log.e("torkqd", "Going to enable GPS..");
		Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
		intent.putExtra("enabled", true);
		sendBroadcast(intent);

		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) { // if gps is disabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);

		}
	}

	// automatic turn off the gps
	public void turnGPSOff() {
		String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (provider.contains("gps")) { // if gps is enabled
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// turnGPSOff();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Check if the key event was the Back button and if there's history
		if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
			myWebView.goBack();
			return true;
		}
		// If it wasn't the Back key or there's no web page history, bubble up
		// to the default
		// system behavior (probably exit the activity)
		return super.onKeyDown(keyCode, event);
	}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }





    public void onLoadResource (WebView view, String url) {
        if (progressDialog == null) {
            // in standard case YourActivity.this
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }
    }
    public void onPageFinished(WebView view, String url) {
        try{
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
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
            if (tf.getName().contains(".JPEG") ||
                    tf.getName().contains(".jpeg") ||
                    tf.getName().contains(".png") ||
                    tf.getName().contains(".PNG") ||
                    tf.getName().contains(".JPG") ||
                    tf.getName().contains(".jpg") ||
                    tf.getName().contains(".bmp") ||
                    tf.getName().contains(".BMP") ||
                    tf.getName().contains(".GIF") ||
                    tf.getName().contains(".gif") ||
                    tf.getName().contains(".mp4") ||
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
                if (f.getName().contains(".JPEG") ||
                        f.getName().contains(".jpeg") ||
                        f.getName().contains(".png") ||
                        f.getName().contains(".PNG") ||
                        f.getName().contains(".JPG") ||
                        f.getName().contains(".jpg") ||
                        f.getName().contains(".bmp") ||
                        f.getName().contains(".BMP") ||
                        f.getName().contains(".GIF") ||
                        f.getName().contains(".gif") ||
                        f.getName().contains(".mp4") ||
                        f.getName().contains(".MP4")
                        ) {

                    if (!Arrays.asList(fnamelLst).contains(f.getName())) {
                        flLst.add(f.getAbsolutePath());
                        fnamelLst.add(f.getName());

                     //
                    }
                }


            }
        }

        Collections.reverse(flLst);


        return flLst;


    }



}
