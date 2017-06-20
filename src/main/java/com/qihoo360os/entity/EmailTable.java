package com.qihoo360os.entity;

import java.util.Date;

/**
 * Created by i-chendonglin on 2017/6/20.
 */
public class EmailTable {
    public String tableHead;
    public String programName;
    public String phoneVersion;
    public String flashVersion;
    public Date testDate;
    public String rdLeader;    //reasearch and development
    public String tester;
    public String testResult;
    public String pushWay;
    public String flashLink;
    public String confLink;
    public String rescovery;
    public String relation;
    public String suggest;

    @Override
    public String toString() {
        return "EmailTable{" +
                "tableHead='" + tableHead + '\'' +
                ", programName='" + programName + '\'' +
                ", phoneVersion='" + phoneVersion + '\'' +
                ", flashVersion='" + flashVersion + '\'' +
                ", testDate=" + testDate +
                ", rdLeader='" + rdLeader + '\'' +
                ", tester='" + tester + '\'' +
                ", testResult='" + testResult + '\'' +
                ", pushWay='" + pushWay + '\'' +
                ", flashLink='" + flashLink + '\'' +
                ", confLink='" + confLink + '\'' +
                ", rescovery='" + rescovery + '\'' +
                ", relation='" + relation + '\'' +
                ", suggest='" + suggest + '\'' +
                '}';
    }

    public String getRescovery() {
        return rescovery;
    }

    public void setRescovery(String rescovery) {
        this.rescovery = rescovery;
    }

    public String getTableHead() {
        return tableHead;
    }

    public void setTableHead(String tableHead) {
        this.tableHead = tableHead;
    }

    public String getProgramName() {
        return programName;
    }

    public String getFlashVersion() {
        return flashVersion;
    }

    public void setFlashVersion(String flashVersion) {
        this.flashVersion = flashVersion;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getPhoneVersion() {
        return phoneVersion;
    }

    public void setPhoneVersion(String phoneVersion) {
        this.phoneVersion = phoneVersion;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getRdLeader() {
        return rdLeader;
    }

    public void setRdLeader(String rdLeader) {
        this.rdLeader = rdLeader;
    }

    public String getTester() {
        return tester;
    }

    public void setTester(String tester) {
        this.tester = tester;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getPushWay() {
        return pushWay;
    }

    public void setPushWay(String pushWay) {
        this.pushWay = pushWay;
    }

    public String getFlashLink() {
        return flashLink;
    }

    public void setFlashLink(String flashLink) {
        this.flashLink = flashLink;
    }

    public String getConfLink() {
        return confLink;
    }

    public void setConfLink(String confLink) {
        this.confLink = confLink;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

}
