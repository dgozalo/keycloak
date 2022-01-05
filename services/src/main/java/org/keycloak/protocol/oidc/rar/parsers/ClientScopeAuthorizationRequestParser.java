/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
 */
package org.keycloak.protocol.oidc.rar.parsers;

import org.keycloak.OAuth2Constants;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.protocol.oidc.TokenManager;
import org.keycloak.protocol.oidc.rar.AuthorizationRequestParserProvider;
import org.keycloak.rar.AuthorizationRequestContext;
import org.keycloak.protocol.oidc.rar.model.DynamicScopeRepresentation;
import org.keycloak.rar.AuthorizationDetails;
import org.keycloak.rar.AuthorizationDetailsJSONRepresentation;
import org.keycloak.rar.AuthorizationRequestSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:dgozalob@redhat.com">Daniel Gozalo</a>
 */
public class ClientScopeAuthorizationRequestParser implements AuthorizationRequestParserProvider {

    private static final String STATIC_SCOPE_RAR_TYPE = "http://keycloak.org/auth-type/static-oauth2-scope";
    private static final String DYNAMIC_SCOPE_RAR_TYPE = "http://keycloak.org/auth-type/dynamic-oauth2-scope";

    @Override
    public AuthorizationRequestContext parseScopes(String scopeParam, ClientModel client) {
        List<AuthorizationDetails> authDetails = TokenManager.parseScopeParameter(scopeParam)
                .map(scope -> buildAuthorizationDetailsJSONRepresentation(scope, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new AuthorizationRequestContext(authDetails);
    }

    private AuthorizationDetails buildAuthorizationDetailsJSONRepresentation(String scope, ClientModel client) {
        DynamicScopeRepresentation dynamicScopeRepresentation = this.getMatchingClientScopes(scope, client);
        if (dynamicScopeRepresentation == null) {
            return null;
        }
        AuthorizationDetailsJSONRepresentation representation = new AuthorizationDetailsJSONRepresentation();
        representation.setCustomData("access", Collections.singletonList(dynamicScopeRepresentation.getScope()));
        representation.setType(STATIC_SCOPE_RAR_TYPE);
        if (dynamicScopeRepresentation.getParameter() != null) {
            representation.setType(DYNAMIC_SCOPE_RAR_TYPE);
            representation.setCustomData("scope_parameter", dynamicScopeRepresentation.getParameter());
        }
        return new AuthorizationDetails(scope, AuthorizationRequestSource.SCOPE, representation);
    }

    //TODO: Find a good place for this
    private DynamicScopeRepresentation getMatchingClientScopes(String requestScope, ClientModel clientModel) {
        if (OAuth2Constants.SCOPE_OPENID.equalsIgnoreCase(requestScope)) {
            return new DynamicScopeRepresentation(requestScope);
        }

        Map<String, ClientScopeModel> scopeModelMap = Stream.of(clientModel.getClientScopes(true), clientModel.getClientScopes(false))
                .flatMap(map -> map.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Optional<DynamicScopeRepresentation> matchedDynamicScope = scopeModelMap.values().stream()
                .filter(ClientScopeModel::isDynamicScope)
                .map(clientScopeModel -> Pattern.compile(clientScopeModel.getDynamicScopeRegexp()))
                .map(pattern -> pattern.matcher(requestScope))
                .filter(Matcher::matches)
                .map(matcher -> new DynamicScopeRepresentation(requestScope, matcher.group(0)))
                .findFirst();

        //TODO: Cache the compiled regexps

        return matchedDynamicScope.orElseGet(() -> {
            if (scopeModelMap.containsKey(requestScope)) {
                return new DynamicScopeRepresentation(scopeModelMap.get(requestScope).getName());
            }
            return null;
        });
    }

    @Override
    public void close() {

    }
}
