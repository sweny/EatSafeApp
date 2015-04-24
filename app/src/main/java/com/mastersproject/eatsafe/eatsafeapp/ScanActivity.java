package com.mastersproject.eatsafe.eatsafeapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mastersproject.eatsafe.OCRApiService.ApiService;
import com.mastersproject.eatsafe.connectivity.ServerConnectivity;
import com.mastersproject.eatsafe.connectivity.StringValues;
import com.mastersproject.eatsafe.imageCapture.AlbumStorageDirFactory;
import com.mastersproject.eatsafe.imageCapture.BaseAlbumDirFactory;
import com.mastersproject.eatsafe.imageCapture.FroyoAlbumDirFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ScanActivity extends Activity {

    private static final int ACTION_TAKE_PHOTO_B = 1;

    private static final String BITMAP_STORAGE_KEY = "viewbitmap";
    private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;


    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    public static String Storage_file_path;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    static File imageStoreDirectory;
    public static Context appContext;
    public static String textRecognized;

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }

    public static File createImageStore() {
        File externalStorage = Environment.getExternalStorageDirectory();
        imageStoreDirectory = new File(externalStorage + File.separator + "EatSafe images");
        Log.d("Debug:","Directory logic check");
        if (imageStoreDirectory.exists()==false && imageStoreDirectory.isDirectory()==false) {
            imageStoreDirectory.mkdir();
        }
        return imageStoreDirectory;
    }



    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            //storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());
            storageDir = ScanActivity.createImageStore();
            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("EatSafe Images", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        // String imageFileName = JPEG_FILE_PREFIX ;
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        Storage_file_path = imageF.getPath();
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

            /* There isn't enough memory to open up more than a couple camera photos */
            /* So pre-scale the target bitmap into which the file is decoded */

            /* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

            /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

            /* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

            /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

            /* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            /* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(bitmap);

        mImageView.setVisibility(View.VISIBLE);

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }


    private void handleBigCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
        }
    }

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        appContext = getApplicationContext();
        mImageView = (ImageView) findViewById(R.id.imageView1);

        mImageBitmap = null;


        Button picBtn = (Button) findViewById(R.id.btnIntend);
        setBtnListenerOrDisable(
                picBtn,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );
/* Here we show how to start a new Activity that implements the Scandit
         * SDK as a full screen scanner. The Activity can be found in the
         * SampleFullScreenBarcodeActivity in this demo project. The old and
         * new GUIs can both be easily opened this way, which is also shown in
         * the aforementioned activity. */
        Button activityButton = (Button) findViewById(R.id.btnscandit);
        activityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanActivity.this,
                        SampleFullScreenBarcodeActivity.class));
               // finish();
                //showNotification();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                    if (mCurrentPhotoPath.length() > 0){
                        ApiService apiService = new ApiService("SeP6qLUaQq");
                        Boolean result = apiService.convertToText("en",mCurrentPhotoPath);
                        textRecognized = apiService.getResponseText();

                       // textRecognized = "Starbucks";
                        Log.d("result:"," "+result+" "+textRecognized);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("textRecognized", textRecognized));
                        ServerConnectivity sr = new ServerConnectivity();
                        String jsonString;
                        JSONObject json = sr.getJSON(StringValues.PASSWORD_RESET_CODE_URL, params);

                        if (json != null) {
                            try {
                                jsonString = json.getString("response");
                                if (json.getBoolean("res")) {
                                    Log.e("JSON", jsonString);
                                }
                            } catch (JSONException e){
                                Log.d("Inside JSON Exception",e.getMessage());
                            }
                        }
                        //ScanActivity scanActivity = new ScanActivity();
                        showNotification();
                       /* String recognizedText = imageToStringConverter(mCurrentPhotoPath);
                        mCurrentPhotoPath = null;
                        Log.d("recognizedText",recognizedText);*/
                    }
                }
                break;
            } // ACTION_TAKE_PHOTO_B

        } // switch
    }

    // Some lifecycle callbacks so that the image can survive orientation change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        String flag = (mImageBitmap != null)? "true":"false";
        Log.d("flag : ",flag);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        mImageView.setImageBitmap(mImageBitmap);
        Log.d("onRestoreInstanceState", "mImageView created");
        mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );

    }

    /**
     * Indicates whether the specified action can be used as an intent. This
     * method queries the package manager for installed packages that can
     * respond to an intent with the specified action. If no suitable package is
     * found, this method returns false.
     * http://android-developers.blogspot.com/2009/01/can-i-use-this-intent.html
     *
     * @param context The application's environment.
     * @param action The Intent action to check for availability.
     *
     * @return True if an Intent with the specified action can be sent and
     *         responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            Button btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            btn.setText(
                    getText(R.string.cannot).toString() + " " + btn.getText());
            btn.setClickable(false);
        }
    }
    //Tessearact code
/*    public String imageToStringConverter(String imagePath) {
        System.out.println("imagePath : "+imagePath);

        String data_path = Environment.getExternalStorageDirectory().toString()+"/";
       // String path = baseDir.toString() + File.separator;
        if (!(new File(data_path + "tessdata/eng.traineddata")).exists()) {
            try {
                AssetManager assetManager = getAssets();
                String[] files = null;
                files = assetManager.list("");
                InputStream in = assetManager.open("tesseract/tessdata/eng.traineddata");
                OutputStream out = new FileOutputStream(data_path+"tessdata/eng.traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                Log.d("fail to copy trainedata" , e.toString());
            }
        }
        Bitmap image = BitmapFactory.decodeFile(imagePath);
       // ScanActivity scanActivity = new ScanActivity();
        //image = scanActivity.updateImage(imagePath, image);
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.init(Environment.getExternalStorageDirectory().toString()+"/", "eng");
        baseApi.setImage(image);
        String recognizedText = baseApi.getUTF8Text();
        //recognizedText=recognizedText.replaceAll("[^a-zA-Z0-9]", " ");
        baseApi.end();
        Log.d("recog", recognizedText);
        return recognizedText;
    }


    public Bitmap updateImage(String imagePath, Bitmap bitmap){
        System.out.println("Inside updateImage");
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        return bitmap;
    }*/

    public void showNotification(){
        System.out.println("inside show notifi - ");
        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(ScanActivity.this, NotificationReceiver.class);
        PendingIntent pIntent = PendingIntent.getActivity(ScanActivity.this, 0, intent, 0);
        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        System.out.println("intent - "+intent);
        System.out.println("pintent - "+pIntent);

        Notification mNotification = new Notification.Builder(this)

                .setContentTitle("Scan Result")
                .setContentText("Here's the result of your scan!")
                .setSmallIcon(R.drawable.ic_restaurant)
                .setContentIntent(pIntent)
                .setSound(soundUri)
                .addAction(R.drawable.ic_restaurant, "View", pIntent)
                .addAction(0, "Remind", pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);
    }
}
