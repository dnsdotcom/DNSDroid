//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package com.dns.android.authoritative.rest;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import com.dns.android.authoritative.utils.DNSPrefs_;

public final class RestClient_
    extends RestClient
{

    private Context context_;

    private RestClient_(Context context) {
        context_ = context;
        init_();
    }

    public void afterSetContentView_() {
        if (!(context_ instanceof Activity)) {
            return ;
        }
    }

    /**
     * You should check that context is an activity before calling this method
     * 
     */
    public View findViewById(int id) {
        Activity activity_ = ((Activity) context_);
        return activity_.findViewById(id);
    }

    @SuppressWarnings("all")
    private void init_() {
        if (context_ instanceof Activity) {
            Activity activity = ((Activity) context_);
        }
        prefs = new DNSPrefs_(context_);
    }

    public static RestClient_ getInstance_(Context context) {
        return new RestClient_(context);
    }

    public void rebind(Context context) {
        context_ = context;
        init_();
    }

}
