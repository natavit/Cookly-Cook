package com.natavit.cooklycook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.natavit.cooklycook.R;
import com.natavit.cooklycook.db.DBCooklyCook;
import com.natavit.cooklycook.manager.AccountManager;
import com.natavit.cooklycook.util.Utils;
import com.natavit.cooklycook.util.album.AlbumStorageDirFactory;
import com.natavit.cooklycook.util.album.BaseAlbumDirFactory;
import com.natavit.cooklycook.util.album.FroyoAlbumDirFactory;
import com.natavit.cooklycook.view.IngredientViewGroup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * This class is used to add a new recipe stored in the local database (SQLite)
 */
public class AddRecipeActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     *
     * Variable
     *
     */

    private static final int IMAGE_FROM_CAMERA = 1001;
    private static final int IMAGE_FROM_GALLERY = 1002;

    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private final String[] imgActions = new String[]{"From Camera", "From Gallery"};

    CoordinatorLayout coordinatorLayout;
    Toolbar toolbar;

    ImageView ivImgRecipe;
    FancyButton btnAddImgRecipe;
    EditText etFoodName;
    ImageView ivAddIngredient;
    LinearLayout linearLayoutIngredient;

    AlertDialog.Builder builder;
    AlertDialog dialog;

    DBCooklyCook dbHelper;
    SQLiteDatabase db;

    /**
     *
     * Function
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        initInstances();
        initImageLoader();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
    }

    /**
     * Initialize view variables
     */
    private void initInstances() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImgRecipe = (ImageView) findViewById(R.id.ivImgRecipe);
        ivImgRecipe.setOnClickListener(this);

        btnAddImgRecipe = (FancyButton) findViewById(R.id.btnAddImgRecipe);
        btnAddImgRecipe.setOnClickListener(this);

        etFoodName = (EditText) findViewById(R.id.etFoodName);

        ivAddIngredient = (ImageView) findViewById(R.id.ivAddIngredient);
        ivAddIngredient.setOnClickListener(this);

        linearLayoutIngredient = (LinearLayout) findViewById(R.id.linearLayoutIngredient);
    }

    /**
     * Initialize Alert Dialog
     * Show up when Add Image Button is clicked
     */
    private void initImageLoader() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, imgActions);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dispatchTakePictureIntent(IMAGE_FROM_CAMERA);
                    dialog.cancel();
                }
                else {
                    dispatchLoadPictureIntent(IMAGE_FROM_GALLERY);
                    dialog.cancel();
                }
            }
        });

        dialog = builder.create();
    }

    /**
     * Save a new recipe to the local database (SQLite)
     * @return boolean to show whether it succeeds or not
     */
    private boolean saveRecipe() {

        String recipeName = etFoodName.getText().toString();

        if (recipeName.equals("") || recipeName.equals(" ")) {
            return false;
        }

        String imgPath = mCurrentPhotoPath;

        dbHelper = new DBCooklyCook(this);
        db = dbHelper.getWritableDatabase();

        int count = linearLayoutIngredient.getChildCount();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE
                                + " WHERE " + DBCooklyCook.COL_RECIPE_NAME + "='" + recipeName + "' AND "
                                + DBCooklyCook.COL_RECIPE_OWNER + "='" + AccountManager.getInstance().getName()
                                +  AccountManager.getInstance().getLoginTypeString() + "'", null);

//        Cursor cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE
//                + " WHERE " + DBCooklyCook.COL_RECIPE_NAME + "='" + recipeName + "'", null);

        if (cursor.getCount() == 0) {

            db.execSQL("INSERT INTO " + DBCooklyCook.TABLE_RECIPE + " ("
                    + DBCooklyCook.COL_RECIPE_NAME + ", " + DBCooklyCook.COL_RECIPE_IMG + ", " + DBCooklyCook.COL_RECIPE_OWNER + ")"
                    + " VALUES ('" + recipeName + "', '" + imgPath + "', '" + AccountManager.getInstance().getName()
                    +  AccountManager.getInstance().getLoginTypeString() + "');");

            IngredientViewGroup ing;
            for (int i = 0; i < count; i++) {
                ing = (IngredientViewGroup) linearLayoutIngredient.getChildAt(i);
                if (ing != null) {
                    String ingredientName = ing.getIngredientName();
                    String ingredientAmount = ing.getIngredientAmount();
                    String ingredientUnit = ing.getIngredientUnit();
                    Log.e("ING", ingredientUnit);
                    if (!ingredientName.equals("") && !ingredientName.equals(" ")
                            && !ingredientAmount.equals("") && !ingredientAmount.equals(" ")
                            && !ingredientUnit.equals("") && !ingredientUnit.equals(" ")) {
                        db.execSQL("INSERT INTO " + DBCooklyCook.TABLE_INGREDIENT + " ("
                                + DBCooklyCook.COL_ING_NAME + ", "
                                + DBCooklyCook.COL_ING_AMOUNT + ", "
                                + DBCooklyCook.COL_ING_UNIT + ", "
                                + DBCooklyCook.COL_ING_FOREIGN + ")"
                                + " VALUES ('" + ingredientName + "', '"
                                + ingredientAmount + "', '"
                                + ingredientUnit + "', '"
                                + recipeName + "');");
                    }
                }
            }

        }
        else {
            Utils.getInstance().showSnackBarShort("The recipe name has been already used", coordinatorLayout);
        }

        dbHelper.close();
        db.close();

        return true;

    }

    /**
     *
     * Image Management
     * Capture/Load
     *
     * Working with Camera: http://developer.android.com/training/camera/photobasics.html
     *
     */


    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }


    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("Camera", "failed to create directory");
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
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchLoadPictureIntent(int actionCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(photoPickerIntent, actionCode);
    }

    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case IMAGE_FROM_CAMERA: {

                File f;

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
            }
            default:
                break;
        }

        startActivityForResult(takePictureIntent, actionCode);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
//            setPic();
            Glide.with(AddRecipeActivity.this)
                    .load(mCurrentPhotoPath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivImgRecipe);
            galleryAddPic();
//            mCurrentPhotoPath = null;
        }

    }

    private void handleGalleryPhoto(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        // Get the cursor
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        mCurrentPhotoPath = cursor.getString(columnIndex);
        cursor.close();
        // Set the Image in ImageView after decoding the String
        Glide.with(AddRecipeActivity.this)
                .load(mCurrentPhotoPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImgRecipe);

        btnAddImgRecipe.setVisibility(View.INVISIBLE);
//        mCurrentPhotoPath = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_FROM_CAMERA: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();

                    btnAddImgRecipe.setVisibility(View.INVISIBLE);
                }
                else {
                    new File(mCurrentPhotoPath).delete();
                }
                break;
            }
            case IMAGE_FROM_GALLERY: {
                if (resultCode == RESULT_OK) {
                    handleGalleryPhoto(data);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.action_save: {
                if(saveRecipe()) {
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    Toast.makeText(this, "Food name cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
            default:
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAddIngredient: {
                IngredientViewGroup ingredientViewGroup = new IngredientViewGroup(AddRecipeActivity.this);
                ingredientViewGroup.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                linearLayoutIngredient.addView(ingredientViewGroup);
                break;
            }
            case R.id.ivImgRecipe: {
                dialog.show();
                break;
            }
            case R.id.btnAddImgRecipe: {
                dialog.show();
                break;
            }
        }
    }

}
