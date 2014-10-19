package com.box.boxjavalibv2.requests;

import com.box.boxjavalibv2.IBoxConfig;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;
import com.box.boxjavalibv2.requests.requestobjects.BoxPagingRequestObject;
import com.box.restclientv2.RestMethod;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.DefaultBoxRequest;

/**
 * Request to get the items inside a folder. These items can be files, sub-folders, weblinks, and etc.
 */
public class GetAllCollectionsRequest extends DefaultBoxRequest {

    public static final String URI = "/collections";

    /**
     * Constructor.
     * 
     * @param config
     *            config
     * @param parser
     *            json parser
     * @param requestObject
     *            request object
     * @throws BoxRestException
     *             exception
     */
    public GetAllCollectionsRequest(final IBoxConfig config, final IBoxJSONParser parser, BoxPagingRequestObject requestObject) throws BoxRestException {
        super(config, parser, URI, RestMethod.GET, requestObject);
    }
}
