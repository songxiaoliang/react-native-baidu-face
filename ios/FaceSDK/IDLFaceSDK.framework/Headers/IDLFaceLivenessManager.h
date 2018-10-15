//
//  IDLFaceLivenessManager.h
//  IDLFaceSDK
//
//  Created by Tong,Shasha on 2017/5/18.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <CoreGraphics/CoreGraphics.h>

#define TIME_THRESHOLD_FOR_ANOTHER_SESSION 2.0

typedef NS_ENUM(NSUInteger, LivenessRemindCode) {
    LivenessRemindCodeOK = 0,   //成功
    LivenessRemindCodePitchOutofDownRange = 1,    //头部偏低
    LivenessRemindCodePitchOutofUpRange = 2,  //头部偏高
    LivenessRemindCodeYawOutofLeftRange = 3,  //头部偏左
    LivenessRemindCodeYawOutofRightRange = 4, //头部偏右
    LivenessRemindCodePoorIllumination = 5,   //光照不足
    LivenessRemindCodeNoFaceDetected = 6, //没有检测到人脸
    LivenessRemindCodeDataHitOne,
    LivenessRemindCodeDataHitLast,
    LivenessRemindCodeImageBlured,    //图像模糊
    LivenessRemindCodeOcclusionLeftEye,   //左眼有遮挡
    LivenessRemindCodeOcclusionRightEye,  //右眼有遮挡
    LivenessRemindCodeOcclusionNose, //鼻子有遮挡
    LivenessRemindCodeOcclusionMouth,    //嘴巴有遮挡
    LivenessRemindCodeOcclusionLeftContour,  //左脸颊有遮挡
    LivenessRemindCodeOcclusionRightContour, //右脸颊有遮挡
    LivenessRemindCodeOcclusionChinCoutour,  //下颚有遮挡
    LivenessRemindCodeTooClose,  //太近
    LivenessRemindCodeTooFar,    //太远
    LivenessRemindCodeBeyondPreviewFrame,    //出框
    LivenessRemindCodeLiveEye,   //眨眨眼
    LivenessRemindCodeLiveMouth, //张大嘴
    LivenessRemindCodeLiveYawRight,  //向左摇头
    LivenessRemindCodeLiveYawLeft,   //向右摇头
    LivenessRemindCodeLivePitchUp,   //向上抬头
    LivenessRemindCodeLivePitchDown, //向下低头
    LivenessRemindCodeLiveYaw,   //摇摇头
    LivenessRemindCodeSingleLivenessFinished,    //完成一个活体动作
    LivenessRemindCodeVerifyInitError,          //鉴权失败
    LivenessRemindCodeVerifyDecryptError,
    LivenessRemindCodeVerifyInfoFormatError,
    LivenessRemindCodeVerifyExpired,
    LivenessRemindCodeVerifyMissRequiredInfo,
    LivenessRemindCodeVerifyInfoCheckError,
    LivenessRemindCodeVerifyLocalFileError,
    LivenessRemindCodeVerifyRemoteDataError,
    LivenessRemindCodeTimeout,  //超时
    LivenessRemindCodeConditionMeet
};

typedef void (^LivenessStrategyCompletion) (NSDictionary * images, LivenessRemindCode remindCode);

@interface IDLFaceLivenessManager : NSObject

// 声音的开关
@property (nonatomic, assign) BOOL enableSound;

+ (instancetype)sharedInstance;

- (void)livenessStratrgyWithImage:(UIImage *)image previewRect:(CGRect)previewRect detectRect:(CGRect)detectRect completionHandler:(LivenessStrategyCompletion)completion;

- (void)reset;

-(void)startInitial;

- (void)livenesswithList:(NSArray *)array order:(BOOL)order numberOfLiveness:(NSInteger)numberOfLiveness;

@end
