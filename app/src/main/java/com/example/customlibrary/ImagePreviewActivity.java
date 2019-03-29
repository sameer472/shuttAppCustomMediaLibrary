package com.example.customlibrary;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.customlibrary.adapter.ImagePreviewAdapter;
import com.example.customlibrary.adapter.ImagePreviewBottomIconAdapter;
import com.example.customlibrary.callbacks.ImagePickerCallback;
import com.example.customlibrary.entity.ChosenImage;
import com.example.customlibrary.entity.ChosenVideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity implements ImagePickerCallback,
        ImagePreviewAdapter.AdapterCallback, ImagePreviewBottomIconAdapter.ImageIconInterface {

    private ImageView ivImageGlide;
    private String uri;
    private String mimeType;
    private List<ChosenImage> images;
    private List<ChosenVideo> videos;

    private ChosenImage image;
    private FloatingActionButton ok;
    private ImageView moreImage;
    private ImagePickerCallback callback;
    private LinearLayout layout,layout_icon;
    private Toolbar topToolBar;
    private ActionBar actionBar;
    private RecyclerView preview_gallry_recyclerView;
    private ImagePreviewAdapter imagePreviewAdapter;
    private int review_position=0;
    private CameraImagePicker cameraPicker;
    private String pickerPath;

    private RecyclerView preview_icon_recyclerView;
    private ImagePreviewBottomIconAdapter imagePreviewBottomIconAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        //ivImageGlide=findViewById(R.id.ivImageGlide);
         topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        topToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
            public void onClick(View view) {
                finish();
            }
        });
        intiView();


    }

    private void intiView() {

        preview_gallry_recyclerView=findViewById(R.id.preview_gallry_recyclerView);
      //  preview_icon_recyclerView=findViewById(R.id.preview_icon_recyclerView);


        ok=findViewById(R.id.ok);
        moreImage=findViewById(R.id.moreImage);
//        layout_icon = (LinearLayout) findViewById(R.id.linear_icon);



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("IMAGES").putParcelableArrayListExtra("images", (ArrayList<ChosenImage>) images);
                //Intent intent = new Intent("IMAGES").putExtra("BG_SELECT",  images.size());

                LocalBroadcastManager.getInstance(ImagePreviewActivity.this).sendBroadcast(intent);
                finish();
            }
        });

        moreImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageMultiple();
            }
        });
        images= getIntent().getParcelableArrayListExtra("data");
        if(images.size()>0){
            displayImage(images);
            //DisplayIcon(images);
        }
    }

    private void displayImage( List<ChosenImage> images) {

        this.images=images;
        //this.images.addAll(images);

       // Toast.makeText(getApplicationContext(),"showing ",Toast.LENGTH_LONG).show();

        imagePreviewAdapter=new ImagePreviewAdapter(this,images,this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        preview_gallry_recyclerView = (RecyclerView) findViewById(R.id.preview_gallry_recyclerView);
        preview_gallry_recyclerView.setLayoutManager(linearLayoutManager);
        preview_gallry_recyclerView.setItemAnimator(new DefaultItemAnimator());
        preview_gallry_recyclerView.setAdapter(imagePreviewAdapter);

        preview_gallry_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    //Dragging
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    review_position = linearLayoutManager.findFirstVisibleItemPosition();
                    onRotateGallaryImage(review_position);
                 //  Toast.makeText(getApplicationContext(),review_position+"",Toast.LENGTH_LONG).show();
                  //// Here load the Image to image view with picaso


                }
            }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);


        }
    });

        imagePreviewBottomIconAdapter=new ImagePreviewBottomIconAdapter(this,images,this);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        preview_icon_recyclerView = (RecyclerView) findViewById(R.id.preview_icon_recyclerView);
        preview_icon_recyclerView.setLayoutManager(linearLayoutManager1);
        preview_icon_recyclerView.setItemAnimator(new DefaultItemAnimator());
        preview_icon_recyclerView.setAdapter(imagePreviewBottomIconAdapter);

        imagePreviewAdapter.notifyDataSetChanged();
        imagePreviewBottomIconAdapter.notifyDataSetChanged();
    }

    public void DisplayMoreImages(List<ChosenImage> images){

        this.images.addAll(images);

        imagePreviewAdapter.notifyDataSetChanged();
        imagePreviewBottomIconAdapter.notifyDataSetChanged();

    }

    public void setCallback(ImagePickerCallback callback) {
        this.callback = callback;
    }

    private ImagePicker imagePicker;



    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        Toast.makeText(getApplicationContext(),"getting response in imagepreview"+images.size(),Toast.LENGTH_LONG).show();

        DisplayMoreImages(images);
       // DisplayIcon(images);

    }

    @Override
    public void onError(String message) {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.delete:
                deleteSelectedImage();
                break;
            case R.id.camara:
                takePicture();
                break;
            default:
                return true;


        }


        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedImage() {
        if(images!=null){
            images.remove(review_position);
            imagePreviewAdapter.notifyDataSetChanged();
            imagePreviewBottomIconAdapter.notifyDataSetChanged();

        }


    }


    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.setDebugglable(true);
        cameraPicker.setCacheLocation(CacheLocation.EXTERNAL_STORAGE_APP_DIR);
        cameraPicker.setMultipleImagePickerCallback(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        cameraPicker.allowMultiple();
        pickerPath = cameraPicker.pickImage();
    }

    public void pickImageMultiple() {
        imagePicker = new ImagePicker(this);
        imagePicker.setMultipleImagePickerCallback(this);
        imagePicker.allowMultiple();
        imagePicker.pickImage();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // After Activity recreate, you need to re-intialize these
        // two values to be able to re-intialize CameraImagePicker
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
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
                    imagePicker.setMultipleImagePickerCallback(this);
                }
                imagePicker.submit(data);
            }
            else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                cameraPicker.submit(data);
            }
        }
    }


    @Override
    public void onImageSelecte(int position) {

        onRotateGallaryImage(position);
    }

    public void onRotateGallaryImage(int position){
        for (ChosenImage image : images) {
            image.setIsSelected(0);
        }

        images.get(position).setIsSelected(1);
        imagePreviewBottomIconAdapter.notifyDataSetChanged();
        preview_icon_recyclerView.getLayoutManager().scrollToPosition(position);

        //Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onImageSelectIcon(int position) {

        preview_gallry_recyclerView.getLayoutManager().smoothScrollToPosition(preview_gallry_recyclerView, new RecyclerView.State(),position);
        Toast.makeText(getApplicationContext(),position+"",Toast.LENGTH_LONG).show();
    }
}
