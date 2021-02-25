package com.seraphic.lightapp.home_module.models;

import java.util.List;

public class SessionListPojo {
    List<SessionCategories> categories;

    public List<SessionCategories> getCategories() {
        return categories;
    }

    public void setCategories(List<SessionCategories> categories) {
        this.categories = categories;
    }

    public SessionGetter getDailySession() {
        return dailySession;
    }

    public void setDailySession(SessionGetter dailySession) {
        this.dailySession = dailySession;
    }

    SessionGetter dailySession;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    boolean success;




}
