package io.growing.sdk.java.dto;

import java.io.Serializable;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:20 PM
 */
public abstract class GIOMessage implements Serializable {

    private static final long serialVersionUID = 6360367979496722188L;

    protected String projectKey;

    public abstract void setProjectKey(String projectKey);

    public String getProjectKey() {
        return projectKey;
    }

    public boolean isIllegal() {
        return false;
    }
}