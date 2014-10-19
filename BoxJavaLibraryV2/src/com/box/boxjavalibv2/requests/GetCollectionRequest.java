package com.box.boxjavalibv2.requests;

import com.box.boxjavalibv2.IBoxConfig;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;
import com.box.restclientv2.RestMethod;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.BoxDefaultRequestObject;
import com.box.restclientv2.requestsbase.DefaultBoxRequest;

/**
 * Request to get a collaboration. Can also request for collaborations of a certain status. Currently only
 * {@link com.box.boxjavalibv2.dao.BoxCollaboration#STATUS_PENDING} is allowed.
 */
public class GetCollectionRequest extends DefaultBoxRequest {

    public static final String URI = "/collections/%s";

    /**
     * Constructor.
     * 
     * @param config
     *            config
     * @param parser
     *            json parser
     * @param collectionId
     *            id of the collection
     * @param requestObject
     *            request object
     * @throws BoxRestException
     *             exception
     */
    public GetCollectionRequest(final IBoxConfig config, final IBoxJSONParser parser, String collectionId, final BoxDefaultRequestObject requestObject)
        throws BoxRestException {
        super(config, parser, getUri(collectionId), RestMethod.GET, requestObject);
    }

    /**
     * Get uri.
     * 
     * @param collectionId
     *            collection id
     * @return uri
     */
    public static String getUri(final String collectionId) {
        return String.format(URI, collectionId);
    }
}
