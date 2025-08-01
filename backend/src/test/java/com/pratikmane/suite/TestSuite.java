package com.pratikmane.suite;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import com.pratikmane.service.UserServiceTest;
import com.pratikmane.service.PostServiceTest;
import com.pratikmane.service.MessageServiceTest;
import com.pratikmane.controller.AuthControllerTest;
import com.pratikmane.repository.UserRepositoryTest;
import com.pratikmane.integration.WeChatApplicationIntegrationTest;

/**
 * Test Suite for running all unit tests
 */
@Suite
@SuiteDisplayName("WeChat Application Unit Test Suite")
@SelectClasses({
    UserServiceTest.class,
    PostServiceTest.class,
    MessageServiceTest.class,
    AuthControllerTest.class,
    UserRepositoryTest.class
})
public class UnitTestSuite {
    // Test suite runner for all unit tests
}

/**
 * Test Suite for running all integration tests
 */
@Suite
@SuiteDisplayName("WeChat Application Integration Test Suite")
@SelectClasses({
    WeChatApplicationIntegrationTest.class
})
class IntegrationTestSuite {
    // Test suite runner for all integration tests
}

/**
 * Test Suite for running all tests
 */
@Suite
@SuiteDisplayName("WeChat Application Complete Test Suite")
@SelectPackages({
    "com.pratikmane.service",
    "com.pratikmane.controller", 
    "com.pratikmane.repository",
    "com.pratikmane.integration"
})
class AllTestSuite {
    // Test suite runner for all tests
}
