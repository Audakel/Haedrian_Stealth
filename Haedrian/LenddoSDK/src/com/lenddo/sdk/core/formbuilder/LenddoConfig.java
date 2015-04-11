package com.lenddo.sdk.core.formbuilder;

import com.lenddo.sdk.core.LenddoConstants;

/**
 * Created by joseph on 1/21/15.
 */
public class LenddoConfig {
    static boolean testMode = false;


    public static void setTestMode(boolean mode) {
        testMode = mode;
    }

    public static String getMemberServiceEndpoint() {
        if (!testMode) {
            return LenddoConstants.MEMBERS_SERVICE_ENDPOINT;
        } else {
            return LenddoConstants.TEST_MEMBERS_SERVICE_ENDPOINT;
        }
    }

    public static String getProductsServiceEndpoint() {
        if (!testMode) {
            return LenddoConstants.PRODUCTS_SERVICE_ENDPOINT;
        } else {
            return LenddoConstants.TEST_PRODUCTS_SERVICE_ENDPOINT;
        }
    }

    public static String getSocialServiceEndpoint() {
        if (!testMode) {
            return LenddoConstants.SOCIAL_SERVICE_ENDPOINT;
        } else {
            return LenddoConstants.TEST_SOCIAL_SERVICE_ENDPOINT;
        }
    }

    public static String getAuthorizeSiteUrl() {
        if (!testMode) {
            return LenddoConstants.AUTHORIZE_SITE_URL;
        } else {
            return LenddoConstants.TEST_AUTHORIZE_SITE_URL;
        }
    }
}
