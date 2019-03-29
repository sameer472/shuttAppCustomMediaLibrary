package com.example.customlibrary;

import android.Manifest;

import com.example.customlibrary.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.customlibrary.adapter.ChatAdapter;
import com.example.customlibrary.adapter.MediaResultsAdapter;
import com.example.customlibrary.callbacks.ImagePickerCallback;
import com.example.customlibrary.callbacks.VideoPickerCallback;
import com.example.customlibrary.entity.ChosenImage;
import com.example.customlibrary.entity.ChosenVideo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImagePickerCallback,VideoPickerCallback {


    private static ListView lvResults;
    private ChatAdapter chatAdapter;
    private LinearLayoutManager linearLayoutManager;
    private  RecyclerView chat_recyclerView;

    private List<ChosenImage> chosenImageList=new ArrayList<>();
    private ImageButton btPickImageSingle;
    private Button btPickImageMultiple;
    private Button btTakePicture;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    private int pickerType;
    private String videoPickerPath;
    private int videoPickerType;

    public MainActivity() {

    }

    private String pickerPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("IMAGES"));

        setContentView(R.layout.activity_main);


        requestPermission();


        lvResults = (ListView) findViewById(R.id.lvResults);
        btPickImageSingle = (ImageButton) findViewById(R.id.pickerIcon);
        btPickImageSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //pickImageSingle();

                onButtonShowPopupWindowClick(v);
//
            }
        });

         chatAdapter =new ChatAdapter(getApplicationContext(),chosenImageList);
         linearLayoutManager = new LinearLayoutManager(getApplicationContext(), OrientationHelper.VERTICAL, false);
         chat_recyclerView = (RecyclerView) findViewById(R.id.recycler);
        chat_recyclerView.setLayoutManager(linearLayoutManager);
       // chat_recyclerView.setItemAnimator(new DefaultItemAnimator());
        chat_recyclerView.setAdapter(chatAdapter);

//
//
//        btTakePicture = (Button) findViewById(R.id.btCameraImage);
//        btTakePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                takePicture();
////                if (ContextCompat.checkSelfPermission(MainActivity.this,
////                        Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////
////                    ActivityCompat.requestPermissions(MainActivity.this,
////                            new String[]{Manifest.permission.CAMERA},
////                            MY_PERMISSIONS_REQUEST_CAMARA);
////                }
////                else {
////                    takePicture();
////                }
//
//            }
//        });

        StrictMode.setThreadPolicy (new StrictMode.ThreadPolicy.Builder ()
                .detectDiskReads ()
                .detectDiskWrites ()
                .detectNetwork ()
                .detectAll ()// or .detectAll() for all detectable problems
                .penaltyLog ()
                .build ());
        StrictMode.setVmPolicy (new StrictMode.VmPolicy.Builder ()
                .detectLeakedSqlLiteObjects ()
                .detectLeakedClosableObjects ()
                .penaltyLog ()
                .penaltyDeath ()
                .build ());

    }


    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        ImageView gallaryIcon=popupView.findViewById(R.id.gallaryIcon);
        ImageView camaraIcon=popupView.findViewById(R.id.camaraIcon);
        ImageView videoIcon=popupView.findViewById(R.id.vidoeIcon);
        ImageView audioIcon=popupView.findViewById(R.id.audioIcon);

        gallaryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageSingle();
                popupWindow.dismiss();
            }

        });

        camaraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
                popupWindow.dismiss();
            }

        });

        videoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVideos();
                popupWindow.dismiss();
            }

        });
        audioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAudios();
                popupWindow.dismiss();
            }

        });

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }


    private void pickAudios() {

    }



    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CAMARA);
        }

    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           // String receivedHexColor = intent.getStringExtra("BG_SELECT");



            List<ChosenImage> chosenImages=intent.getParcelableArrayListExtra("images");
            chosenImageList.addAll(chosenImages);
            //chatAdapter.notifyItemInserted(chosenImageList.size());
            chatAdapter.notifyDataSetChanged();

//            Toast.makeText(getApplicationContext(),chosenImages.size()+"",Toast.LENGTH_LONG).show();
//            for (ChosenImage image : chosenImages) {
//
//            }



        }
    };

    private ImagePicker imagePicker;

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.setDebugglable(true);
        imagePicker.setFolderName("Random");
        imagePicker.setRequestId(1234);
        imagePicker.ensureMaxSize(500, 500);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        Bundle bundle = new Bundle();
        bundle.putInt("android.intent.extras.CAMERA_FACING", 1);
        imagePicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_PUBLIC_DIR);
        imagePicker.pickImage();
    }

    private CameraImagePicker cameraPicker;

    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.setDebugglable(true);
        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        cameraPicker.setImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        pickerPath = cameraPicker.pickImage();
    }


    private VideoPicker videoPicker;
    private void pickVideos() {

        videoPicker = prepareVideoPicker();
        videoPicker.allowMultiple();
        videoPicker.pickVideo();
        videoPickerType = Picker.PICK_VIDEO_DEVICE;
    }

    private VideoPicker prepareVideoPicker() {
        VideoPicker videoPicker = new VideoPicker(this);
        videoPicker.setVideoPickerCallback(this);
        return videoPicker;
    }


    @Override
    public void onImagesChosen(List<ChosenImage> images) {


//
        Toast.makeText(getApplicationContext(),"calling me hahhahaa",Toast.LENGTH_LONG).show();
//        MediaResultsAdapter adapter = new MediaResultsAdapter(images, this);
//        lvResults.setAdapter(adapter);
        //Log.i(TAG, "onClick: Tapped: " + image.getOriginalPath());
//        Intent intent = new Intent(this, ImagePreviewActivity.class);
//        intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) images);
//        startActivity(intent);
    }

//    public void bindImageToRecyclerView(List<ChosenImage> images){
//        MediaResultsAdapter adapter = new MediaResultsAdapter(images, this);
//        lvResults.setAdapter(adapter);
//    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        outState.putInt("video_picker_type", videoPickerType);
        outState.putString("video_picker_path", videoPickerPath);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }else if(savedInstanceState.containsKey("video_picker_type")){
                videoPickerType = savedInstanceState.getInt("video_picker_type");
                videoPickerPath = savedInstanceState.getString("video_picker_path");
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                }
                imagePicker.submit(data);
               // handleGalleryData(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                cameraPicker.submit(data);
            } else if (requestCode == Picker.PICK_VIDEO_DEVICE) {
                VideoPickerImpl videoPickerImpl = null;
                if (videoPicker == null) {
                    videoPicker = prepareVideoPicker();
                }
                videoPickerImpl = videoPicker;
                videoPickerImpl.submit(data);
            }
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

//        if(requestCode==RequestPermissionCode){
//            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//                FireCamaraForImageMediaPickerLibrary();
//            }
//        }
        if(requestCode==MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //FireGallaryMediaPickerLibrary();
               // pickImageSingle();
            }
        }
//

            if(requestCode==MY_PERMISSIONS_REQUEST_CAMARA){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               // takePicture();
            }
        }

    }


    @Override
    public void onVideosChosen(List<ChosenVideo> videos) {
        Toast.makeText(getApplicationContext(),videos.size(),Toast.LENGTH_LONG).show();
    }
}
