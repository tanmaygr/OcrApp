package com.example.tanmay.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.example.tanmay.myapplication.MainActivity.string;


public class camera extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private File output=null;
    TextView vanshaj;
    Canvas canvas;
    String phrase;
    Bitmap photo;
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        vanshaj=findViewById(R.id.textView2);
        imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        phrase= string;
        canvas=new Canvas();
        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
//                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
////                    File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
////                    output=new File(dir, FILENAME);
//
////                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
//
//
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                    dispatchTakePictureIntent();

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
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
//                Toast.makeText(this, "Fail Inside dispatchPicture", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

//                Toast.makeText(this, "In Uri", Toast.LENGTH_SHORT).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//                onActivityResult();
            }
        }
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            Log.d("Vanshaj","Vanshajjjj");
//        if(requestCode==CAMERA_REQUEST&&resultCode == Activity.RESULT_OK){

            setPic();
//        }
    }

    public void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
//        Toast.makeText(this, "In setPic", Toast.LENGTH_SHORT).show();
        Log.d("aryan","aryan");

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
        textRecognisition(bitmap);
    }
    public void textRecognisition(Bitmap photo){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photo);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                String resultText = firebaseVisionText.getText();

                                for (FirebaseVisionText.TextBlock block: firebaseVisionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    Log.d("InLoop",blockText+" "+blockConfidence);
                                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                    Log.d("blockLang",blockLanguages.toString());
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();

                                    if (blockText.equals(phrase)){
                                        Log.d("PointAndRect",blockText+" -> "+blockFrame.toString());
                                        Bitmap mutableBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);
                                        Canvas canvas = new Canvas(mutableBitmap);

                                        Paint paint = new Paint();
//                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeWidth(12);
                                        paint.setColor(Color.BLACK);
                                        paint.setStyle(Paint.Style.STROKE);
                                        Rect rectangle = new Rect(blockFrame);

                                        canvas.drawRect(rectangle,paint);
                                        imageView.setImageBitmap(mutableBitmap);
                                        break;
                                    }
//                                    for (FirebaseVisionText.Line line: block.getLines()) {
//                                        String lineText = line.getText();
//                                        Log.d("InSecondLoop",lineText);
//                                        Float lineConfidence = line.getConfidence();
////                                        Log.d("lineConf",lineConfidence);
//                                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
//                                        Point[] lineCornerPoints = line.getCornerPoints();
//                                        if(lineCornerPoints!=null)
//                                        Log.d("inLINEPOINTS",lineCornerPoints.toString());
//                                        Rect lineFrame = line.getBoundingBox();
//                                        Paint paint=new Paint();
////                                        for (FirebaseVisionText.Element element: line.getElements()) {
//                                            String elementText = element.getText();
//                                            Float elementConfidence = element.getConfidence();
//                                            List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
//                                            Point[] elementCornerPoints = element.getCornerPoints();
//                                            Rect elementFrame = element.getBoundingBox();
//                                            Log.d("elementFrame",elementFrame.toString());
//                                            paint.setStyle(Paint.Style.STROKE);
//                                            paint.setColor(Color.BLACK);
//                                            canvas.drawColor(Color.BLUE);
//                                            canvas.drawRect(blockFrame,paint);
//                                        }
//                                    }
                                }
//                                Toast.makeText(camera.this,resultText, Toast.LENGTH_LONG).show();
                                vanshaj.setText(resultText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(camera.this, "Unable to detect", Toast.LENGTH_LONG).show();
                                    }
                                });

    }
}



