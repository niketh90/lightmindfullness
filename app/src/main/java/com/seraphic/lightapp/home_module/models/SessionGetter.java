package com.seraphic.lightapp.home_module.models;

import java.io.Serializable;

public class SessionGetter  implements Serializable {
    public String _id,sessionName,sessionDescription,sessionUrl,sessionThumbNail,modified_at,created_at,ratingMessage;
    public int sessionType,sessionTime;
    public SessionAuthor sessionAuthor;
 }
