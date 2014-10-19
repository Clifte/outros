package com.box.boxjavalibv2.requests;

import junit.framework.Assert;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.box.boxjavalibv2.dao.BoxResourceType;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.testutils.TestUtils;
import com.box.restclientv2.RestMethod;
import com.box.restclientv2.exceptions.BoxRestException;

public class GetUserMembershipsRequestTest extends RequestTestBase{
	private static String id = "12344";
	
	@Test
    public void testUri() {
        Assert.assertEquals("/users/"+ id + "/memberships", GetUserMembershipsRequest.getUri(id));
    }

    @Test
    public void testMembershipRequestIsWellFormed() throws BoxRestException, AuthFatalFailureException {

    	 GetUserMembershipsRequest request = new GetUserMembershipsRequest(CONFIG,JSON_PARSER, id, null);
         testRequestIsWellFormed(request, TestUtils.getConfig().getApiUrlAuthority(),
             TestUtils.getConfig().getApiUrlPath().concat(GetUserMembershipsRequest.getUri(id)), HttpStatus.SC_OK, RestMethod.GET);

     
    }


}
