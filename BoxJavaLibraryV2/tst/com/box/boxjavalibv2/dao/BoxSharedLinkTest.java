package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxSharedLinkTest {

    @Test
    public void testParcelRoundTrip() throws IOException, BoxRestException {
        String sharedJson = FileUtils.readFileToString(new File("testdata/sharedlink.json"));
        BoxSharedLink original = (BoxSharedLink) TestUtils.getFromJSON(sharedJson, BoxSharedLink.class);
        TestParcel parcel = new TestParcel();
        original.writeToParcel(parcel, 0);
        BoxSharedLink fromParcel = new BoxSharedLink(parcel);

        Assert.assertEquals("https://www.box.com/s/rh935iit6ewrmw0unyul", fromParcel.getUrl());
        Assert.assertEquals("https://www.box.com/shared/static/rh935iit6ewrmw0unyul.jpeg", fromParcel.getDownloadUrl());
        Assert.assertEquals(null, fromParcel.getVanityUrl());
        Assert.assertEquals(false, fromParcel.isPasswordEnabled());
        Assert.assertEquals(null, fromParcel.getUnsharedAt());
        Assert.assertEquals(0, fromParcel.getDownloadCount().intValue());
        Assert.assertEquals(0, fromParcel.getPreviewCount().intValue());
        Assert.assertEquals("open", fromParcel.getAccess());
        Assert.assertEquals(true, fromParcel.getPermissions().isCan_download());
        Assert.assertEquals(true, fromParcel.getPermissions().canPreview());
        Assert.assertEquals("testeffectiveaccess", fromParcel.getEffectiveAccess());
    }
}
