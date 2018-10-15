//
//  RNIOSExportJsToReact.h
//
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import <React/RCTEventEmitter.h>
#define RNIOSExportJsToReact(noti) [[NSNotificationCenter defaultCenter] postNotificationName:@"event-emitted" object:noti];

@interface RNIOSExportJsToReact : RCTEventEmitter
+ (void)emitEventWithName:(NSString *)name andPayload:(NSDictionary *)payload;

@end
