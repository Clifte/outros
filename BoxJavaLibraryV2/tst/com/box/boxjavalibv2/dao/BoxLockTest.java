package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxLockTest {

    @Test
    public void testParcelRoundTrip() throws BoxRestException, IOException {

        String lockJson = FileUtils.readFileToString(new File("testdata/lock.json"));

        BoxLock boxLock = (BoxLock) TestUtils.getFromJSON(lockJson, BoxLock.class);
        TestParcel parcel = new TestParcel();
        boxLock.writeToParcel(parcel, 0);
        boxLock = new BoxLock(parcel);

        Assert.assertEquals(boxLock.getType(), "lock");
        Assert.assertEquals(boxLock.getId(), "112429");
        Assert.assertEquals(boxLock.getCreatedAt(), "2013-12-04T10:28:36-08:00");
        Assert.assertEquals(boxLock.getExpiresAt(), "2012-12-12T10:55:30-08:00");
        Assert.assertEquals(boxLock.isDownloadPrevented(),false);

        Assert.assertEquals(boxLock.getCreatedBy().getType(), "user");
        Assert.assertEquals(boxLock.getCreatedBy().getId(), "18212074");
        Assert.assertEquals(boxLock.getCreatedBy().getName(), "Obi Wan");
        Assert.assertEquals(boxLock.getCreatedBy().getLogin(), "obiwan@box.com");


    }
}
