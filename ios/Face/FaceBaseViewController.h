//
//  FaceBaseViewController.h
//  IDLFaceSDKDemoOC
//
//  Created by 阿凡树 on 2017/5/23.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CircleView.h"

typedef enum : NSUInteger {
    CommonStatus,
    PoseStatus,
    occlusionStatus
} WarningStatus;

@interface FaceBaseViewController : UIViewController

@property (nonatomic, readwrite, retain) UIImageView *displayImageView;
@property (nonatomic, readwrite, assign) BOOL hasFinished;
@property (nonatomic, readwrite, retain) UIImage* coverImage;
@property (nonatomic, readwrite, assign) CGRect previewRect;
@property (nonatomic, readwrite, assign) CGRect detectRect;
@property (nonatomic, readwrite, retain) CircleView * circleView;

- (void)faceProcesss:(UIImage *)image;

- (void)closeAction;

- (void)onAppWillResignAction;
- (void)onAppBecomeActive;

- (void)warningStatus:(WarningStatus)status warning:(NSString *)warning;
- (void)singleActionSuccess:(BOOL)success;

@end
