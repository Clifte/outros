package com.box.boxjavalibv2.resourcemanagers;

import com.box.boxjavalibv2.dao.BoxCollection;
import com.box.boxjavalibv2.dao.BoxItemCollection;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.boxjavalibv2.requests.requestobjects.BoxPagingRequestObject;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.BoxDefaultRequestObject;

public interface IBoxCollectionsManager extends IBoxResourceManager {

    /**
     * Get a collection.
     * 
     * @param collectionId
     *            id of the collection
     * @param requestObject
     *            object that goes into request.
     * @return collection (Errors may occur if the IDs are invalid or if the user does not have permissions to see the collection.)
     * @throws BoxRestException
     *             See {@link com.box.restclientv2.exceptions.BoxRestException} for more info.
     * @throws BoxServerException
     *             See {@link com.box.boxjavalibv2.exceptions.BoxServerException} for more info.
     * @throws AuthFatalFailureException
     *             See {@link com.box.boxjavalibv2.exceptions.AuthFatalFailureException} for more info.
     */
    public BoxItemCollection getCollection(String collectionId, BoxDefaultRequestObject requestObject) throws BoxRestException, BoxServerException,
        AuthFatalFailureException;

    /**
     * Get all collections.
     * 
     * @param requestObject
     *            object that goes into request.
     * @return collections
     * @throws BoxRestException
     *             See {@link com.box.restclientv2.exceptions.BoxRestException} for more info.
     * @throws BoxServerException
     *             See {@link com.box.boxjavalibv2.exceptions.BoxServerException} for more info.
     * @throws AuthFatalFailureException
     *             See {@link com.box.boxjavalibv2.exceptions.AuthFatalFailureException} for more info.
     */
    public BoxCollection getAllCollections(BoxPagingRequestObject requestObject) throws BoxRestException, BoxServerException, AuthFatalFailureException;

    /**
     * Get all items in a given collection.
     * 
     * @param collectionId
     *            id of the collection
     * @param requestObject
     *            object that goes into request.
     * @return collection (Errors may occur if the IDs are invalid or if the user does not have permissions to see the collection.)
     * @throws BoxRestException
     *             See {@link com.box.restclientv2.exceptions.BoxRestException} for more info.
     * @throws BoxServerException
     *             See {@link com.box.boxjavalibv2.exceptions.BoxServerException} for more info.
     * @throws AuthFatalFailureException
     *             See {@link com.box.boxjavalibv2.exceptions.AuthFatalFailureException} for more info.
     */
    public BoxCollection getCollectionItems(String collectionId, BoxPagingRequestObject requestObject) throws BoxRestException, BoxServerException,
        AuthFatalFailureException;
}
