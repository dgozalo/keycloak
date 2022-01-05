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
package org.keycloak.rar;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author <a href="mailto:dgozalob@redhat.com">Daniel Gozalo</a>
 */
public class AuthorizationDetails implements Serializable {

    String clientScope;

    AuthorizationRequestSource source;

    AuthorizationDetailsJSONRepresentation authorizationDetails;

    public AuthorizationDetails(String clientScope, AuthorizationRequestSource source, AuthorizationDetailsJSONRepresentation authorizationDetails) {
        this.clientScope = clientScope;
        this.source = source;
        this.authorizationDetails = authorizationDetails;
    }

    public String getClientScope() {
        return clientScope;
    }

    public void setClientScope(String clientScope) {
        this.clientScope = clientScope;
    }

    public AuthorizationRequestSource getSource() {
        return source;
    }

    public void setSource(AuthorizationRequestSource source) {
        this.source = source;
    }

    public AuthorizationDetailsJSONRepresentation getAuthorizationDetails() {
        return authorizationDetails;
    }

    public void setAuthorizationDetails(AuthorizationDetailsJSONRepresentation authorizationDetails) {
        this.authorizationDetails = authorizationDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorizationDetails that = (AuthorizationDetails) o;
        return Objects.equals(clientScope, that.clientScope) && source == that.source && Objects.equals(authorizationDetails, that.authorizationDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientScope, source, authorizationDetails);
    }

    @Override
    public String toString() {
        return "AuthorizationDetails{" +
                "clientScope=" + clientScope +
                ", source=" + source +
                ", authorizationDetails=" + authorizationDetails +
                '}';
    }
}
