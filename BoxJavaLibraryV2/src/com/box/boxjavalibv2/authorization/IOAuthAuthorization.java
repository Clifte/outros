package com.box.boxjavalibv2.authorization;

import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.IBoxRequest;

/**
 * Created by liran on 26/05/2014.
 */
public interface IOAuthAuthorization {

    public void setOAuthData(BoxOAuthToken data);

    /**
     * Refresh the OAuth token.
     *
     * @throws com.box.boxjavalibv2.exceptions.AuthFatalFailureException
     *             exception
     */
    public void refresh() throws AuthFatalFailureException;

    /**
     * Initialize this auth. This need to be called before making an API request using this auth.
     */
    public void initOAuthForRequest();

    public void setAuth(final IBoxRequest request) throws BoxRestException, AuthFatalFailureException;
}
