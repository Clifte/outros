package com.box.boxjavalibv2.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;


// CHECKSTYLE:OFF
/**
 * Permission for shared links.
 * 
 */
public class BoxSharedLinkPermissions extends BoxObject {

    public static final String FIELD_CAN_DOWNLOAD = "can_download";
    public static final String FIELD_CAN_PREVIEW = "can_preview";

    /**
     * Default constructor.
     */
    public BoxSharedLinkPermissions() {
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxSharedLinkPermissions(BoxSharedLinkPermissions obj) {
        super(obj);
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxSharedLinkPermissions(Map<String, Object> map) {
        super(map);
    }

    /**
     * Constructor.
     * 
     * @param canDownload
     *            can be downloaded
     */
    public BoxSharedLinkPermissions(final boolean canDownload) {
        setCan_download(canDownload);
    }

    /**
     * whether can_download is true.
     * 
     * @return can_download
     */
    public Boolean isCan_download() {
        return (Boolean) getValue(FIELD_CAN_DOWNLOAD);
    }

    /**
     * Setter.
     * 
     * @param canDownload
     */
    protected void setCan_download(final Boolean canDownload) {
        put(FIELD_CAN_DOWNLOAD, canDownload);
    }

    /**
     * whether can_preview is true.
     *
     * @return can_preview
     */
    @JsonProperty(FIELD_CAN_PREVIEW)
    public Boolean canPreview() {
        return (Boolean) getValue(FIELD_CAN_PREVIEW);
    }

    /**
     * Setter.
     *
     * @param canPreview
     */
    @JsonProperty(FIELD_CAN_PREVIEW)
    private void setCanPreview(final Boolean canPreview) {
        put(FIELD_CAN_PREVIEW, canPreview);
    }

    protected BoxSharedLinkPermissions(IBoxParcelWrapper parcel) {
        super(parcel);
    }
}
