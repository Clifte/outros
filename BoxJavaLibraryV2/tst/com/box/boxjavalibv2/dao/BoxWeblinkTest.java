package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxWeblinkTest {

    @Test
    public void testParcelRoundTrip() throws BoxRestException, IOException {
        String json = FileUtils.readFileToString(new File("testdata/weblink.json"));
        BoxWebLink weblink = (BoxWebLink) TestUtils.getFromJSON(json, BoxWebLink.class);

        TestParcel parcel = new TestParcel();
        weblink.writeToParcel(parcel, 0);
        BoxWebLink fromParcel = new BoxWebLink(parcel);

        Assert.assertEquals("web_link", fromParcel.getType());
        Assert.assertEquals("testweblinkid", fromParcel.getId());
        Assert.assertEquals("testetag", fromParcel.getEtag());
    }

    @Test
    public void makeSureWeblinkSizeIsNotNullButZero() throws IOException, BoxRestException {
        String json = FileUtils.readFileToString(new File("testdata/weblink.json"));
        BoxWebLink weblink = (BoxWebLink) TestUtils.getFromJSON(json, BoxWebLink.class);
        Assert.assertEquals(0d, weblink.getSize(), 0.001);
    }
}
