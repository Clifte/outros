package com.box.boxjavalibv2.dao;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BoxWebLink extends BoxItem {

    public static final String FIELD_URL = "url";

    /**
     * Constructor.
     */
    public BoxWebLink() {
        setType(BoxResourceType.WEB_LINK.toString());
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxWebLink(BoxWebLink obj) {
        super(obj);
    }

    // Hide the permission from web link due to a backend issue. This method can
    // be removed when backend supports permissions in weblink.
    @Override
    @JsonIgnore
    public BoxItemPermissions getPermissions() {
        return null;
    }

    // Hide the permission from web link due to a backend issue. This method can
    // be removed when backend supports permissions in weblink.
    @JsonIgnore
    private void setPermissions(BoxItemPermissions permissions) {
        put(FIELD_PERMISSIONS, permissions);
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxWebLink(Map<String, Object> map) {
        super(map);
    }

    @Override
    @JsonProperty("size")
    public Double getSize() {
        Double size = super.getSize();
        return size == null ? 0d : size;
    }

    /**
     * Get url of the weblink.
     * 
     * @return url
     */
    @JsonProperty(FIELD_URL)
    public String getUrl() {
        return (String) getValue(FIELD_URL);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}
     * 
     * @param url
     *            url
     */
    @JsonProperty(FIELD_URL)
    private void setUrl(String url) {
        put(FIELD_URL, url);
    }

    public BoxWebLink(IBoxParcelWrapper in) {
        super(in);
    }
}
