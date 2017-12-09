/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.grpc.contrib.instancemode;

import io.grpc.Attributes;
import io.grpc.ServerTransportFilter;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 *
 */
public class PerSessionServerTransportFilter extends ServerTransportFilter {
    static final Attributes.Key<UUID> SESSION_ID = Attributes.Key.of("SESSION_ID");

    private static Observable terminated = new Observable() {
        @Override
        public void notifyObservers(Object arg) {
            setChanged();
            super.notifyObservers(arg);
        }
    };

    /**
     *
     * @param term
     */
    static void subscribeToTerminated(Observer term) {
        terminated.addObserver(term);
    }

    @Override
    public Attributes transportReady(Attributes transportAttrs) {
        return Attributes.newBuilder(transportAttrs).set(SESSION_ID, UUID.randomUUID()).build();
    }

    @Override
    public void transportTerminated(Attributes transportAttrs) {
        UUID sessionKey = transportAttrs.get(SESSION_ID);
        if (sessionKey != null) {
            terminated.notifyObservers(sessionKey);
        }
    }
}
