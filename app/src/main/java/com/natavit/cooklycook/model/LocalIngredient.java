package com.natavit.cooklycook.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Natavit on 4/14/2016 AD.
 */
public class LocalIngredient implements Parcelable {
    private String name;
    private String amount;
    private String fr;

    public LocalIngredient() {

    }

    protected LocalIngredient(Parcel in) {
        name = in.readString();
        amount = in.readString();
        fr = in.readString();
    }

    public static final Creator<LocalIngredient> CREATOR = new Creator<LocalIngredient>() {
        @Override
        public LocalIngredient createFromParcel(Parcel in) {
            return new LocalIngredient(in);
        }

        @Override
        public LocalIngredient[] newArray(int size) {
            return new LocalIngredient[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFr() {
        return fr;
    }

    public void setFr(String fr) {
        this.fr = fr;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(amount);
        dest.writeString(fr);
    }
}
