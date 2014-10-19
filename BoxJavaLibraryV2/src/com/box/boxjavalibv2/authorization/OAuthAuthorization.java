package com.box.boxjavalibv2.authorization;

import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.restclientv2.authorization.DefaultRequestAuth;
import com.box.restclientv2.exceptions.BoxRestException;
import com.box.restclientv2.requestsbase.IBoxRequest;

/**
 * This is authorization class for API requests using OAuth.
 */
public class OAuthAuthorization extends DefaultRequestAuth implements IOAuthAuthorization {

    private static final String BEARER = "Bearer";
    private final OAuthDataController mOAuth;

    public OAuthAuthorization(final OAuthDataController oAuth) {
        this.mOAuth = oAuth;
    }

    @Override
    public void setOAuthData(BoxOAuthToken data) {
        mOAuth.setOAuthData(data);
        mOAuth.initialize();
    }

    /**
     * Refresh the OAuth token.
     * 
     * @throws AuthFatalFailureException
     *             exception
     */
    @Override
    public void refresh() throws AuthFatalFailureException {
        mOAuth.refresh();
    }

    /**
     * Initialize this auth. This need to be called before making an API request using this auth.
     */
    @Override
    public void initOAuthForRequest() {
        mOAuth.initialize();
    }

    @Override
    public void setAuth(final IBoxRequest request) throws BoxRestException, AuthFatalFailureException {
        super.setAuth(request);

        request.addHeader(AUTH_HEADER_NAME, getAuthString());
    }

    /**
     * Get auth string to be put into request header.
     * 
     * @return string
     * @throws AuthFatalFailureException
     */
    private String getAuthString() throws AuthFatalFailureException {
        if (mOAuth != null) {
            BoxOAuthToken data = mOAuth.getAuthData();
            if (data != null) {
                return BEARER + " " + data.getAccessToken();
            }
        }
        return "";

    }
}
