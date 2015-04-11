package com.lenddo.sdk.core;

import com.lenddo.sdk.models.ApplicationDefinition;

/**
 * Created by joseph on 8/14/14.
 */
public interface ApplicationDefinitionListener {
    void onComplete(ApplicationDefinition definition);

    void onError(int statusCode, String rawResponse);
}
