package com.box.boxjavalibv2.requests;

import com.box.boxjavalibv2.IBoxConfig;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;
import com.box.restclientv2.RestMethod;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.BoxDefaultRequestObject;
import com.box.restclientv2.requestsbase.DefaultBoxRequest;

public class GetUserMembershipsRequest extends DefaultBoxRequest {

    private static final String URI = "/users/%s/memberships";

    public GetUserMembershipsRequest(final IBoxConfig config, final IBoxJSONParser parser, final String userId, final BoxDefaultRequestObject requestObject)
        throws BoxRestException {
        super(config, parser, getUri(userId), RestMethod.GET, requestObject);
    }

    public static String getUri(final String id) {
        return String.format(URI, id);
    }
}
