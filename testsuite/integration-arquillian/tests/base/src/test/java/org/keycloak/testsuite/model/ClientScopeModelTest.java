/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates
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

package org.keycloak.testsuite.model;

import org.junit.Test;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.arquillian.annotation.AuthServerContainerExclude;
import org.keycloak.testsuite.arquillian.annotation.ModelTest;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.Matchers.equalToIgnoringCase;


/**
 * @author <a href="mailto:dgozalob@redhat.com">Daniel Gozalo</a>
 */
@AuthServerContainerExclude(AuthServerContainerExclude.AuthServer.REMOTE)
public class ClientScopeModelTest extends AbstractKeycloakTest {
    private ClientScopeModel clientScope;
    private String realmName = "original";
    private KeycloakSession currentSession;

    @Test
    @ModelTest
    public void testAddDynamicScope(KeycloakSession session) {
        KeycloakModelUtils.runJobInTransaction(session.getKeycloakSessionFactory(), (KeycloakSession transactionSession) -> {
            currentSession = transactionSession;
            RealmModel realm = currentSession.realms().getRealmByName(realmName);
            assertThat(String.format("Realm Model '%1s' is NULL !!", realmName), realm, notNullValue());
            clientScope = realm.addClientScope("dynamic-scope");
            clientScope.setProtocol("openid-connect");
            clientScope.setAttribute(ClientScopeModel.IS_DYNAMIC_SCOPE, "true");
            clientScope.setAttribute(ClientScopeModel.DYNAMIC_SCOPE_REGEXP, "dynamic-scope:*");
        });
        KeycloakModelUtils.runJobInTransaction(session.getKeycloakSessionFactory(), (KeycloakSession transactionSession) -> {
            currentSession = transactionSession;
            RealmModel realm = currentSession.realms().getRealmByName(realmName);
            assertThat(String.format("Realm Model '%1s' is NULL !!", realmName), realm, notNullValue());
            ClientScopeModel scope = realm.getClientScopeById(clientScope.getId());
            assertThat(String.format("IsDynamicScope should return the value set to the %1s attribute", ClientScopeModel.IS_DYNAMIC_SCOPE), scope.isDynamicScope(), is(true));
            assertThat(String.format("getDynamicScopeRegexp should return the value set to the %1s attribute", ClientScopeModel.DYNAMIC_SCOPE_REGEXP),
                    scope.getDynamicScopeRegexp(), equalToIgnoringCase("dynamic-scope:*"));
        });
    }


    @Override
    public void addTestRealms(List<RealmRepresentation> testRealms) {
        RealmRepresentation realm = new RealmRepresentation();
        realm.setRealm(realmName);
        realm.setEnabled(true);
        testRealms.add(realm);
    }
}
