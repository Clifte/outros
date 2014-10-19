package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxFileTest {

    @Test
    public void testParcelRoundTrip() throws BoxRestException, IOException {
        String fileJson = FileUtils.readFileToString(new File("testdata/file.json"));
        BoxFile filev2 = (BoxFile) TestUtils.getFromJSON(fileJson, BoxFile.class);

        TestParcel parcel = new TestParcel();
        filev2.writeToParcel(parcel, 0);
        BoxFile fromParcel = new BoxFile(parcel);

        Assert.assertEquals("file", fromParcel.getType());
        Assert.assertEquals("5000948880", fromParcel.getId());
        Assert.assertEquals("3", fromParcel.getSequenceId());
        Assert.assertEquals("3", fromParcel.getEtag());
        Assert.assertEquals("134b65991ed521fcfe4724b7d814ab8ded5185dc", fromParcel.getSha1());
        Assert.assertEquals("tigers.jpeg", fromParcel.getName());
        Assert.assertEquals("a picture of tigers", fromParcel.getDescription());
        Assert.assertEquals(629644, fromParcel.getSize().doubleValue(),0);

        // test that path collection has complete data
        Assert.assertEquals(2,(long)fromParcel.getPathCollection().getTotalCount().intValue());
        Assert.assertEquals("folder",fromParcel.getPathCollection().getEntries().get(0).getType());
        Assert.assertEquals("0",fromParcel.getPathCollection().getEntries().get(0).getId());
        Assert.assertEquals(null,((BoxItem)fromParcel.getPathCollection().getEntries().get(0)).getSequenceId());
        Assert.assertEquals(null,((BoxItem) fromParcel.getPathCollection().getEntries().get(0)).getEtag());
        Assert.assertEquals("All Files", ((BoxItem) fromParcel.getPathCollection().getEntries().get(0)).getName());

        Assert.assertEquals("folder",fromParcel.getPathCollection().getEntries().get(1).getType());
        Assert.assertEquals("11446498",fromParcel.getPathCollection().getEntries().get(1).getId());
        Assert.assertEquals("1",((BoxItem)fromParcel.getPathCollection().getEntries().get(1)).getSequenceId());
        Assert.assertEquals("1",((BoxItem) fromParcel.getPathCollection().getEntries().get(1)).getEtag());
        Assert.assertEquals("Pictures",((BoxItem) fromParcel.getPathCollection().getEntries().get(1)).getName());

        Assert.assertEquals("2012-12-12T10:55:30-08:00",fromParcel.getCreatedAt());
        Assert.assertEquals("2012-12-12T11:04:26-08:00",fromParcel.getModifiedAt());
        Assert.assertEquals(null,fromParcel.getTrashedAt());
        Assert.assertEquals(null,fromParcel.getPurgedAt());
        Assert.assertEquals("2013-02-04T16:57:52-08:00",fromParcel.getContentCreatedAt());
        Assert.assertEquals("2013-02-04T16:57:52-08:00",fromParcel.getContentModifiedAt());

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


        Assert.assertEquals("https://www.box.com/s/rh935iit6ewrmw0unyul",fromParcel.getSharedLink().getUrl());
        Assert.assertEquals("https://www.box.com/shared/static/rh935iit6ewrmw0unyul.jpeg",fromParcel.getSharedLink().getDownloadUrl());
        Assert.assertEquals(null,fromParcel.getSharedLink().getVanityUrl());
        Assert.assertEquals(false,fromParcel.getSharedLink().isPasswordEnabled());
        Assert.assertEquals(null,fromParcel.getSharedLink().getUnsharedAt());
        Assert.assertEquals(0,fromParcel.getSharedLink().getDownloadCount().intValue());
        Assert.assertEquals(0,fromParcel.getSharedLink().getPreviewCount().intValue());
        Assert.assertEquals("open",fromParcel.getSharedLink().getAccess());
        Assert.assertEquals(true,fromParcel.getSharedLink().getPermissions().isCan_download());
        Assert.assertEquals(true,fromParcel.getSharedLink().getPermissions().canPreview());

        Assert.assertEquals("folder",fromParcel.getParent().getType());
        Assert.assertEquals("11446498",fromParcel.getParent().getId());
        Assert.assertEquals("1",fromParcel.getParent().getSequenceId());
        Assert.assertEquals("1",fromParcel.getParent().getEtag());
        Assert.assertEquals("Pictures",fromParcel.getParent().getName());

        Assert.assertEquals("active",fromParcel.getItemStatus());
        Assert.assertEquals(2,fromParcel.getTags().length);
        Assert.assertEquals("cropped",fromParcel.getTags()[0]);
        Assert.assertEquals("color corrected",fromParcel.getTags()[1]);

        // more detailed test in BoxLockTest
        Assert.assertEquals("lock",fromParcel.getLock().getType());
        Assert.assertEquals("112429",fromParcel.getLock().getId());

        Assert.assertEquals("2", fromParcel.getVersionNumber());
        Assert.assertEquals(2, (int) fromParcel.getCommentCount());
        Assert.assertEquals("jpeg", fromParcel.getExtension());


    }
}
