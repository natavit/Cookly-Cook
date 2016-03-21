package com.natavit.cooklycook.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Natavit on 1/29/2016 AD.
 */
public class FoodCollectionDao implements Parcelable{

    /**
     *
     * Edamam API
     *
     */

    @SerializedName("q")
    @Expose
    private String q;
    @SerializedName("from")
    @Expose
    private int from;
    @SerializedName("to")
    @Expose
    private int to;
    @SerializedName("more")
    @Expose
    private Boolean more;
    @SerializedName("count")
    @Expose
    private int count;
    @SerializedName("hits")
    @Expose
    private List<HitDao> hits = new ArrayList<HitDao>();

    public FoodCollectionDao() {

    }

    protected FoodCollectionDao(Parcel in) {
        q = in.readString();
        hits = in.createTypedArrayList(HitDao.CREATOR);
    }

    public static final Creator<FoodCollectionDao> CREATOR = new Creator<FoodCollectionDao>() {
        @Override
        public FoodCollectionDao createFromParcel(Parcel in) {
            return new FoodCollectionDao(in);
        }

        @Override
        public FoodCollectionDao[] newArray(int size) {
            return new FoodCollectionDao[size];
        }
    };

    /**
     * @return The q
     */
    public String getQ() {
        return q;
    }

    /**
     * @param q The q
     */
    public void setQ(String q) {
        this.q = q;
    }

    /**
     * @return The from
     */
    public int getFrom() {
        return from;
    }

    /**
     * @param from The from
     */
    public void setFrom(Integer from) {
        this.from = from;
    }

    /**
     * @return The to
     */
    public int getTo() {
        return to;
    }

    /**
     * @param to The to
     */
    public void setTo(Integer to) {
        this.to = to;
    }

    /**
     * @return The more
     */
    public Boolean getMore() {
        return more;
    }

    /**
     * @param more The more
     */
    public void setMore(Boolean more) {
        this.more = more;
    }

    /**
     * @return The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return The hits
     */
    public List<HitDao> getHits() {
        return hits;
    }

    /**
     * @param hits The hits
     */
    public void setHits(List<HitDao> hits) {
        this.hits = hits;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(q);
        dest.writeTypedList(hits);
    }
}
