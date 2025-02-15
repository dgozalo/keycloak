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

package org.keycloak.it.cli.dist;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.function.Consumer;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.keycloak.it.junit5.extension.BeforeStartDistribution;
import org.keycloak.it.junit5.extension.CLIResult;
import org.keycloak.it.junit5.extension.DistributionTest;
import org.keycloak.it.junit5.extension.RawDistOnly;
import org.keycloak.it.utils.KeycloakDistribution;

import io.quarkus.test.junit.main.Launch;
import io.quarkus.test.junit.main.LaunchResult;

@DistributionTest(reInstall = DistributionTest.ReInstall.NEVER)
@BeforeStartDistribution(QuarkusPropertiesDistTest.SetDebugLogLevel.class)
@RawDistOnly(reason = "Containers are immutable")
@TestMethodOrder(OrderAnnotation.class)
public class QuarkusPropertiesDistTest {

    @Test
    @Launch({ "build", "--cache=local" })
    @Order(1)
    void testBuildWithPropertyFromQuarkusProperties(LaunchResult result) {
        CLIResult cliResult = (CLIResult) result;
        cliResult.assertMessage("DEBUG [");
        cliResult.assertBuild();
    }

    @Test
    @Launch({ "start", "--http-enabled=true", "--hostname-strict=false" })
    @Order(2)
    void testPropertyEnabledAtRuntime(LaunchResult result) {
        CLIResult cliResult = (CLIResult) result;
        cliResult.assertMessage("DEBUG [");
        cliResult.assertStarted();
    }

    @Test
    @Launch({ "-Dquarkus.log.level=INFO", "start", "--http-enabled=true", "--hostname-strict=false" })
    @Order(3)
    void testIgnoreQuarkusSystemPropertiesAtStart(LaunchResult result) {
        CLIResult cliResult = (CLIResult) result;
        cliResult.assertMessage("DEBUG [");
        cliResult.assertStarted();
    }

    @Test
    @Launch({ "-Dquarkus.log.level=INFO", "build" })
    @Order(4)
    void testIgnoreQuarkusSystemPropertyAtBuild(LaunchResult result) {
        CLIResult cliResult = (CLIResult) result;
        cliResult.assertMessage("DEBUG [");
        cliResult.assertBuild();
    }

    @Test
    @BeforeStartDistribution(SetDebugLogLevelInKeycloakConf.class)
    @Launch({ "build" })
    @Order(5)
    void testIgnoreQuarkusPropertyFromKeycloakConf(LaunchResult result) {
        CLIResult cliResult = (CLIResult) result;
        assertFalse(cliResult.getOutput().contains("DEBUG ["));
        cliResult.assertBuild();
    }

    public static class SetDebugLogLevel implements Consumer<KeycloakDistribution> {

        @Override
        public void accept(KeycloakDistribution distribution) {
            distribution.setQuarkusProperty("quarkus.log.level", "DEBUG");
        }
    }

    public static class SetDebugLogLevelInKeycloakConf implements Consumer<KeycloakDistribution> {

        @Override
        public void accept(KeycloakDistribution distribution) {
            distribution.deleteQuarkusProperties();
            distribution.setProperty("quarkus.log.level", "DEBUG");
        }
    }
}