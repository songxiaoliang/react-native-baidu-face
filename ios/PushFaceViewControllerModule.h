//
//  PushFaceViewControllerModule.h
//  faceTest
//
//  Created by jayden on 2018/9/6.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import "LivenessViewController.h"

@interface PushFaceViewControllerModule : NSObject<RCTBridgeModule,LivenessViewControllerDelegate>

@end
