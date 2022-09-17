package com.example.khunpet.ui.activities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.khunpet.R;
import com.example.khunpet.controllers.view_models.InsertPublicationRefugViewModel;
import com.example.khunpet.googleapis.GetOAuthToken;
import com.example.khunpet.model.AdoptionPublication;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivityVision extends AppCompatActivity {

   private InsertPublicationRefugViewModel viewModel;

    private final String TAG = "CloudVisionExample";

    private Uri uriGlobal ;

    static final int REQUEST_GALLERY_IMAGE = 100;
    static final int REQUEST_CODE_PICK_ACCOUNT = 101;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 102;
    static final int REQUEST_PERMISSIONS = 13;

    private static String accessToken;
    private ImageView selectedImage;
    private TextView labelResults;
    private TextView textResults;
    private Account mAccount;
    private ProgressDialog mProgressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_vision);

        mProgressDialog = new ProgressDialog(this);

        Button selectImageButton = findViewById(R.id.select_image_button);
        Button selectImageButton2 = findViewById(R.id.select_image_button2);

        selectedImage = findViewById(R.id.selected_image);
        labelResults = findViewById(R.id.tv_label_results);
        textResults = findViewById(R.id.tv_texts_results);

       // InsertPublicationRefugViewModel  viewModel = ViewModelProviders.of(this).get(InsertPublicationRefugViewModel.class);

         viewModel = new ViewModelProvider(this).get(InsertPublicationRefugViewModel.class);
        //viewModel.uploadJsonToFirebaseStorage();


        ActivityCompat.requestPermissions(MainActivityVision.this,
                new String[]{ Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                },
                REQUEST_PERMISSIONS);



        //String valor = getIntent().getStringExtra("URIPET");
        // performCloudVisionRequest( Uri.parse( valor ) );


        selectImageButton.setOnClickListener(new View.OnClickListener() {
          //@RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(MainActivityVision.this,
                        new String[]{ Manifest.permission.GET_ACCOUNTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_PERMISSIONS);

             //   RequestPermissions();

              /*  try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
*/


                //subir publiacion
               // product = intent.getParcelableExtra<Product>("AnyNameOrKey")
                //AdoptionPublication publication =  getIntent().getParcelableExtra("PUBLICACTION_SERIAL");

               //   viewModel.publish(publication, publication.getPetID(), uriGlobal);


                //  viewModel.uploadJsonToFirebaseStorage();

                   //changeToMainActivity();
            }


        } );

        selectImageButton2.setOnClickListener(item -> {

            AdoptionPublication publication =  getIntent().getParcelableExtra("PUBLICACTION_SERIAL");

            viewModel.publish(publication, publication.getPetID(), uriGlobal);


            viewModel.uploadJsonToFirebaseStorage();

           // changeToMainActivity();


        } ) ;


    }








    View layout;
    //pilas
  //  @RequiresApi(api = Build.VERSION_CODES.R)
    void RequestPermissions()
    {
     //   if (Environment.isExternalStorageManager() ){

            Log.d(TAG, "RequiresApi"+ String.valueOf(Build.VERSION_CODES.R));
            Log.d(TAG, "Inside RequestPermissions");


// If you don't have access, launch a new activity to show the user the system's dialog
// to allow access to the external storage
      //  }else{
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
      //  }
    }







//ORIGINAL---------------------------------
    private void launchImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select an image"),
                REQUEST_GALLERY_IMAGE);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAuthToken();
                } else {
                    Toast.makeText(MainActivityVision.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == RESULT_OK && data != null) {

            performCloudVisionRequest(data.getData());

            //podria enviar aqui la imagen de data ? ( es una uri en el metodo perform cloud )
            //viewModel.



        } else if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                AccountManager am = AccountManager.get(this);
                Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                for (Account account : accounts) {
                    if (account.name.equals(email)) {
                        mAccount = account;
                        break;
                    }
                }
                getAuthToken();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No Account Selected", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_ACCOUNT_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                onTokenReceived(extra.getString("authtoken"));
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Authorization Failed", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void performCloudVisionRequest(Uri uri) {

        if (uri != null) {

            try {

                uriGlobal  = uri ;


                Bitmap bitmap = resizeBitmap(
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                callCloudVision(bitmap);

                selectedImage.setImageBitmap(bitmap);





            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        mProgressDialog = ProgressDialog.show(this, null,"Scanning image with Vision API...", true);

        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
                try {
                    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder
                            (httpTransport, jsonFactory, credential);
                    Vision vision = builder.build();

                    List<Feature> featureList = new ArrayList<>();

                    Feature labelDetection = new Feature();
                    labelDetection.setType("LABEL_DETECTION");
                    labelDetection.setMaxResults(10);
                    featureList.add(labelDetection);

                    Feature imageProperties = new Feature();
                    imageProperties.setType("IMAGE_PROPERTIES");
                    imageProperties.setMaxResults(10);
                    featureList.add(imageProperties);

                    Feature cropHints = new Feature();
                    cropHints.setType("CROP_HINTS");
                    cropHints.setMaxResults(10);
                    featureList.add(cropHints);

                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION");
                    textDetection.setMaxResults(10);
                    featureList.add(textDetection);




                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "Sending request to Google Cloud");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();



                    return response;

                } catch (GoogleJsonResponseException e) {
                    Log.e(TAG, "Request error: " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "Request error: " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {

               // String PetID = "a4asdkn4";
                String PetID =  getSharedPreferencePetIDVISION();
                // List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();

                JSONArray jsArray = null;


                jsArray = new JSONArray(response.getResponses());


                try {

                    FileWriter file = new FileWriter("/storage/1DFA-2608/MyIdea/" + PetID + "-1.json");
                    BufferedWriter output = new BufferedWriter(file);
                  String  jsonArrayFinal = jsArray.toString();

                    output.write(jsonArrayFinal.substring(1, (jsonArrayFinal.length() - 1) )  );

                     output.close();

                     Toast.makeText(getApplicationContext(), "Json saved", Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }



                viewModel.uploadJsonToFirebaseStorage();


                mProgressDialog.dismiss();
                textResults.setText(getDetectedTexts(response));
                labelResults.setText(getDetectedLabels(response));
            }

        }.execute();
    }

    private String getDetectedLabels(BatchAnnotateImagesResponse response){
        StringBuilder message = new StringBuilder("");
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();


        if (labels != null) {
            for (EntityAnnotation label : labels) {

                message.append(String.format(Locale.getDefault(), "%.3f: %s",
                        label.getScore(), label.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing\n");
        }

        return message.toString();
    }

    private String getDetectedTexts(BatchAnnotateImagesResponse response){
        StringBuilder message = new StringBuilder("");
        List<EntityAnnotation> texts = response.getResponses().get(0).getTextAnnotations();

        if (texts != null) {
            for (EntityAnnotation text : texts) {
                message.append(String.format(Locale.getDefault(), "%s: %s",
                        text.getLocale(), text.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing\n");
        }

        return message.toString();
    }


  /*  private String getDetectedProperties(BatchAnnotateImagesResponse response){
        StringBuilder message = new StringBuilder("");
        List<EntityAnnotation> texts = response.getResponses().get(0).getImagePropertiesAnnotation();

        if (texts != null) {
            for (EntityAnnotation text : texts) {
                message.append(String.format(Locale.getDefault(), "%s: %s",
                        text.getLocale(), text.getDescription()));
                message.append("\n");
            }
        } else {
            message.append("nothing\n");
        }

        return message.toString();
    }
    */


    public Bitmap resizeBitmap(Bitmap bitmap) {

        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

    private void pickUserAccount() {
        String[] accountTypes = new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    private void getAuthToken() {
        String SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform";
        if (mAccount == null) {
            pickUserAccount();
        } else {
            new GetOAuthToken(MainActivityVision.this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                    .execute();
        }
    }

    public void onTokenReceived(String token){
        accessToken = token;



        launchImagePicker();



    }

    private String getSharedPreferencePetIDVISION(){
    SharedPreferences sh = getSharedPreferences("confData", Context.MODE_PRIVATE);
      //TAMBIEN VALIO XD
        // SharedPreferences   editorSP = AppDatabase.Companion.getShareDB();

    String PetID = sh.getString("petIDVISION", "");
        //TAMBIEN VALIO XD
   // String PetID2 = editorSP.getString("petIDVISION", "");

        return PetID;
    }



    private void changeToMainActivity(){


        Intent intent = new Intent(this, MainActivityRefug.class);
        startActivity(intent);

    }





    }



