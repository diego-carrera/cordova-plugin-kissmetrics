#import <KISSmetricsAPI.h>
#import <Cordova/CDV.h>
#import "AppDelegate.h"

@interface KissmetricsPlugin : CDVPlugin
- (void)identify:(CDVInvokedUrlCommand *)command;
- (void)identity:(CDVInvokedUrlCommand *)command;
- (void)setProperties:(CDVInvokedUrlCommand *)command;
- (void)setDistinct:(CDVInvokedUrlCommand *)command;
- (void)record:(CDVInvokedUrlCommand *)command;
@end
