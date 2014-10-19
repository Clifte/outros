package com.box.boxjavalibv2.resourcemanagers;

import com.box.boxjavalibv2.IBoxConfig;
import com.box.boxjavalibv2.dao.BoxCollection;
import com.box.boxjavalibv2.dao.BoxItemCollection;
import com.box.boxjavalibv2.dao.BoxResourceType;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.jsonparsing.IBoxJSONParser;
import com.box.boxjavalibv2.jsonparsing.IBoxResourceHub;
import com.box.boxjavalibv2.requests.GetAllCollectionsRequest;
import com.box.boxjavalibv2.requests.GetCollectionItemsRequest;
import com.box.boxjavalibv2.requests.GetCollectionRequest;
import com.box.boxjavalibv2.requests.requestobjects.BoxPagingRequestObject;
import com.box.restclientv2.IBoxRESTClient;
import com.box.restclientv2.authorization.IBoxRequestAuth;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.BoxDefaultRequestObject;

/**
 * Use this class to execute requests <b>synchronously</b> against the Box REST API(V2), collections endpints. Full details about the Box API can be found at <a
 * href="http://developers.box.com/docs">http://developers.box.com/docs</a> . You must have an OpenBox application with a valid API key to use the Box API. All
 * methods in this class are executed in the invoking thread, and therefore are NOT safe to execute in the UI thread of your application. You should only use
 * this class if you already have worker threads or AsyncTasks that you want to incorporate the Box API into.
 */
public final class BoxCollectionsManagerImpl extends AbstractBoxResourceManager implements IBoxCollectionsManager {

    /**
     * Constructor.
     * 
     * @param config
     *            Config
     * @param resourceHub
     *            IResourceHub
     * @param parser
     *            json parser
     * @param auth
     *            auth for api calls
     * @param restClient
     *            REST client to make api calls.
     */
    public BoxCollectionsManagerImpl(IBoxConfig config, final IBoxResourceHub resourceHub, final IBoxJSONParser parser, final IBoxRequestAuth auth,
        final IBoxRESTClient restClient) {
        super(config, resourceHub, parser, auth, restClient);
    }

    @Override
    public BoxItemCollection getCollection(final String collectionId, BoxDefaultRequestObject requestObject) throws BoxRestException, BoxServerException,
        AuthFatalFailureException {
        GetCollectionRequest request = new GetCollectionRequest(getConfig(), getJSONParser(), collectionId, requestObject);

        return (BoxItemCollection) getResponseAndParseAndTryCast(request, BoxResourceType.COLLECTION, getJSONParser());
    }

    @Override
    public BoxCollection getAllCollections(final BoxPagingRequestObject requestObject) throws BoxRestException, BoxServerException, AuthFatalFailureException {
        GetAllCollectionsRequest request = new GetAllCollectionsRequest(getConfig(), getJSONParser(), requestObject);

        return (BoxCollection) getResponseAndParseAndTryCast(request, BoxResourceType.COLLECTIONS, getJSONParser());
    }

    @Override
    public BoxCollection getCollectionItems(String collectionId, BoxPagingRequestObject requestObject) throws BoxRestException, BoxServerException,
        AuthFatalFailureException {
        GetCollectionItemsRequest request = new GetCollectionItemsRequest(getConfig(), getJSONParser(), collectionId, requestObject);

        return (BoxCollection) getResponseAndParseAndTryCast(request, BoxResourceType.ITEMS, getJSONParser());
    }
}
