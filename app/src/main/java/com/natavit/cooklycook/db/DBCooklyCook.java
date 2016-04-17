package com.natavit.cooklycook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Natavit on 4/14/2016 AD.
 */
public class DBCooklyCook extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CooklyCook.db";

    public static final String TABLE_RECIPE = "LocalRecipe";
    public static final String COL_RECIPE_ID = "id";
    public static final String COL_RECIPE_NAME = "name";
    public static final String COL_RECIPE_IMG = "img";
    public static final String COL_RECIPE_OWNER = "owner";

    public static final String TABLE_INGREDIENT = "Ingredient";
    public static final String COL_ING_ID = "id";
    public static final String COL_ING_NAME = "name";
    public static final String COL_ING_AMOUNT = "amount";
    public static final String COL_ING_FOREIGN = "fr";

    private static final String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPE + " ("
            + COL_RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_RECIPE_NAME + " TEXT NOT NULL, "
            + COL_RECIPE_IMG + " TEXT NOT NULL, "
            + COL_RECIPE_OWNER + " TEXT NOT NULL);";

    private static final String CREATE_INGREDIENT_TABLE = "CREATE TABLE " + TABLE_INGREDIENT + " ("
            + COL_ING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ING_NAME + " TEXT NOT NULL, "
            + COL_ING_AMOUNT + " TEXT NOT NULL, "
            + COL_ING_FOREIGN + " TEXT NOT NULL);";

    public DBCooklyCook(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENT);
        onCreate(db);
    }
}
