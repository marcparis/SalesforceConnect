/*
 * Copyright (C) 2016 Authlete, Inc.
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
package com.authlete.jaxrs.server.api;


import com.authlete.common.dto.Property;
import com.authlete.common.types.User;
import com.authlete.jaxrs.server.db.UserDao;
import com.authlete.jaxrs.spi.TokenRequestHandlerSpiAdapter;


/**
 * Implementation of {@link com.authlete.jaxrs.spi.TokenRequestHandlerSpi
 * TokenRequestHandlerSpi} interface which needs to be given to the
 * constructor of {@link com.authlete.jaxrs.TokenRequestHandler
 * TokenRequestHandler}.
 *
 * @author Takahiko Kawasaki
 */
class TokenRequestHandlerSpiImpl extends TokenRequestHandlerSpiAdapter
{
    @Override
    public String authenticateUser(String username, String password)
    {
        // Note: this method needs to be implemented only when you
        // want to support "Resource Owner Password Credentials Grant".

        // Search the user database for a user.
        User user = UserDao.getByCredentials(username, password);

        // If not found.
        if (user == null)
        {
            // There is no user who has the credentials.
            return null;
        }

        // Return the subject (= unique identifier) of the user.
        return user.getSubject();
    }


    @Override
    public Property[] getProperties()
    {
        // Properties returned from this method will be associated with an
        // access token that will be issued as a result of the token request.
        return null;
    }
}
