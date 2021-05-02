package com.nmadpl.pitstop.models;

import java.io.Serializable;

public class CompanyModel implements Serializable {
    private String mfgCode,logo;
    int rank;

    public CompanyModel() {
    }

    public CompanyModel(String mfgCode, int rank) {
        this.mfgCode = mfgCode;
        this.rank = rank;
    }

    public String getMfgCode() {
        return mfgCode;
    }

    public void setMfgCode(String mfgCode) {
        this.mfgCode = mfgCode;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
