/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.ivy.util.url;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ivy.util.Credentials;
import org.apache.ivy.util.Message;

/**
 *
 */
@SuppressWarnings("unchecked")
public final class CredentialsStore {
    /**
     * A Map of Credentials objects keyed by the 'key' of the Credentials.
     */
    private static final Map KEYRING = new HashMap();

    private static final Set SECURED_HOSTS = new HashSet();

    public static CredentialsStore INSTANCE = new CredentialsStore();

    private Map mKEYRING;
    private Set mSECURED_HOSTS;


    private CredentialsStore() {
        mKEYRING = KEYRING;
        mSECURED_HOSTS = SECURED_HOSTS;
    }

    public static void setNewINSTANCE() {
        CredentialsStore newstore = new CredentialsStore();
        newstore.mKEYRING  = new HashMap(KEYRING);
        newstore.mSECURED_HOSTS = new HashSet(SECURED_HOSTS);
        INSTANCE = newstore;
    }

    public void addCredentials(String realm, String host, String userName, String passwd) {
        if (userName == null) {
            return;
        }
        Credentials c = new Credentials(realm, host, userName, passwd);
        Message.debug("credentials added: " + c);
        mKEYRING.put(c.getKey(), c);
        mSECURED_HOSTS.add(host);
    }

    public Credentials getCredentials(String realm, String host) {
        String key = Credentials.buildKey(realm, host);
        Message.debug("try to get credentials for: " + key);
        if (!mKEYRING.containsKey(key)) {
            key = Credentials.buildKey("*", host);
            Message.debug("try to get credentials for: " + key);
            if (!mKEYRING.containsKey(key)) {
                key = Credentials.buildKey(null, host);
                Message.debug("try to get credentials for: " + key);
            }
        }
        return (Credentials) mKEYRING.get(key);

    }

    public boolean hasCredentials(String host) {
        return mSECURED_HOSTS.contains(host);
    }

}
