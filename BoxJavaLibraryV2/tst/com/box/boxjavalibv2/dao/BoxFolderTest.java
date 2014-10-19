package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.exceptions.BoxJSONException;
import com.box.boxjavalibv2.jsonparsing.BoxJSONParser;
import com.box.boxjavalibv2.jsonparsing.BoxResourceHub;
import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxFolderTest {

    @Test
    public void testParcelRoundTrip() throws BoxRestException, IOException, BoxJSONException {


        String folderJson = FileUtils.readFileToString(new File("testdata/folder.json"));
        BoxFolder folder = (BoxFolder) TestUtils.getFromJSON(folderJson, BoxFolder.class);

        TestParcel parcel = new TestParcel();
        folder.writeToParcel(parcel, 0);
        BoxFolder fromParcel = new BoxFolder(parcel);


        Assert.assertEquals("folder", fromParcel.getType());
        Assert.assertEquals("11446498", fromParcel.getId());
        Assert.assertEquals("1", fromParcel.getSequenceId());
        Assert.assertEquals("1", fromParcel.getEtag());
        Assert.assertEquals("Pictures", fromParcel.getName());
        Assert.assertEquals("2012-12-12T10:53:43-08:00", fromParcel.getCreatedAt());
        Assert.assertEquals("2012-12-12T11:15:04-08:00", fromParcel.getModifiedAt());
        Assert.assertEquals("Some pictures I took", fromParcel.getDescription());
        Assert.assertEquals(629644, fromParcel.getSize().doubleValue(), 0);

        Assert.assertEquals(1, fromParcel.getPathCollection().getTotalCount().intValue());
        Assert.assertEquals("folder",fromParcel.getPathCollection().getEntries().get(0).getType());
        Assert.assertEquals("0",fromParcel.getPathCollection().getEntries().get(0).getId());
        Assert.assertEquals(null,((BoxItem)fromParcel.getPathCollection().getEntries().get(0)).getSequenceId());
        Assert.assertEquals(null,((BoxItem) fromParcel.getPathCollection().getEntries().get(0)).getEtag());
        Assert.assertEquals("All Files", ((BoxItem) fromParcel.getPathCollection().getEntries().get(0)).getName());

        Assert.assertEquals("user",fromParcel.getCreatedBy().getType());
        Assert.assertEquals("17738362",fromParcel.getCreatedBy().getId());
        Assert.assertEquals("sean rose",fromParcel.getCreatedBy().getName());
        Assert.assertEquals("sean@box.com",fromParcel.getCreatedBy().getLogin());

        Assert.assertEquals("user",fromParcel.getModifiedBy().getType());
        Assert.assertEquals("17738362",fromParcel.getModifiedBy().getId());
        Assert.assertEquals("sean rose",fromParcel.getModifiedBy().getName());
        Assert.assertEquals("sean@box.com",fromParcel.getModifiedBy().getLogin());

        Assert.assertEquals("user",fromParcel.getOwnedBy().getType());
        Assert.assertEquals("17738362",fromParcel.getOwnedBy().getId());
        Assert.assertEquals("sean rose",fromParcel.getOwnedBy().getName());
        Assert.assertEquals("sean@box.com",fromParcel.getOwnedBy().getLogin());

        Assert.assertEquals("https://www.box.com/s/vspke7y05sb214wjokpk",fromParcel.getSharedLink().getUrl());
        Assert.assertEquals(null,fromParcel.getSharedLink().getDownloadUrl());
        Assert.assertEquals(null,fromParcel.getSharedLink().getVanityUrl());
        Assert.assertEquals(false,fromParcel.getSharedLink().isPasswordEnabled());
        Assert.assertEquals(null,fromParcel.getSharedLink().getUnsharedAt());
        Assert.assertEquals(0,fromParcel.getSharedLink().getDownloadCount().intValue());
        Assert.assertEquals(0,fromParcel.getSharedLink().getPreviewCount().intValue());
        Assert.assertEquals("open",fromParcel.getSharedLink().getAccess());
        Assert.assertEquals(true,fromParcel.getSharedLink().getPermissions().isCan_download());
        Assert.assertEquals(true,fromParcel.getSharedLink().getPermissions().canPreview());

        Assert.assertEquals("open", fromParcel.getFolderUploadEmail().getAccess());
        Assert.assertEquals("upload.Picture.k13sdz1@u.box.com", fromParcel.getFolderUploadEmail().getEmail());

        Assert.assertEquals("folder", fromParcel.getParent().getType());
        Assert.assertEquals("0", fromParcel.getParent().getId());
        Assert.assertEquals(null, fromParcel.getParent().getSequenceId());
        Assert.assertEquals(null, fromParcel.getParent().getEtag());
        Assert.assertEquals("All Files", fromParcel.getParent().getName());


        Assert.assertEquals("active", fromParcel.getItemStatus());

        Assert.assertEquals(1, fromParcel.getItemCollection().getTotalCount().intValue());
        Assert.assertEquals("file", fromParcel.getItemCollection().getEntries().get(0).getType());
        Assert.assertEquals("5000948880", fromParcel.getItemCollection().getEntries().get(0).getId());
        Assert.assertEquals("3", ((BoxItem)fromParcel.getItemCollection().getEntries().get(0)).getSequenceId());
        Assert.assertEquals("3", ((BoxItem) fromParcel.getItemCollection().getEntries().get(0)).getEtag());
        Assert.assertEquals("134b65991ed521fcfe4724b7d814ab8ded5185dc", ((BoxFile)fromParcel.getItemCollection().getEntries().get(0)).getSha1());
        Assert.assertEquals("tigers.jpeg", ((BoxItem) fromParcel.getItemCollection().getEntries().get(0)).getName());

        Assert.assertEquals(2,fromParcel.getTags().length);
        Assert.assertEquals("approved",fromParcel.getTags()[0]);
        Assert.assertEquals("ready to publish",fromParcel.getTags()[1]);

        Assert.assertEquals(false, (boolean) fromParcel.hasCollaborations());
        Assert.assertEquals("not_synced", fromParcel.getSyncState());
    }
}
