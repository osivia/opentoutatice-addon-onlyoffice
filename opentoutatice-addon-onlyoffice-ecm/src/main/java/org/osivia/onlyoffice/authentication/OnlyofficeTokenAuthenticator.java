/*
 * (C) Copyright 2006-2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Antoine Taillefer
 */
package org.osivia.onlyoffice.authentication;

import java.util.List;
import java.util.Map;

import javax.security.auth.spi.LoginModule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.jwt.authentication.JWTTokenAuthenticator;
import org.osivia.jwt.service.JWTTokenService;

/**
 * Handles authentication with a JWT token sent as a request header or request parameter.
 * <p>
 * The user is retrieved with the {@link JWTTokenService}.
 * <p>
 * This Authentication Plugin is configured to be used with the Trusting_LM {@link LoginModule} plugin => no password
 * check will be done, a principal will be created from the userName if the user exists in the user directory.
 *
 */
public class OnlyofficeTokenAuthenticator extends JWTTokenAuthenticator {

    private static final Log log = LogFactory.getLog(OnlyofficeTokenAuthenticator.class);

    @Override
    protected String getUserByToken(String token) {

        String userid = null;
        Map<String, Object> payload = getJWTTokenService().getPayload(token, algorithmId);
        if (payload != null) {
            Map<String, Object> jsonPayload = (Map<String, Object>) payload.get("payload");
            if (jsonPayload != null) {
                List<Object> jsonActions = (List<Object>) jsonPayload.get("actions");
                if (jsonActions != null) {
                    Map<String, Object> jsonAction = (Map<String, Object>) jsonActions.get(0);
                    if (jsonAction != null) {
                        userid = (String) jsonAction.get("userid");
                    }
                }
            }
        }
        return userid;
    }
}
