#import "KissmetricsPlugin.h"
#import <objc/runtime.h>

@interface KissmetricsPlugin()
@end

@implementation KissmetricsPlugin

- (id)settingForKey:(NSString*)key {
  return [self.commandDelegate.settings objectForKey:[key lowercaseString]];
}


- (void)pluginInitialize {
    NSLog(@"Starting Kissmetrics plugin");

    // Add notification listener for tracking app activity with FB Events
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(applicationDidFinishLaunching:)
                                                 name:UIApplicationDidFinishLaunchingNotification object:nil];

}


- (void) applicationDidFinishLaunching:(NSNotification *) notification {
    NSLog(@"Kissmetrics applicationDidFinishLaunching");

    NSString *setting;
    NSString *apiKey;

    setting  = @"kissmetrics_api_key";
    if ([self settingForKey:setting]) {
      apiKey = [self settingForKey:setting];
    }

    [KISSmetricsAPI sharedAPIWithKey:apiKey];
    [[KISSmetricsAPI sharedAPI] autoRecordInstalls];
    [[KISSmetricsAPI sharedAPI] autoRecordAppLifecycle];
    [[KISSmetricsAPI sharedAPI] autoSetAppProperties];
    [[KISSmetricsAPI sharedAPI] autoSetHardwareProperties];

}


#pragma mark - Cordova commands
- (void)identify:(CDVInvokedUrlCommand *)command {
  NSLog(@"Running identify");
  CDVPluginResult *res;
  if ([command.arguments count] == 0) {
    // Not enough arguments
    res = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid arguments"];
    [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
    return;
  }

  NSString* identity = [command.arguments objectAtIndex:0];
  [[KISSmetricsAPI sharedAPI] identify:identity];

  res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
  [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];

}

- (void)identity:(CDVInvokedUrlCommand *)command {
    NSLog(@"Running identity");

    NSString* identity = [[KISSmetricsAPI sharedAPI] identity];
    NSLog(@"Identity: %@", identity);

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: identity];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)setProperties:(CDVInvokedUrlCommand *)command {
    NSLog(@"Running setProperties");

    NSDictionary *properties = [[command.arguments objectAtIndex:0] copy];

    [[KISSmetricsAPI sharedAPI] set:properties];
    CDVPluginResult *res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
}

- (void)setDistinct:(CDVInvokedUrlCommand *)command {
    NSLog(@"Running setDistinct");
    NSString *propertyName  = [command.arguments objectAtIndex:0];
    NSString *propertyValue = [command.arguments objectAtIndex:1];
    [[KISSmetricsAPI sharedAPI] setDistinct:propertyValue forKey:propertyName];

    CDVPluginResult *res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
}

- (void)record:(CDVInvokedUrlCommand *)command {
    NSLog(@"Running record");
    CDVPluginResult *res;
    if ([command.arguments count] == 0) {
      // Not enough arguments
      res = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"Invalid arguments"];
      [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
      return;
    }
    NSString *event = [command.arguments objectAtIndex:0];
    if ([command.arguments count] == 1) {
        [[KISSmetricsAPI sharedAPI] record:event];
    } else {
        NSDictionary *properties = [[command.arguments objectAtIndex:1] copy];
        [[KISSmetricsAPI sharedAPI] record:event withProperties:properties];
    }

    res = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:res callbackId:command.callbackId];
}
@end
