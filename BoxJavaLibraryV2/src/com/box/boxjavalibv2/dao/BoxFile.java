package com.box.boxjavalibv2.dao;

import java.util.Date;
import java.util.Map;

import com.box.boxjavalibv2.utils.ISO8601DateParser;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Box File object
 */
public class BoxFile extends BoxItem {

    public final static String FIELD_SHA1 = "sha1";
    public final static String FIELD_VERSION_NUMBER = "version_number";
    public final static String FIELD_COMMENT_COUNT = "comment_count";
    public final static String FIELD_CONTENT_CREATED_AT = "content_created_at";
    public final static String FIELD_CONTENT_MODIFIED_AT = "content_modified_at";
    public final static String FIELD_TRASHED_AT = "trashed_at";
    public final static String FIELD_PURGED_AT = "purged_at";
    public final static String FIELD_LOCK = "lock";
    public final static String FIELD_EXTENSION = "extension";

    /**
     * Constructor.
     */
    public BoxFile() {
        setType(BoxResourceType.FILE.toString());
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxFile(BoxFile obj) {
        super(obj);
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxFile(Map<String, Object> map) {
        super(map);
    }

    /**
     * Get sha1 of the file.
     * 
     * @return sha1 of the file.
     */
    @JsonProperty(FIELD_SHA1)
    public String getSha1() {
        return (String) getValue(FIELD_SHA1);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}
     * 
     * @param sha1
     *            sha1
     */
    @JsonProperty(FIELD_SHA1)
    private void setSha1(String sha1) {
        put(FIELD_SHA1, sha1);
    }

    @JsonProperty(FIELD_CONTENT_CREATED_AT)
    public String getContentCreatedAt() {
        return (String) getValue(FIELD_CONTENT_CREATED_AT);
    }

    /**
     * Return the date content created on local machine, if this date was not provided when uploading the file, this will be the time file was uploaded.
     * 
     * @return
     */
    public Date dateContentCreatedAt() {
        return ISO8601DateParser.parseSilently(getContentCreatedAt());
    }

    @JsonProperty(FIELD_CONTENT_CREATED_AT)
    private void setContentCreatedAt(String createdAt) {
        put(FIELD_CONTENT_CREATED_AT, createdAt);
    }

    @JsonProperty(FIELD_CONTENT_MODIFIED_AT)
    public String getContentModifiedAt() {
        return (String) getValue(FIELD_CONTENT_MODIFIED_AT);
    }

    /**
     * Return the date content last modified on local machine, if this date was not provided when uploading the file, this will be the time file was uploaded.
     * 
     * @return
     */
    public Date dateContentModifieddAt() {
        return ISO8601DateParser.parseSilently(getContentModifiedAt());
    }

    @JsonProperty(FIELD_CONTENT_MODIFIED_AT)
    private void setContentModifiedAt(String modifiedAt) {
        put(FIELD_CONTENT_MODIFIED_AT, modifiedAt);
    }

    /**
     * Get version number of the file.
     * 
     * @return version number of the file.
     */
    @JsonProperty(FIELD_VERSION_NUMBER)
    public String getVersionNumber() {
        return (String) getValue(FIELD_VERSION_NUMBER);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}
     * 
     * @param versionNumber
     *            version number
     */
    @JsonProperty(FIELD_VERSION_NUMBER)
    private void setVersionNumber(String versionNumber) {
        put(FIELD_VERSION_NUMBER, versionNumber);
    }

    /**
     * Get comment count of the file.
     * 
     * @return comment count of the file.
     */
    @JsonProperty(FIELD_COMMENT_COUNT)
    public Integer getCommentCount() {
        return (Integer) getValue(FIELD_COMMENT_COUNT);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}
     * 
     * @param commentCount
     *            comment count
     */
    @JsonProperty(FIELD_COMMENT_COUNT)
    private void setCommentCount(Integer commentCount) {
        put(FIELD_COMMENT_COUNT, commentCount);
    }

    @JsonProperty(FIELD_TRASHED_AT)
    public String getTrashedAt() {
        return (String) getValue(FIELD_TRASHED_AT);
    }

    @JsonProperty(FIELD_TRASHED_AT)
    private void setTrashedAt(String trashedAt) {
        put(FIELD_TRASHED_AT, trashedAt);
    }

    /**
     * Return the date file was last moved to the trash.
     * 
     * @return the date file was last moved to the trash.
     */
    public Date dateTrashedAt() {
        return ISO8601DateParser.parseSilently(getTrashedAt());
    }

    @JsonProperty(FIELD_PURGED_AT)
    public String getPurgedAt() {
        return (String) getValue(FIELD_PURGED_AT);
    }

    @JsonProperty(FIELD_PURGED_AT)
    private void setPurgedAt(String trashedAt) {
        put(FIELD_PURGED_AT, trashedAt);
    }

    /**
     * Return the date this file will be permanently deleted.
     * 
     * @return the date this file will be permanently deleted.
     */
    public Date datePurgedAt() {
        return ISO8601DateParser.parseSilently(getPurgedAt());
    }

    /**
     * Get lock on this file.
     * 
     * @return lock on the file
     */
    @JsonProperty(FIELD_LOCK)
    public BoxLock getLock() {
        return (BoxLock) getValue(FIELD_LOCK);
    }

    /**
     * Set the lock.
     * 
     * @param lock
     *            lock
     */
    @JsonProperty(FIELD_LOCK)
    protected void setLock(BoxLock lock) {
        put(FIELD_LOCK, lock);
    }

    /**
     * Get extension of the file.
     * 
     * @return extension of the file.
     */
    @JsonProperty(FIELD_EXTENSION)
    public String getExtension() {
        return (String) getValue(FIELD_EXTENSION);
    }

    /**
     * Sett extension.
     * 
     * @param extension
     *            extension
     */
    @JsonProperty(FIELD_EXTENSION)
    private void setExtension(String extension) {
        put(FIELD_EXTENSION, extension);
    }

    public BoxFile(IBoxParcelWrapper in) {
        super(in);
    }

}
