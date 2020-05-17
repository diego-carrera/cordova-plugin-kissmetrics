package org.apache.cordova.kissmetrics;


import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kissmetrics.sdk.ArchiverImpl;
import com.kissmetrics.sdk.KISSmetricsAPI;

public class KissmetricsPlugin extends CordovaPlugin {

  private final String TAG = "KissmetricsPlugin";
  private String apiKey;

  @Override
  protected void pluginInitialize() {

    apiKey = preferences.getString("kissmetrics_api_key", null);
    if ( apiKey != null ) {
      Log.d( TAG, "Initialize Kissmetrics");
      Log.d( TAG, "Api Key " + apiKey );
      KISSmetricsAPI.sharedAPI(apiKey, cordova.getActivity().getApplicationContext());
      KISSmetricsAPI.sharedAPI().autoRecordInstalls();
      KISSmetricsAPI.sharedAPI().autoSetAppProperties();
      KISSmetricsAPI.sharedAPI().autoSetHardwareProperties();
    }


  }

  @Override
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

    if ( apiKey == null ) {
      callbackContext.error("No api key defined");
      return true;
    }

    if ( action.equals("identify") ) {
      executeIdentify(args, callbackContext);
      return true;
    }

    if ( action.equals("identity") ) {
      String identity = KISSmetricsAPI.sharedAPI().identity();
      callbackContext.success(identity);
      return true;
    }

    if ( action.equals("setProperties") ) {
      executeSet(args, callbackContext);
      return true;
    }

    if ( action.equals("setDistinct") ) {
      executeSetDistinct(args, callbackContext);
      return true;
    }

    if ( action.equals("record") ) {
      executeRecord(args, callbackContext);
      return true;
    }

    return false;
  }

  private void executeIdentify(JSONArray args, CallbackContext callbackContext ) throws JSONException {

    Log.d( TAG, "executeIdentify" );
    String identity = null;
    try {
      identity = args.getString(0);
    } catch (JSONException e) {
    }
    if ( identity == null || identity == ""  || identity == "null" ) {
      Log.d( TAG, "identity cannot be null " + identity );
      callbackContext.error("identity cannot be null");
    } else {
      Log.d( TAG, "setIdentity " + identity );
      KISSmetricsAPI.sharedAPI().identify(identity);
      callbackContext.success();
    }
  }

  private void executeSet(JSONArray args, CallbackContext callbackContext ) throws JSONException {
    Log.d( TAG, "executeSet" );
    JSONObject props;
    try {
      props = args.getJSONObject(0);
    } catch(JSONException e) {
      props = new JSONObject();
    }

    HashMap<String, String> properties = new HashMap<String, String>();
    Iterator<?> objectKeys = props.keys();
    while ( objectKeys.hasNext() ) {
      String key = (String)objectKeys.next();
      String value = props.getString( key );
      properties.put(key, value);
    }
    if ( properties.size() > 0 ) {
      Log.d( TAG, "properties" + properties );
      KISSmetricsAPI.sharedAPI().set(properties);
      callbackContext.success();
    } else {
      callbackContext.error("No properties given");
    }

  }

  private void executeSetDistinct(JSONArray args, CallbackContext callbackContext ) throws JSONException {
    Log.d( TAG, "executeSetDistinct" );
    String propertyName = null;
    String value        = null;

    try {
      propertyName = args.getString(0);
      value        = args.getString(1);
    } catch (JSONException e) {
    }
    if ( propertyName == "" || value == "" ) {
      callbackContext.error("Missing parameters");
    } else {
      Log.d( TAG, "properties" + propertyName + " = " + value );
      KISSmetricsAPI.sharedAPI().setDistinct(propertyName, value);
      callbackContext.success();
    }

  }


  private void executeRecord(JSONArray args, CallbackContext callbackContext ) throws JSONException {
    Log.d( TAG, "executeRecord" );

    String eventName = null;
    JSONObject props;

    try {
      eventName = args.getString(0);
    } catch (JSONException e) {
    }

    if ( eventName == null || eventName == "" ) {
      callbackContext.error("No event name provided");
    } else {

      try {
        props = args.getJSONObject(1);
      } catch(JSONException e) {
        props = new JSONObject();
      }

      HashMap<String, String> properties = new HashMap<String, String>();

      Iterator<?> objectKeys = props.keys();
      while ( objectKeys.hasNext() ) {
        String key = (String)objectKeys.next();
        String value = props.getString( key );
        properties.put(key, value);
      }
      Log.d( TAG, "eventName:" + eventName );
      if ( properties.size() > 0 ) {
        Log.d( TAG, "properties" + properties );
        KISSmetricsAPI.sharedAPI().record(eventName, properties);
      } else {
        KISSmetricsAPI.sharedAPI().record(eventName);
      }
      callbackContext.success();
    }
  }

}
