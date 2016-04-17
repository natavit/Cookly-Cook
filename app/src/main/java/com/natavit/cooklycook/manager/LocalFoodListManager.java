package com.natavit.cooklycook.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.natavit.cooklycook.db.DBCooklyCook;
import com.natavit.cooklycook.model.LocalIngredient;
import com.natavit.cooklycook.model.LocalRecipe;

import java.util.ArrayList;

/**
 * Created by Natavit on 2/4/2016 AD.
 */
public class LocalFoodListManager {

    private ArrayList<LocalRecipe> recipes;

    DBCooklyCook dbHelper;
    SQLiteDatabase db;
    Cursor cursor;

    private static LocalFoodListManager instance;

    private Context mContext;

    public static LocalFoodListManager getInstance() {
        if (instance == null)
            instance = new LocalFoodListManager();
        return instance;
    }

    private LocalFoodListManager() {
        mContext = Contextor.getInstance().getContext();
        recipes = new ArrayList<>();

        loadLocalRecipes();
    }

    public ArrayList<LocalRecipe> getLocalRecipes() {
        return recipes;
    }

    public void loadLocalRecipes() {
        dbHelper = new DBCooklyCook(mContext);
        db = dbHelper.getReadableDatabase();

        recipes.clear();

        cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE
                + " WHERE " + DBCooklyCook.COL_RECIPE_OWNER + "='" + AccountManager.getInstance().getName() + "'", null);

//        cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocalRecipe localRecipe = new LocalRecipe();
            String recipeName = cursor.getString(cursor.getColumnIndex(DBCooklyCook.COL_RECIPE_NAME));
            localRecipe.setName(recipeName);
            localRecipe.setImgPath(cursor.getString(cursor.getColumnIndex(DBCooklyCook.COL_RECIPE_IMG)));

            Cursor cursorIng = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_INGREDIENT
                    + " WHERE " + DBCooklyCook.COL_ING_FOREIGN + "='" + recipeName + "'", null);
            cursorIng.moveToFirst();
            ArrayList<LocalIngredient> ings = new ArrayList<>();
            while (!cursorIng.isAfterLast()) {
                LocalIngredient localIngredient = new LocalIngredient();
                localIngredient.setName(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_NAME)));
                localIngredient.setAmount(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_AMOUNT)));
                localIngredient.setFr(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_FOREIGN)));
                ings.add(localIngredient);
                cursorIng.moveToNext();
            }

            localRecipe.setIngredients(ings);

            recipes.add(0, localRecipe);
            cursor.moveToNext();
        }

        dbHelper.close();
        db.close();
    }

    public LocalRecipe getSingleLocalRecipe(String rn) {
        dbHelper = new DBCooklyCook(mContext);
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_RECIPE
                + " WHERE " + DBCooklyCook.COL_RECIPE_NAME + "='" + rn + "' AND "
                + DBCooklyCook.COL_RECIPE_OWNER + "='" + AccountManager.getInstance().getName() + "'", null);

        cursor.moveToFirst();
        LocalRecipe localRecipe = new LocalRecipe();
        String recipeName = cursor.getString(cursor.getColumnIndex(DBCooklyCook.COL_RECIPE_NAME));
        localRecipe.setName(recipeName);
        localRecipe.setImgPath(cursor.getString(cursor.getColumnIndex(DBCooklyCook.COL_RECIPE_IMG)));

        Cursor cursorIng = db.rawQuery("SELECT * FROM " + DBCooklyCook.TABLE_INGREDIENT
                + " WHERE " + DBCooklyCook.COL_ING_FOREIGN + "='" + recipeName + "'", null);
        cursorIng.moveToFirst();
        ArrayList<LocalIngredient> ings = new ArrayList<>();
        while (!cursorIng.isAfterLast()) {
            LocalIngredient localIngredient = new LocalIngredient();
            localIngredient.setName(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_NAME)));
            localIngredient.setAmount(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_AMOUNT)));
            localIngredient.setFr(cursorIng.getString(cursorIng.getColumnIndex(DBCooklyCook.COL_ING_FOREIGN)));
            ings.add(localIngredient);
            cursorIng.moveToNext();
        }

        localRecipe.setIngredients(ings);

        dbHelper.close();
        db.close();

        return localRecipe;
    }

    public int getCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }

}
