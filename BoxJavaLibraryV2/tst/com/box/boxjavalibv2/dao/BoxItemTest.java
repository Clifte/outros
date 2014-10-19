package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxItemTest {

    @Test
    public void testParcelRoundTrip() throws BoxRestException, IOException {
        String fileJson = FileUtils.readFileToString(new File("testdata/file.json"));
        BoxItem originalItem = (BoxItem) TestUtils.getFromJSON(fileJson, BoxItem.class);
        TestParcel parcel = new TestParcel();
        originalItem.writeToParcel(parcel, 0);
        BoxItem item = new BoxFile(parcel);

        Assert.assertTrue(item instanceof BoxFile);
        Assert.assertEquals("file", item.getType());
        Assert.assertEquals("5000948880", item.getId());
        Assert.assertEquals("2012-12-12T10:55:30-08:00", item.getCreatedAt());
        Assert.assertEquals("2012-12-12T11:04:26-08:00", item.getModifiedAt());
        Assert.assertEquals("3", item.getEtag());
        Assert.assertEquals("3", item.getSequenceId());
        Assert.assertEquals("tigers.jpeg", item.getName());
        Assert.assertEquals("a picture of tigers", item.getDescription());
        Assert.assertEquals(629644, item.getSize().doubleValue(),0);
        Assert.assertEquals("active", item.getItemStatus());


        BoxItemPermissions perm = item.getPermissions();
        Assert.assertTrue(perm.canPreview());
        Assert.assertTrue(perm.canDownload());
        Assert.assertTrue(perm.canSetShareAccess());
        Assert.assertTrue(perm.canComment());
        Assert.assertTrue(perm.canDelete());
        Assert.assertTrue(perm.canShare());
        Assert.assertTrue((Boolean) perm.getExtraData("can_whatever"));
        Assert.assertFalse((Boolean) perm.getExtraData("cannot_whatever"));
    }
}
