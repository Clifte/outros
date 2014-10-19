package com.box.boxjavalibv2.dao;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoxItemPermissions extends BoxObject {

    public static final String FIELD_CAN_DOWNLOAD = "can_download";
    public static final String FIELD_CAN_PREVIEW = "can_preview";
    public static final String FIELD_CAN_UPLOAD = "can_upload";
    public static final String FIELD_CAN_SHARE = "can_share";
    public static final String FIELD_CAN_RENAME = "can_rename";
    public static final String FIELD_CAN_DELETE = "can_delete";
    public static final String FIELD_CAN_SET_SHARE_ACCESS = "can_set_share_access";
    public static final String FIELD_CAN_INVITE_COLLABORATOR = "can_invite_collaborator";
    public static final String FIELD_CAN_COMMENT = "can_comment";

    public BoxItemPermissions() {
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxItemPermissions(BoxItemPermissions obj) {
        super(obj);
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxItemPermissions(Map<String, Object> map) {
        super(map);
    }

    public BoxItemPermissions(IBoxParcelWrapper in) {
        super(in);
    }

    @JsonProperty(FIELD_CAN_DOWNLOAD)
    public Boolean canDownload() {
        return (Boolean) getValue(FIELD_CAN_DOWNLOAD);
    }

    @JsonProperty(FIELD_CAN_DOWNLOAD)
    private void setCanDownload(Boolean canDownload) {
        put(FIELD_CAN_DOWNLOAD, canDownload);
    }
   
    @Deprecated
    public Boolean canPreivew() {
        return canPreview();
    }
    
    @JsonProperty(FIELD_CAN_PREVIEW)
    public Boolean canPreview() {
        return (Boolean) getValue(FIELD_CAN_PREVIEW);
    }

    @JsonProperty(FIELD_CAN_PREVIEW)
    private void setCanPreview(Boolean canPreview) {
        put(FIELD_CAN_PREVIEW, canPreview);
    }

    @JsonProperty(FIELD_CAN_UPLOAD)
    public Boolean canUpload() {
        return (Boolean) getValue(FIELD_CAN_UPLOAD);
    }

    @JsonProperty(FIELD_CAN_UPLOAD)
    private void setCanUpload(Boolean canUpload) {
        put(FIELD_CAN_UPLOAD, canUpload);
    }

    @JsonProperty(FIELD_CAN_SHARE)
    public Boolean canShare() {
        return (Boolean) getValue(FIELD_CAN_SHARE);
    }

    @JsonProperty(FIELD_CAN_SHARE)
    private void setCanShare(Boolean canshare) {
        put(FIELD_CAN_SHARE, canshare);
    }

    @JsonProperty(FIELD_CAN_RENAME)
    public Boolean canRename() {
        return (Boolean) getValue(FIELD_CAN_RENAME);
    }

    @JsonProperty(FIELD_CAN_RENAME)
    private void setCanRename(Boolean canrename) {
        put(FIELD_CAN_RENAME, canrename);
    }

    @JsonProperty(FIELD_CAN_DELETE)
    public Boolean canDelete() {
        return (Boolean) getValue(FIELD_CAN_DELETE);
    }

    @JsonProperty(FIELD_CAN_DELETE)
    private void setCanDelete(Boolean canDelete) {
        put(FIELD_CAN_DELETE, canDelete);
    }

    @JsonProperty(FIELD_CAN_SET_SHARE_ACCESS)
    public Boolean canSetShareAccess() {
        return (Boolean) getValue(FIELD_CAN_SET_SHARE_ACCESS);
    }

    @JsonProperty(FIELD_CAN_SET_SHARE_ACCESS)
    private void setCanSetShareAccess(Boolean canSetShareAccess) {
        put(FIELD_CAN_SET_SHARE_ACCESS, canSetShareAccess);
    }

    @JsonProperty(FIELD_CAN_INVITE_COLLABORATOR)
    public Boolean canInviteCollaborator() {
        return (Boolean) getValue(FIELD_CAN_INVITE_COLLABORATOR);
    }

    @JsonProperty(FIELD_CAN_INVITE_COLLABORATOR)
    private void setCanInviteCollaborator(Boolean canInviteCollaborator) {
        put(FIELD_CAN_INVITE_COLLABORATOR, canInviteCollaborator);
    }

    @JsonProperty(FIELD_CAN_COMMENT)
    public Boolean canComment() {
        return (Boolean) getValue(FIELD_CAN_COMMENT);
    }

    @JsonProperty(FIELD_CAN_COMMENT)
    private void setCanComment(Boolean canComment) {
        put(FIELD_CAN_COMMENT, canComment);
    }
}
