//
//  FaceSDKManager.h
//  IDLFaceSDK
//
//  Created by Tong,Shasha on 2017/5/15.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "FaceSDK.h"

typedef NS_ENUM(NSInteger, FaceLivenessActionType) {
    FaceLivenessActionTypeLiveEye = 0,
    FaceLivenessActionTypeLiveMouth = 1,
    FaceLivenessActionTypeLiveYawRight = 2,
    FaceLivenessActionTypeLiveYawLeft = 3,
    FaceLivenessActionTypeLivePitchUp = 4,
    FaceLivenessActionTypeLivePitchDown = 5,
    FaceLivenessActionTypeLiveYaw = 6,
    FaceLivenessActionTypeNoAction = 7,
};

typedef NS_ENUM(NSUInteger, ResultCode) {
    ResultCodeOK,
    ResultCodePitchOutofDownRange,  //头部偏低
    ResultCodePitchOutofUpRange,   //头部偏高
    ResultCodeYawOutofLeftRange,     //头部偏左
    ResultCodeYawOutofRightRange,     //头部偏右
    ResultCodePoorIllumination,      //光照不足
    ResultCodeNoFaceDetected,    //没有检测到人脸
    ResultCodeDataNotReady,
    ResultCodeDataHitOne, //采集到一张照片
    ResultCodeDataHitLast, //采集到最后一张照片
    ResultCodeImageBlured,     //图像模糊
    ResultCodeOcclusionLeftEye,  //左眼有遮挡
    ResultCodeOcclusionRightEye, //右眼有遮挡
    ResultCodeOcclusionNose,     //鼻子有遮挡
    ResultCodeOcclusionMouth,    //嘴巴有遮挡
    ResultCodeOcclusionLeftContour,  //左脸颊有遮挡
    ResultCodeOcclusionRightContour, //右脸颊有遮挡
    ResultCodeOcclusionChinCoutour,  //下颚有遮挡
    ResultCodeVerifyInitError,          //鉴权失败
    ResultCodeVerifyDecryptError,
    ResultCodeVerifyInfoFormatError,
    ResultCodeVerifyExpired,
    ResultCodeVerifyMissRequiredInfo,
    ResultCodeVerifyInfoCheckError,
    ResultCodeVerifyLocalFileError,
    ResultCodeVerifyRemoteDataError,
    ResultCodeUnknowType            //未知类型
};

@class FaceInfo;
@interface FaceSDKManager : NSObject

@property (nonatomic, assign) CGFloat conditionTimeout;

+ (instancetype)sharedInstance;

//鉴权方法
- (void)setLicenseID:(NSString *)licenseID andLocalLicenceFile:(NSString *)localLicencePath;

- (BOOL)canWork;

//set方法
- (void)setMinFaceSize:(NSInteger)width;

- (void)setCropFaceSizeWidth:(CGFloat)width;

- (void)setNotFaceThreshold:(CGFloat)th;

- (void)setOccluThreshold:(CGFloat)thr;

- (void)setIllumThreshold:(NSInteger)thr;

- (void)setBlurThreshold:(CGFloat)thr;

- (void)setEulurAngleThrPitch:(NSInteger)pitch yaw:(NSInteger)yaw roll:(NSInteger)roll;

- (void)setIsCheckQuality:(BOOL)isCheck;

- (void)setMaxCropImageNum:(NSInteger)imageNum;

- (void)clearTrackedFaces;

- (void)setConditionTimeout:(CGFloat)timeout;

- (void)detectWithImage:(UIImage *)image completion: (void (^)(FaceInfo * faceinfo, ResultCode resultCode))completion;

- (void)livenessWithImage:(UIImage *)image completion:(void (^)(FaceInfo * faceinfo, LivenessState* state, ResultCode resultCode))completion;

+ (NSString *)getVersion;

@end


@interface FaceInfo : NSObject

@property (nonatomic, assign) CGRect faceRect;
@property (nonatomic, assign) NSInteger faceId;
@property (nonatomic, strong) NSArray * landMarks;
@property (nonatomic, assign) CGFloat score;
@property (nonatomic, strong) NSArray * headPose;
@property (nonatomic, strong) NSArray * cropImages;

@end
