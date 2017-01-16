package com.natavit.cooklycook.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Natavit on 1/30/2016 AD.
 */

/**
 *
 * Edamam API
 *
 */
public class RecipeDao implements Parcelable {

    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("source")
    @Expose
    private String source;
    @SerializedName("sourceIcon")
    @Expose
    private String sourceIcon;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("shareAs")
    @Expose
    private String shareAs;
    @SerializedName("ingredientLines")
    @Expose
    private List<String> ingredientLines = new ArrayList<String>();
    @SerializedName("calories")
    @Expose
    private Double calories;

    /**
     * @return The uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return The image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image The image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return The source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return The sourceIcon
     */
    public String getSourceIcon() {
        return sourceIcon;
    }

    /**
     * @param sourceIcon The sourceIcon
     */
    public void setSourceIcon(String sourceIcon) {
        this.sourceIcon = sourceIcon;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The shareAs
     */
    public String getShareAs() {
        return shareAs;
    }

    /**
     * @param shareAs The shareAs
     */
    public void setShareAs(String shareAs) {
        this.shareAs = shareAs;
    }

    /**
     * @return The ingredientLines
     */
    public List<String> getIngredientLines() {
        return ingredientLines;
    }

    /**
     * @param ingredientLines The ingredientLines
     */
    public void setIngredientLines(List<String> ingredientLines) {
        this.ingredientLines = ingredientLines;
    }

    /**
     * @return The calories
     */
    public Double getCalories() {
        return calories;
    }

    /**
     * @param calories The calories
     */
    public void setCalories(Double calories) {
        this.calories = calories;
    }

    protected RecipeDao(Parcel in) {
        uri = in.readString();
        label = in.readString();
        image = in.readString();
        source = in.readString();
        sourceIcon = in.readString();
        url = in.readString();
        shareAs = in.readString();
        ingredientLines = in.createStringArrayList();
        calories = in.readDouble();
    }

    public static final Creator<RecipeDao> CREATOR = new Creator<RecipeDao>() {
        @Override
        public RecipeDao createFromParcel(Parcel in) {
            return new RecipeDao(in);
        }

        @Override
        public RecipeDao[] newArray(int size) {
            return new RecipeDao[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(label);
        dest.writeString(image);
        dest.writeString(source);
        dest.writeString(sourceIcon);
        dest.writeString(url);
        dest.writeString(shareAs);
        dest.writeStringList(ingredientLines);
        dest.writeDouble(calories);
    }
}
