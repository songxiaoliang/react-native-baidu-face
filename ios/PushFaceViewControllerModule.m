//
//  PushFaceViewControllerModule.m
//  faceTest
//
//  Created by jayden on 2018/9/6.
//  Copyright © 2018年 Facebook. All rights reserved.
//

#import "PushFaceViewControllerModule.h"
#import "AppDelegate.h"

#import "IDLFaceSDK/IDLFaceSDK.h"
#import "FaceParameterConfig.h"
#import "LivingConfigModel.h"

#import "RNIOSExportJsToReact.h"

@implementation PushFaceViewControllerModule



@synthesize bridge = _bridge;


RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(openPushFaceViewController:(NSDictionary*)dict) {
  dispatch_async(dispatch_get_main_queue(), ^{
    [self pushVC:dict];
  });
}
-(void)pushVC:(NSDictionary*)dict{
  if ([[FaceSDKManager sharedInstance] canWork]) {
    NSString* licensePath = [[NSBundle mainBundle] pathForResource:FACE_LICENSE_NAME ofType:FACE_LICENSE_SUFFIX];
    [[FaceSDKManager sharedInstance] setLicenseID:FACE_LICENSE_ID andLocalLicenceFile:licensePath];
  }
  [self setColor:dict];
  
  id sound = [dict objectForKey:@"sound"] ;
  if (sound != nil) {
    [IDLFaceLivenessManager sharedInstance].enableSound = sound;
  }
  
  id result = [dict objectForKey:@"quality"] ;
  if (result != nil) {
    [self setQuality:[dict objectForKey:@"quality"]];
  }
  
  LivenessViewController* lvc = [[LivenessViewController alloc] init];
  LivingConfigModel* model = [LivingConfigModel sharedInstance];
  
  
  id liveActionArray = [dict objectForKey:@"liveActionArray"] ;
  if (liveActionArray != nil && [liveActionArray isKindOfClass:[NSArray class]]) {
    LivingConfigModel.sharedInstance.liveActionArray = liveActionArray;
  }
  id order = [dict objectForKey:@"order"] ;
  if (order != nil) {
    LivingConfigModel.sharedInstance.isByOrder = order;
  }
  id numOfLiveness = [dict objectForKey:@"order"] ;
  if (numOfLiveness != nil) {
    LivingConfigModel.sharedInstance.numOfLiveness = (NSInteger)numOfLiveness;
  }
  [lvc livenesswithList:model.liveActionArray order:model.isByOrder numberOfLiveness:model.numOfLiveness];
  lvc.delegate = self;
  
  UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:lvc];
  nav.navigationBarHidden = true;
  AppDelegate *app = (AppDelegate *)[[UIApplication sharedApplication] delegate];
  [app.window.rootViewController presentViewController:nav animated:YES completion:nil];
  // 这个地方之前代码应该是有问题  会在打开人俩识别窗口后马上关闭 注释掉即可正常执行人脸识别流程
  //[app.window.rootViewController dismissViewControllerAnimated:YES completion:nil];
  
}
- (void)LivenessViewController:(NSDictionary *)images code:(NSInteger) remindCode{
  
  RNIOSExportJsToReact((@{@"images" : images,
                          @"remindCode": @(remindCode),
                          @"errorCode":@"0",
                          @"msg":@"成功"}));
}


-(void)setColor:(NSDictionary*)dict{
  NSUserDefaults * userDefaults = [NSUserDefaults standardUserDefaults];
  id circleOutSideColor = [dict objectForKey:@"CircleOutSideColor"] ;
  if (circleOutSideColor != nil) {
    NSArray * colorArray = [(NSString*)circleOutSideColor componentsSeparatedByString:@","];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[0]].integerValue) forKey:@"circleOutSideColorR"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[1]].integerValue) forKey:@"circleOutSideColorG"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[2]].integerValue) forKey:@"circleOutSideColorB"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[3]].integerValue) forKey:@"circleOutSideColorA"];
  }else{
    [userDefaults setInteger:246 forKey:@"circleOutSideColorR"];
    [userDefaults setInteger:166 forKey:@"circleOutSideColorG"];
    [userDefaults setInteger:35 forKey:@"circleOutSideColorB"];
    [userDefaults setInteger:1 forKey:@"circleOutSideColorA"];
  }
  
  id circleBackgroundColor = [dict objectForKey:@"CircleBackgroundColor"] ;
  if (circleBackgroundColor != nil) {
    NSArray * colorArray = [(NSString*)circleBackgroundColor componentsSeparatedByString:@","];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[0]].integerValue) forKey:@"circleBackgroundColorR"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[1]].integerValue) forKey:@"circleBackgroundColorG"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[2]].integerValue) forKey:@"circleBackgroundColorB"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[3]].integerValue) forKey:@"circleBackgroundColorA"];
  }else{
    [userDefaults setInteger:47 forKey:@"circleBackgroundColorR"];
    [userDefaults setInteger:47 forKey:@"circleBackgroundColorG"];
    [userDefaults setInteger:51 forKey:@"circleBackgroundColorB"];
    [userDefaults setInteger:1 forKey:@"circleBackgroundColorA"];
  }
  
  id faceTitleColor = [dict objectForKey:@"faceTitleColor"] ;
  if (faceTitleColor != nil) {
    NSArray * colorArray = [(NSString*)faceTitleColor componentsSeparatedByString:@","];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[0]].integerValue) forKey:@"faceTitleColorR"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[1]].integerValue) forKey:@"faceTitleColorG"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[2]].integerValue) forKey:@"faceTitleColorB"];
    [userDefaults setInteger:([NSString stringWithFormat:@"%@",colorArray[3]].integerValue) forKey:@"faceTitleColorA"];
  }else{
    [userDefaults setInteger:246 forKey:@"faceTitleColorR"];
    [userDefaults setInteger:166 forKey:@"faceTitleColorG"];
    [userDefaults setInteger:35 forKey:@"faceTitleColorB"];
    [userDefaults setInteger:1 forKey:@"faceTitleColorA"];
  }
  [userDefaults synchronize];
}

/**
 质量校验设置
 */
-(void)setQuality:(NSDictionary*)dict{
  
  // 设置最小检测人脸阈值
  [[FaceSDKManager sharedInstance] setMinFaceSize:[self getObj:dict key:@"minFaceSize" Default:200]];
  
  // 设置截取人脸图片大小
  [[FaceSDKManager sharedInstance] setCropFaceSizeWidth:[self getObj:dict key:@"cropFaceSizeWidth" Default:400]];
  
  // 设置人脸遮挡阀值
  [[FaceSDKManager sharedInstance] setOccluThreshold:[self getObj:dict key:@"occluThreshold" DefaultFloat:0.5]];
  
  // 设置亮度阀值
  [[FaceSDKManager sharedInstance] setIllumThreshold:[self getObj:dict key:@"illumThreshold" Default:40]];
  
  // 设置图像模糊阀值
  [[FaceSDKManager sharedInstance] setBlurThreshold:[self getObj:dict key:@"blurThreshold" DefaultFloat:0.7]];
  
  // 设置头部姿态角度
  [[FaceSDKManager sharedInstance]
   setEulurAngleThrPitch:[self getObj:dict key:@"EulurAngleThrPitch" Default:10]
   yaw:[self getObj:dict key:@"EulurAngleThrYaw" Default:10]
   roll:[self getObj:dict key:@"EulurAngleThrRoll" Default:10]];
  
  // 设置是否进行人脸图片质量检测
  [[FaceSDKManager sharedInstance] setIsCheckQuality:[self getObj:dict key:@"isCheckQuality" Default:YES]];
  
  // 设置超时时间
  [[FaceSDKManager sharedInstance] setConditionTimeout:[self getObj:dict key:@"conditionTimeout" Default:10]];
  
  // 设置人脸检测精度阀值
  [[FaceSDKManager sharedInstance] setNotFaceThreshold:[self getObj:dict key:@"notFaceThreshold" DefaultFloat:0.6]];
  
  // 设置照片采集张数
  [[FaceSDKManager sharedInstance] setMaxCropImageNum:[self getObj:dict key:@"maxCropImageNum" Default:1]];
}

-(NSInteger)getObj:(NSDictionary*)dict key:(id)key Default:(NSInteger)Default{
  NSInteger obj = Default;
  id result = [dict objectForKey:key] ;
  if (result != nil) {
    obj = [result integerValue];
  }
  return obj;
}
-(CGFloat)getObj:(NSDictionary*)dict key:(id)key DefaultFloat:(CGFloat)Default{
  CGFloat obj = Default;
  id result = [dict objectForKey:key] ;
  if (result != nil) {
    obj = [result floatValue];
  }
  return obj;
}
@end
