package io.growing.sdk.java.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : tong.wang
 * @version : 1.0.0
 * @since : 11/21/18 5:20 PM
 */
public abstract class GIOMessage implements Serializable {

    private static final long serialVersionUID = -5789315589035420840L;

    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public abstract Map<String, Object> getMapResult();
}