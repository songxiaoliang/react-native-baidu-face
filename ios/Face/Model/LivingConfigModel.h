//
//  LivingConfigModel.h
//  IDLFaceSDKDemoOC
//
//  Created by 阿凡树 on 2017/5/23.
//  Copyright © 2017年 Baidu. All rights reserved.
//
//  此类为了存储用户选择活体检测命令而设置的单例类
//

#import <Foundation/Foundation.h>

@interface LivingConfigModel : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, readwrite, assign) BOOL isByOrder;
@property (nonatomic, readwrite, assign) NSInteger numOfLiveness;
@property (nonatomic, readwrite, retain) NSMutableArray *liveActionArray;

- (void)resetState;

@end
