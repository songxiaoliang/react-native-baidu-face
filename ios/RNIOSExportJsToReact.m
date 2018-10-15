//
//  RNIOSExportJsToReact.m
//
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "RNIOSExportJsToReact.h"

@implementation RNIOSExportJsToReact
RCT_EXPORT_MODULE();
// @synthesize bridge = _bridge;

- (NSArray<NSString *> *)supportedEvents {
  return @[@"FaceCheckHelper"]; //这里返回的将是你要发送的消息名的数组。
}
- (void)startObserving
{
  [[NSNotificationCenter defaultCenter] addObserver:self
                                           selector:@selector(emitEventInternal:)
                                               name:@"event-emitted"
                                             object:nil];
}
- (void)stopObserving
{
  [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)emitEventInternal:(NSNotification *)notification
{
  [self sendEventWithName:@"FaceCheckHelper"
                     body:notification.object];
}

+ (void)emitEventWithName:(NSString *)name andPayload:(NSDictionary *)payload
{
  [[NSNotificationCenter defaultCenter] postNotificationName:@"event-emitted"
                                                      object:self
                                                    userInfo:payload];
}
@end
