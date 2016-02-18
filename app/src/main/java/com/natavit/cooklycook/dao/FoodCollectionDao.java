package com.natavit.cooklycook.dao;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Natavit on 1/29/2016 AD.
 */
public class FoodCollectionDao {

    /**
     *
     * Edamam FoodEdamam API
     *
     */

    @SerializedName("q")
    @Expose
    private String q;
    @SerializedName("from")
    @Expose
    private Integer from;
    @SerializedName("to")
    @Expose
    private Integer to;
    @SerializedName("more")
    @Expose
    private Boolean more;
    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("hits")
    @Expose
    private List<HitDao> hits = new ArrayList<HitDao>();

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
    public Integer getFrom() {
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
    public Integer getTo() {
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


}
