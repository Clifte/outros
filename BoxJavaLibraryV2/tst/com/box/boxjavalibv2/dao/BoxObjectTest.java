package com.box.boxjavalibv2.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.exceptions.BoxRestException;

public class BoxObjectTest {

    /**
     * Test when parsing box object, if type cannot be recognized, we fall back to the specified base object.
     */
    @Test
    public void testFallBack() throws IOException, BoxRestException {
        String json = FileUtils.readFileToString(new File("testdata/wrongtype.json"));
        BoxTypedObject obj = (BoxTypedObject) TestUtils.getFromJSON(json, BoxTypedObject.class);
        Assert.assertEquals(BoxTypedObject.class, obj.getClass());

        BoxItem obj1 = (BoxItem) TestUtils.getFromJSON(json, BoxItem.class);
        Assert.assertEquals(BoxItem.class, obj1.getClass());

        // Make sure can also parse for string without type.
        json = json.replace("\"type\":\"notype\",", "");
        obj1 = (BoxItem) TestUtils.getFromJSON(json, BoxItem.class);
        Assert.assertEquals(BoxItem.class, obj1.getClass());
    }

    /**
     * Verify that unknown types in json are handled according to canBeHandledAsUnknown() method in BoxObject.
     */
    @Test
    public void testHandleUnknownTypes() throws IOException, BoxRestException {
        String json = FileUtils.readFileToString(new File("testdata/filewithunknowntype.json"));
        BoxFile obj = (BoxFile) TestUtils.getFromJSON(json, BoxFile.class);
        Assert.assertEquals(BoxFile.class, obj.getClass());

        String unknownStr = (String) obj.getExtraData("unknownstring");
        Assert.assertEquals("unknown string", unknownStr);

        @SuppressWarnings("unchecked")
        List<Integer> unknownIntArray = (List<Integer>) obj.getExtraData("intarray");
        Assert.assertEquals(1, unknownIntArray.get(0).intValue());
        Assert.assertEquals(2, unknownIntArray.get(1).intValue());

        @SuppressWarnings("unchecked")
        List<String> unknownStrArray = (List<String>) obj.getExtraData("stringarray");
        Assert.assertEquals(2, unknownStrArray.size());
        Assert.assertEquals("string1", unknownStrArray.get(0));
        Assert.assertEquals("string2", unknownStrArray.get(1));

        Assert.assertFalse(obj.contains("unparsabletype"));
    }
}
