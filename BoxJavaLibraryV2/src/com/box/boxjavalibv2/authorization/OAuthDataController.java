package com.box.boxjavalibv2.authorization;

import org.apache.http.HttpStatus;

import com.box.boxjavalibv2.BoxClient;
import com.box.boxjavalibv2.dao.BoxOAuthToken;
import com.box.boxjavalibv2.exceptions.AuthFatalFailureException;
import com.box.boxjavalibv2.exceptions.BoxServerException;
import com.box.restclientv2.exceptions.BoxRestException;

/**
 * This is the data controller for OAuth, it handles token auto refresh.
 */
public class OAuthDataController implements IAuthDataController {

    public static enum OAuthTokenState {
        PRE_CREATION, AVAILABLE, REFRESHING, FAIL,
    }

    /**
     * Time to wait for lock.
     */
    private static final int WAIT = 200;

    /**
     * Default timeout waiting for lock
     */
    private static final int WAIT_TIME_OUT = 60000;

    private final BoxClient mClient;
    private final String mClientId;
    private final String mClientSecret;
    private String mDeviceId = null;
    private String mDeviceName = null;
    private volatile BoxOAuthToken mOAuthToken;
    private volatile OAuthTokenState mTokenState = OAuthTokenState.PRE_CREATION;
    private boolean mAutoRefresh;
    private int mWaitTimeOut = WAIT_TIME_OUT;
    private Exception refreshFailException;
    private volatile boolean locked = false;

    private OAuthRefreshListener refreshListener;

    public OAuthDataController(BoxClient boxClient, final String clientId, final String clientSecret, final boolean autoRefresh) {
        this.mClient = boxClient;
        this.mClientId = clientId;
        this.mClientSecret = clientSecret;
        this.mAutoRefresh = autoRefresh;
    }

    /**
     * Makes OAuth auto refresh itself when token expires. Note if autorefresh fails, it's not going to try refresh again.
     * 
     * @param autoRefresh
     */
    public void setAutoRefreshOAuth(boolean autoRefresh) {
        mAutoRefresh = autoRefresh;
    }

    /**
     * Set the timeout for threads waiting for OAuth token refresh.
     * 
     * @param timeout
     */
    public void setWaitTimeOut(int timeout) {
        this.mWaitTimeOut = timeout;
    }

    /**
     * @return the mScheme
     */
    public String getScheme() {
        return mClient.getConfig().getOAuthUrlScheme();
    }

    /**
     * @return the mAuthority
     */
    public String getAuthority() {
        return mClient.getConfig().getOAuthUrlAuthority();
    }

    /**
     * @return the OAuth url path.
     */
    public String getUrlPath() {
        return mClient.getConfig().getOAuthWebUrlPath();
    }

    /**
     * @return the mClientId
     */
    public String getClientId() {
        return mClientId;
    }

    /**
     * @return the mClientSecret
     */
    public String getClientSecret() {
        return mClientSecret;
    }

    public void setOAuthData(BoxOAuthToken token) {
        try {
            waitForLock(true, WAIT);

            mOAuthToken = token;
            if (mOAuthToken != null) {
                internalSetTokenState(OAuthTokenState.AVAILABLE);
            } else {
                internalSetTokenState(OAuthTokenState.PRE_CREATION);
            }
        } finally {
            unlock();
        }
    }

    /**
     * Set device id. This is optional.
     * 
     * @param deviceId
     *            device id
     */
    public void setDeviceId(final String deviceId) {
        this.mDeviceId = deviceId;
    }

    /**
     * Set device name. Optional.
     * 
     * @param deviceName
     *            device name
     */
    public void setDeviceName(final String deviceName) {
        this.mDeviceName = deviceName;
    }

    /**
     * @return the TokenState
     */
    public OAuthTokenState getTokenState() {
        return mTokenState;
    }

    /**
     * Reset token state to PRE_CREATION so it will be ready to refresh again.
     */
    public void resetTokenState() {
        try {
            waitForLock(true, WAIT);

            internalSetTokenState(OAuthTokenState.PRE_CREATION);
        } finally {
            unlock();
        }
    }

    /**
     * Setter of mTokenState. There's no locking mechanisms involved and should not be made public.
     * 
     * @param tokenState
     *            the mTokenState to set
     */
    protected void internalSetTokenState(OAuthTokenState tokenState) {
        this.mTokenState = tokenState;
    }

    /**
     * @return the refreshFailException
     */
    public Exception getRefreshFailException() {
        return refreshFailException;
    }

    /**
     * @param refreshFailException
     *            the refreshFailException to set
     */
    public void setRefreshFail(Exception refreshFailException) {
        this.refreshFailException = refreshFailException;
        if (refreshFailException != null) {
            internalSetTokenState(OAuthTokenState.FAIL);
        }
    }

    /**
     * Initialize the controller.
     */
    public void initialize() {
        internalSetTokenState(OAuthTokenState.AVAILABLE);
        setRefreshFail(null);
        unlock();
    }

    /**
     * Get OAuthData, counting number of retries, in case of too many retries, throw. Note depending on the OAuth token state, there is no guarantee that the
     * OAuthData is valid. an example is that the token state is FAIL, which indicates the token is bad.
     * 
     * @return OAuthData
     * @throws AuthFatalFailureException
     */
    @Override
    public BoxOAuthToken getAuthData() throws AuthFatalFailureException {
        long num = 0;
        while (num * WAIT <= mWaitTimeOut) {
            if (getAndSetLock(false)) {
                return mOAuthToken;
            } else {
                doWait(WAIT);
                num++;
            }
        }
        throw new AuthFatalFailureException(getRefreshFailException());
    }

    /**
     * Get OAuthData, in case of OAuthTokenState indicating refresh needed, do refresh. Note this method may involve network operation so do not call on UI
     * thread.
     */
    public BoxOAuthToken guarranteedGetAuthData() throws AuthFatalFailureException {
        long num = 0;
        while (num * WAIT <= mWaitTimeOut) {
            if (getAndSetLock(false)) {
                if (getTokenState() == OAuthTokenState.PRE_CREATION) {
                    if (!mAutoRefresh) {
                        throw new AuthFatalFailureException(getRefreshFailException());
                    } else {
                        refresh();
                        return guarranteedGetAuthData();
                    }
                } else if (getTokenState() == OAuthTokenState.FAIL) {
                    throw new AuthFatalFailureException(getRefreshFailException());
                } else {
                    return mOAuthToken;
                }
            } else {
                doWait(WAIT);
                num++;
            }
        }
        throw new AuthFatalFailureException(getRefreshFailException());
    }

    /**
     * Refresh the OAuth.
     * 
     * @throws AuthFatalFailureException
     *             exception
     */
    @Override
    public void refresh() throws AuthFatalFailureException {
        if (!getAndSetLock(true)) {
            getAuthData();
        } else {
            try {
                if (getTokenState() == OAuthTokenState.FAIL || !mAutoRefresh) {
                    internalSetTokenState(OAuthTokenState.FAIL);
                    throw new AuthFatalFailureException(getRefreshFailException());
                } else {
                    doRefresh();
                }
            } finally {
                unlock();
            }
        }
    }

    @Deprecated
    /**
     * There is only one refresh listener, "add" doesn't make sense, use setOAuthRefreshListener instead.
     * @param listener
     */
    public void addOAuthRefreshListener(OAuthRefreshListener listener) {
        this.refreshListener = listener;
    }

    public void setOAuthRefreshListener(OAuthRefreshListener listener) {
        this.refreshListener = listener;
    }

    /**
     * Get the lock, optionally lock the lock after getting the lock.
     * 
     * @param doLock
     *            whether want to lock after getting the lock.
     * @return
     */
    synchronized final protected boolean getAndSetLock(boolean doLock) {
        boolean lockRetrieved = false;
        if (doLock) {
            if (locked) {
                lockRetrieved = false;
            } else {
                locked = true;
                lockRetrieved = true;
            }
        } else {
            lockRetrieved = !locked;
        }
        return lockRetrieved;
    }

    /**
     * Unlock the OAuth lock.
     */
    protected void unlock() {
        locked = false;
    }

    /**
     * Refresh the OAuth.
     * 
     * @throws AuthFatalFailureException
     *             exception
     */
    protected void doRefresh() throws AuthFatalFailureException {
        internalSetTokenState(OAuthTokenState.REFRESHING);

        if (mOAuthToken == null) {
            setRefreshFail(new BoxRestException("OAuthToken is null"));
            throw new AuthFatalFailureException(getRefreshFailException());
        }

        try {
            mOAuthToken = mClient.getOAuthManager().refreshOAuth(mOAuthToken.getRefreshToken(), mClientId, mClientSecret, mDeviceId, mDeviceName);
            internalSetTokenState(OAuthTokenState.AVAILABLE);
            setRefreshFail(null);
            if (refreshListener != null) {
                refreshListener.onRefresh(mOAuthToken);
            }
        } catch (BoxRestException e) {
            // A BoxRestException indicates a network error or a json parsing error. In this case, there is no reason to enter a failure state. Just throw an
            // exception and set the token state back to AVAILABLE so the app can retry if it wishes.
            internalSetTokenState(OAuthTokenState.AVAILABLE);
            throw new AuthFatalFailureException(e);
        } catch (BoxServerException e) {
            // A BoxServerException indicates an error from the server. This could be a 500, 403, 400, etc. The only case in which this is a permanent failure
            // is if we get a 400, which means the user's refresh token is invalid. In that case, we call setRefreshFail to enter a failure state until the user
            // re-logs-in. In the case of any other status, we set the token state back to AVAILABLE to allow the app to retry if it wishes.
            if (e.getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
                setRefreshFail(e);
            } else {
                internalSetTokenState(OAuthTokenState.AVAILABLE);
            }
            throw new AuthFatalFailureException(e);
        }
    }

    /**
     * Convenient method for wait.
     */
    protected void doWait(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param doLock
     *            if true, this method will lock. DO call unlock() after your logic is done.
     * @param interval
     */
    protected void waitForLock(boolean doLock, long interval) {
        while (!getAndSetLock(doLock)) {
            doWait(interval);
        }
    }
}
