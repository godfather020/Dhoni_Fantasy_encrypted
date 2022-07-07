package com.app.dharaneesh;

public class MatchDataModel {
    String postdatetime,postdetails,postimg,posttitle, postTeamType, postMatchType, postLeagueType, id, isScheduled, scheduledTime;

    public MatchDataModel() {
    }

    public MatchDataModel(String postdatetime, String postdetails, String postimg, String posttitle,
                          String postTeamType, String postMatchType, String postLeagueType, String id, String isScheduled, String scheduledTime) {
        this.postdatetime = postdatetime;
        this.postdetails = postdetails;
        this.postimg = postimg;
        this.posttitle = posttitle;
        this.postTeamType = postTeamType;
        this.postMatchType = postMatchType;
        this.postLeagueType = postLeagueType;
        this.id = id;
        this.isScheduled = isScheduled;
        this.scheduledTime = scheduledTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(String isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostdatetime() {
        return postdatetime;
    }

    public void setPostdatetime(String postdatetime) {
        this.postdatetime = postdatetime;
    }

    public String getPostdetails() {
        return postdetails;
    }

    public void setPostdetails(String postdetails) {
        this.postdetails = postdetails;
    }

    public String getPostimg() {
        return postimg;
    }

    public void setPostimg(String postimg) {
        this.postimg = postimg;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getPostTeamType() {
        return postTeamType;
    }

    public void setPostTeamType(String postTeamType) {
        this.postTeamType = postTeamType;
    }

    public String getPostMatchType() {
        return postMatchType;
    }

    public void setPostMatchType(String postMatchType) {
        this.postMatchType = postMatchType;
    }

    public String getPostLeagueType() {
        return postLeagueType;
    }

    public void setPostLeagueType(String postLeagueType) {
        this.postLeagueType = postLeagueType;
    }
}
