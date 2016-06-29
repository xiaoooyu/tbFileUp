package com.xiaoooyu.model;

/**
 * Created by xiaoooyu on 6/8/16.
 */
public class Collection {
    String _parentId;
    String _projectId;
    String visible = "members";
    Work[] works;

    public String get_parentId() {
        return _parentId;
    }

    public void set_parentId(String _parentId) {
        this._parentId = _parentId;
    }

    public String get_projectId() {
        return _projectId;
    }

    public void set_projectId(String _projectId) {
        this._projectId = _projectId;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public Work[] getWorks() {
        return works;
    }

    public void setWorks(Work[] works) {
        this.works = works;
    }
}
