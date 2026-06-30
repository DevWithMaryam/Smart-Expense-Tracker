package com.maryam.smartexpensetracker.model;

public class Report {
    private String fileName;
    private String filePath;
    private String generatedDate;
    private String month;

    public Report() {}

    public Report(String fileName, String filePath, String generatedDate, String month) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.generatedDate = generatedDate;
        this.month = month;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(String generatedDate) { this.generatedDate = generatedDate; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
}