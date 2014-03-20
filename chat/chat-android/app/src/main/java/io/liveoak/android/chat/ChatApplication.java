package io.liveoak.android.chat;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.jboss.aerogear.android.Callback;
import org.jboss.aerogear.android.unifiedpush.PushConfig;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.Registrations;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import io.liveoak.helper.LiveOak;

/**
 * Created by mwringe on 28/02/14.
 */
public class ChatApplication extends Application {

    /**********************************************************/
    /*           Application Configuration Setting            */
    /**
     * ******************************************************
     */

    // LiveOak Settings
    private static final String LIVEOAK_HOST = <INSERT HOST HERE>; //eg hostname or ip address;
    private static final int LIVEOAK_PORT = 8080;


    // LiveOak Application Name
    private static final String APPLICATION_NAME = "chat";
    // LiveOak PushSubscription Settings
    private static final String UPS_RESOURCE_NAME = "push";
    private static final String UPS_VARIANT_RESOURCE_ID = "liveoak-chat-android";
    private static final String RESOURCE_SUBSCRIPTION = "/chat/storage/chat/*";
    private static final String MESSAGE = "{'alert': 'New message', 'title':'LiveOak Chat'}";


    // A unique alias for this particular application instance.
    protected String alias;

    /**
     * ******************************************************
     */

    private LiveOak liveOak;
    private LiveOak.PushSubscription subscription;
    private LiveOak.UPSRegistration registration;

    @Override
    public void onCreate() {
        super.onCreate();

        // if the alias doesn't already exist, then create it
        SharedPreferences preferences = getSharedPreferences(this.getClass().getSimpleName(), Context.MODE_PRIVATE);
        alias = preferences.getString("alias", null);
        if (alias == null) {
            alias = UUID.randomUUID().toString();
            preferences.edit().putString("alias", alias).commit();
        }

        // Setup the LiveOak instance and create a subscription for it
        this.liveOak = new LiveOak(LIVEOAK_HOST, LIVEOAK_PORT, APPLICATION_NAME);

        try {
            JSONObject message = new JSONObject(MESSAGE);
            this.subscription = liveOak.createPushSubscription(UPS_RESOURCE_NAME, alias, RESOURCE_SUBSCRIPTION, message);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        this.registration = liveOak.createUPSRegistration(UPS_RESOURCE_NAME, UPS_VARIANT_RESOURCE_ID, alias);
    }


    // Accessor method for Activities to access the Server URL for the LiveOak instance
    public LiveOak getLiveOak() {
        return this.liveOak;
    }

    // Accessor method for Activities to access the subscription for receiving chats
    public LiveOak.PushSubscription getSubscription() {
        return this.subscription;
    }

    public LiveOak.UPSRegistration getRegistration() {
        return this.registration;
    }
}
