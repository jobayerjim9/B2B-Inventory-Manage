package com.nmadpl.pitstop.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModel implements Serializable {
    private String mfgCode,itemCode,itemName;
    private String rate;
    private String unit,modelName,typeName,description;
    private ArrayList<String> images=new ArrayList<>();
    private boolean isFeatured;
    public ProductModel() {
        mfgCode="";
        itemCode="";
        itemName="";
        rate="";
        unit="";
        modelName="";
        typeName="";
        description="";

    }


    public ProductModel(String mfgCode, String itemCode, String itemName, String rate, String unit, String modelName, String typeName, String description) {
        this.mfgCode = mfgCode;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.rate = rate;
        this.unit = unit;
        this.modelName = modelName;
        this.typeName = typeName;
        this.description = description;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getMfgCode() {
        return mfgCode;
    }

    public void setMfgCode(String mfgCode) {
        this.mfgCode = mfgCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }



    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }


}
