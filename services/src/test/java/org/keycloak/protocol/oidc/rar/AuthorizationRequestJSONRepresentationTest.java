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
package org.keycloak.protocol.oidc.rar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.keycloak.rar.AuthorizationDetailsJSONRepresentation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

/**
 * @author <a href="mailto:dgozalob@redhat.com">Daniel Gozalo</a>
 */
public class AuthorizationRequestJSONRepresentationTest {


    @Test
    public void testParseSimpleStaticFieldsRequestObject() throws JsonProcessingException {
        String authorizationDetails = "{\n" +
                "      \"type\":\"account_information\",\n" +
                "      \"actions\":[\n" +
                "         \"list_accounts\"\n" +
                "      ],\n" +
                "      \"locations\":[\n" +
                "         \"https://example.com/accounts\"\n" +
                "      ]\n" +
                "   }";
        ObjectMapper mapper = new ObjectMapper();
        AuthorizationDetailsJSONRepresentation mappedObject = mapper.readValue(authorizationDetails, AuthorizationDetailsJSONRepresentation.class);
        assertThat(mappedObject.getActions(), hasItem("list_accounts"));
        assertThat(mappedObject.getLocations(), hasItem("https://example.com/accounts"));
        assertThat(mappedObject.getType(), is("account_information"));
    }

    @Test
    public void testParseDynamicFieldsRequestObject() throws JsonProcessingException {
        String authorizationDetails = "      {\n" +
                "         \"type\":\"account_information\",\n" +
                "         \"access\":{\n" +
                "            \"accounts\":[\n" +
                "               {\n" +
                "                  \"iban\":\"DE2310010010123456789\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"maskedPan\":\"123456xxxxxx1234\"\n" +
                "               }\n" +
                "            ],\n" +
                "            \"balances\":[\n" +
                "               {\n" +
                "                  \"iban\":\"DE2310010010123456789\"\n" +
                "               }\n" +
                "            ],\n" +
                "            \"transactions\":[\n" +
                "               {\n" +
                "                  \"iban\":\"DE2310010010123456789\"\n" +
                "               },\n" +
                "               {\n" +
                "                  \"maskedPan\":\"123456xxxxxx1234\"\n" +
                "               }\n" +
                "            ]\n" +
                "         },\n" +
                "         \"recurringIndicator\":true\n" +
                "      }";
        ObjectMapper mapper = new ObjectMapper();
        AuthorizationDetailsJSONRepresentation mappedObject = mapper.readValue(authorizationDetails, AuthorizationDetailsJSONRepresentation.class);
        System.out.println(mappedObject);
        assertThat(mappedObject.getCustomData().get("recurringIndicator"), is(true));
    }


}
