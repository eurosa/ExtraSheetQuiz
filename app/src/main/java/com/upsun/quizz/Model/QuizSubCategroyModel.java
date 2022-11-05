package com.upsun.quizz.Model;

public class QuizSubCategroyModel {
    private String subCategoryName;
    private String subCategoryParentId;
    private String SubCategoryId;
    private String subCategoryImageUrl;
    private String subCategoryStaus;

    public QuizSubCategroyModel(){}

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryParentId() {
        return subCategoryParentId;
    }

    public void setSubCategoryParentId(String subCategoryParentId) {
        this.subCategoryParentId = subCategoryParentId;
    }

    public String getSubCategoryId() {
        return SubCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        SubCategoryId = subCategoryId;
    }

    public String getSubCategoryImageUrl() {
        return subCategoryImageUrl;
    }

    public void setSubCategoryImageUrl(String subCategoryImageUrl) {
        this.subCategoryImageUrl = subCategoryImageUrl;
    }

    public String getSubCategoryStaus() {
        return subCategoryStaus;
    }

    public void setSubCategoryStaus(String subCategoryStaus) {
        this.subCategoryStaus = subCategoryStaus;
    }
}
