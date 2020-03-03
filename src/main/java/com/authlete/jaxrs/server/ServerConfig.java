/*
 * Copyright (C) 2019 Authlete, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package com.authlete.jaxrs.server;


import com.authlete.jaxrs.server.util.ServerProperties;


/**
 * A class for configuration of this server.
 *
 * @author Hideki Ikeda
 */
public class ServerConfig
{
    /**
     * Properties.
     */
    private static final ServerProperties sProperties = new ServerProperties();


    /**
     * Property keys.
     */
    private static final String AUTHLETE_AD_BASE_URL_KEY                     = "authlete.ad.base_url";
    private static final String AUTHLETE_AD_WORKSPACE_KEY                    = "authlete.ad.workspace";
    private static final String AUTHLETE_AD_SYNC_CONNECT_TIMEOUT_KEY         = "authlete.ad.sync.connect_timeout";
    private static final String AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT_KEY = "authlete.ad.sync.additional_read_timeout";
    private static final String AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT_KEY        = "authlete.ad.async.connect_timeout";
    private static final String AUTHLETE_AD_ASYNC_READ_TIMEOUT_KEY           = "authlete.ad.async.read_timeout";
    private static final String AUTHLETE_AD_POLL_CONNECT_TIMEOUT_KEY         = "authlete.ad.poll.connect_timeout";
    private static final String AUTHLETE_AD_POLL_READ_TIMEOUT_KEY            = "authlete.ad.poll.read_timeout";
    private static final String AUTHLETE_AD_AUTH_TIMEOUT_RATIO_KEY           = "authlete.ad.auth_timeout_ratio";


    /**
     * Default configuration values.
     */
    private static final String DEFAULT_AUTHLETE_AD_BASE_URL                  = "https://cibasim.authlete.com";
    private static final int DEFAULT_AUTHLETE_AD_SYNC_CONNECT_TIMEOUT         = 10000; // 10000 milliseconds.
    private static final int DEFAULT_AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT = 10000; // 10000 milliseconds.
    private static final int DEFAULT_AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT        = 10000; // 10000 milliseconds.
    private static final int DEFAULT_AUTHLETE_AD_ASYNC_READ_TIMEOUT           = 10000; // 10000 milliseconds.
    private static final int DEFAULT_AUTHLETE_AD_POLL_CONNECT_TIMEOUT         = 10000; // 10000 milliseconds.
    private static final int DEFAULT_AUTHLETE_AD_POLL_READ_TIMEOUT            = 10000; // 10000 milliseconds.
    private static final float DEFALUT_AUTHLETE_AD_AUTH_TIMEOUT_RATIO         = 0.8f;


    /**
     * Determined configuration values.
     */
    private static final String AUTHLETE_AD_BASE_URL                  = sProperties.getString(AUTHLETE_AD_BASE_URL_KEY, DEFAULT_AUTHLETE_AD_BASE_URL);
    private static final String AUTHLETE_AD_WORKSPACE                 = sProperties.getString(AUTHLETE_AD_WORKSPACE_KEY);
    private static final int AUTHLETE_AD_SYNC_CONNECT_TIMEOUT         = sProperties.getInt(AUTHLETE_AD_SYNC_CONNECT_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_SYNC_CONNECT_TIMEOUT);
    private static final int AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT = sProperties.getInt(AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT);
    private static final int AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT        = sProperties.getInt(AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT);
    private static final int AUTHLETE_AD_ASYNC_READ_TIMEOUT           = sProperties.getInt(AUTHLETE_AD_ASYNC_READ_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_ASYNC_READ_TIMEOUT);
    private static final int AUTHLETE_AD_POLL_CONNECT_TIMEOUT         = sProperties.getInt(AUTHLETE_AD_POLL_CONNECT_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_POLL_CONNECT_TIMEOUT);
    private static final int AUTHLETE_AD_POLL_READ_TIMEOUT            = sProperties.getInt(AUTHLETE_AD_POLL_READ_TIMEOUT_KEY, DEFAULT_AUTHLETE_AD_POLL_READ_TIMEOUT);
    private static final float AUTHLETE_AD_AUTH_TIMEOUT_RATIO         = sProperties.getFloat(AUTHLETE_AD_AUTH_TIMEOUT_RATIO_KEY, DEFALUT_AUTHLETE_AD_AUTH_TIMEOUT_RATIO);


    /**
     * Get the base URL of <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim">
     * Authlete CIBA authentication device simulator API</a>.
     *
     * @return
     *         The base URL of <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim">
     *         Authlete CIBA authentication device simulator API</a>.
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim">Authlete CIBA authentication device simulator API</a>
     */
    public static String getAuthleteAdBaseUrl()
    {
        return AUTHLETE_AD_BASE_URL;
    }


    /**
     * Get the workspace for which end-user authentication and authorization is
     * performed on <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The workspace for which end-user authentication and authorization is
     *         performed on <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim">Authlete CIBA authentication device simulator API</a>
     */
    public static String getAuthleteAdWorkspace()
    {
        return AUTHLETE_AD_WORKSPACE;
    }


    /**
     * Get the connect timeout value (in milliseconds) used when the authorization
     * server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">
     * /api/authenticate/sync API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The connect timeout value (in milliseconds) used when the authorization
     *         server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">
     *         /api/authenticate/sync API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">/api/authenticate/sync API</a>
     */
    public static int getAuthleteAdSyncConnectTimeout()
    {
        return AUTHLETE_AD_SYNC_CONNECT_TIMEOUT;
    }


    /**
     * Get the value (in milliseconds) that is used to compute the read timeout
     * value used when the authorization server makes a request to <a
     * href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">
     * /api/authenticate/sync API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * <p>
     * The read timeout value is computed as follows.
     * </p>
     *
     * <p style="border: solid 1px black; padding: 0.5em;">
     * (read timeout) = (the duration of an <code>'auth_req_id'</code> in milliseconds) + (the value returned by this method)
     * </p>
     *
     * For more details, see {@link com.authlete.jaxrs.server.ad.AuthenticationDevice#syncAuth(String, String, int, String)
     * syncAuth} method in {@link com.authlete.jaxrs.server.ad.AuthenticationDevice
     * AuthenticationDevice}.
     *
     * @return
     *         The value (in milliseconds) that is used to compute the read timeout
     *         value used when the authorization server makes a request to <a
     *         href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">
     *         /api/authenticate/sync API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_sync">/api/authenticate/sync API</a>
     *
     * @see {@link com.authlete.jaxrs.server.ad.AuthenticationDevice AuthenticationDevice}.
     */
    public static int getAuthleteAdSyncAdditionalReadTimeout()
    {
        return AUTHLETE_AD_SYNC_ADDITIONAL_READ_TIMEOUT;
    }


    /**
     * Get the connect timeout value (in milliseconds) used when the authorization
     * server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">
     * /api/authenticate/async API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The connect timeout value (in milliseconds) used when the authorization
     *         server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">
     *         /api/authenticate/async API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">/api/authenticate/async API</a>
     */
    public static int getAuthleteAdAsyncConnectTimeout()
    {
        return AUTHLETE_AD_ASYNC_CONNECT_TIMEOUT;
    }


    /**
     * Get the read timeout value (in milliseconds) used when the authorization
     * server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">
     * /api/authenticate/async API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The read timeout value (in milliseconds) used when the authorization
     *         server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">
     *         /api/authenticate/async API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_async">/api/authenticate/async API</a>
     */
    public static int getAuthleteAdAsyncReadTimeout()
    {
        return AUTHLETE_AD_ASYNC_READ_TIMEOUT;
    }


    /**
     * Get the connect timeout value (in milliseconds) used when the authorization
     * server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">
     * /api/authenticate/poll API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The connect timeout value (in milliseconds) used when the authorization
     *         server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">
     *         /api/authenticate/poll API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">/api/authenticate/poll API</a>
     */
    public static int getAuthleteAdPollConnectTimeout()
    {
        return AUTHLETE_AD_POLL_CONNECT_TIMEOUT;
    }


    /**
     * Get the read timeout value (in milliseconds) used when the authorization
     * server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">
     * /api/authenticate/poll API</a> of <a href="https://cibasim.authlete.com">
     * Authlete CIBA authentication device simulator</a>.
     *
     * @return
     *         The read timeout value (in milliseconds) used when the authorization
     *         server makes a request to <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">
     *         /api/authenticate/poll API</a> of <a href="https://cibasim.authlete.com">
     *         Authlete CIBA authentication device simulator</a>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     *
     * @see <a href="https://app.swaggerhub.com/apis-docs/Authlete/cibasim/1.0.0#/default/post_api_authenticate_poll">/api/authenticate/poll API</a>
     */
    public static int getAuthleteAdPollReadTimeout()
    {
        return AUTHLETE_AD_POLL_READ_TIMEOUT;
    }


    /**
     * Get the ratio of timeout for end-user authentication/authorization on the
     * authentication device (<a href="https://cibasim.authlete.com">Authlete CIBA
     * authentication device simulator</a>) to the duration of an <code>'auth_req_id'</code>.
     * Must be specified between 0.0 and 1.0.
     *
     * <p>
     * This value is used to compute the timeout value based on the duration of
     * an <code>'auth_req_id'</code> as below.
     * </p>
     *
     * <p style="border: solid 1px black; padding: 0.5em;">
     * (timeout in seconds) = (the value returned by this method) * (the duration of an <code>'auth_req_id'</code> in seconds)
     * </p>
     *
     * For more details, see {@link com.authlete.jaxrs.server.api.backchannel.BaseAuthenticationDeviceProcessor#computeAuthTimeout
     * computeAuthTimeout()} method in {@link com.authlete.jaxrs.server.api.backchannel.BaseAuthenticationDeviceProcessor
     * BaseAuthenticationDeviceProcessor}.
     *
     * @return
     *         The ratio of timeout for end-user authentication/authorization on
     *         the authentication device (<a href="https://cibasim.authlete.com">Authlete
     *         CIBA authentication device simulator</a>) to the duration
     *         of an <code>'auth_req_id'</code>.
     *
     * @see <a href="https://cibasim.authlete.com">Authlete CIBA authentication device simulator</a>
     */
    public static float getAuthleteAdAuthTimeoutRatio()
    {
        return AUTHLETE_AD_AUTH_TIMEOUT_RATIO;
    }
}
