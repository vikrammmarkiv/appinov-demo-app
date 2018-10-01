package vikram.demoapp.Acitivty;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import vikram.demoapp.Adapter.ImagesViewAdapter;
import vikram.demoapp.R;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1,REQUEST_IMAGE_GALLERY=2;
    String mCurrentPhotoPath;
    FloatingActionButton cameraButton;
    ArrayList<String> imagePaths = new ArrayList<>();
    RecyclerView imagesView;
    ImagesViewAdapter imAdapter;
    AlertDialog.Builder builder;
    private BottomNavigationView navigation;
    private TextView emptyText;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.task1:
                    return true;
                case R.id.task2:
                    intent = new Intent(MainActivity.this,ActivityTask2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
                case R.id.task3:
                    intent = new Intent(MainActivity.this,ActivityTask3.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.task1);

        emptyText = findViewById(R.id.emptyText);
        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });
        imagesView = findViewById(R.id.imagesView);
        imagesView.setLayoutManager(new GridLayoutManager(this, 3));
        Set<String> set = getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE)
                .getStringSet("key", null);
        if (set!=null)
            imagePaths.addAll(set);
        imAdapter = new ImagesViewAdapter(this,imagePaths);
        imagesView.setAdapter(imAdapter);
        if (imAdapter.getItemCount()>0)
            emptyText.setVisibility(View.GONE);

        final CharSequence[] options = { getString(R.string.take_photo), getString(R.string.choose_from_gallery),getString(R.string.cancel) };
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.add_photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.take_photo)))
                {
                    dispatchTakePictureIntent();
                }
                else if (options[item].equals(getString(R.string.choose_from_gallery)))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                }
                else if (options[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imagePaths.add(mCurrentPhotoPath);
            imAdapter.notifyItemInserted(imagePaths.size()-1);
        }
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++)
                    imagePaths.add(data.getClipData().getItemAt(i).getUri().toString());
                imAdapter.notifyDataSetChanged();
            }
            else if (data.getData()!=null) {
                imagePaths.add(data.getData().toString());
                imAdapter.notifyItemInserted(imagePaths.size()-1);
            }
        }
        if (imAdapter.getItemCount()>0)
            emptyText.setVisibility(View.GONE);
        imagesView.smoothScrollToPosition(imAdapter.getItemCount());
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.vikram.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =  new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Set<String> set = new HashSet<String>(imagePaths);
        getSharedPreferences(getPackageName()+"_preferences",MODE_PRIVATE).edit().putStringSet("key", set).apply();
    }


    @Override
    public void onResume(){
        super.onResume();
        navigation.setSelectedItemId(R.id.task1);
    }
}
