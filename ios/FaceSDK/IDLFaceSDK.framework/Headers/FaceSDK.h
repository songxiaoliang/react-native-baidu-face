//
//  FaceSDK.h
//  FaceSDK
//
//  Created by Nick(xuli02) on 15/3/31.
//  Copyright (c) 2015年 Baidu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

typedef enum {
    FaceSDKImgTypeYUV,
    FaceSDKImgTypeRGB,
} FaceSDKImageType;

typedef enum {
    FaceSDKMethodTypeSDM = 0,
    FaceSDKMethodTypeCDNN = 1,
    FaceSDKMethodTypeSDM_7PTS = 2,
    FaceSDKMethodTypeSDM_15PTS = 3,
} FaceSDKMethodType;

typedef enum {
    FaceSDKDetectMethodType_BOOST,
    FaceSDKDetectMethodType_CNN,
    FaceSDKDetectMethodType_NIR
} FaceSDKDetectMethodType;

typedef enum {
    FaceSDKParsingModelType_NOT_USE = 0,
    FaceSDKParsingModelType_CLASS_NUM_3 = 1,
    FaceSDKParsingModelType_CLASS_NUM_7 = 2,
    FaceSDKParsingModelType_CLASS_NUM_10 = 3,
} FaceSDKParsingModelType;

typedef enum {
    QualitySDKModelType_NOT_USE,
    QualitySDKModelType_BLUR,
    QualitySDKModelType_OCCLUSION,
} QualitySDKModelType;

typedef enum {
    LicenseSuccess = 0,
    LicenseInitError,
    LicenseDecryptError,
    LicenseInfoFormatError,
    LicenseExpired,
    LicenseMissRequiredInfo,
    LicenseInfoCheckError,
    LicenseLocalFileError,
    LicenseRemoteDataError
} FaceLicenseErrorCode;


NS_ASSUME_NONNULL_BEGIN
@interface LivenessState : NSObject
/** 眼状态 */
@property(nonatomic, assign) BOOL isLiveEye;
@property(nonatomic, assign) NSInteger leftEyeStatus;
@property(nonatomic, assign) NSInteger rightEyeStatus;
/** 嘴状态 */
@property(nonatomic, assign) BOOL isLiveMouth;
@property(nonatomic, assign) NSInteger mouthStatus;
/** 3D info*/
@property(nonatomic, assign) BOOL isLiveYawLeft;
@property(nonatomic, assign) BOOL isLiveYawRight;
@property(nonatomic, assign) NSInteger yawAngles;

@property(nonatomic, assign) BOOL isLivePitchUp;
@property(nonatomic, assign) BOOL isLivePitchDown;
@property(nonatomic, assign) NSInteger pitchAngles;

@end

@interface FaceSDK : NSObject
/**
 *  初始化方法
 *
 *  @return 实例
 */
+ (FaceSDK * _Nullable)sharedInstance;

/**
 *  鉴权方法
 *  SDK鉴权方法 必须在使用其他方法之前设置，否则会导致SDK不可用
 *  @param licenseId 鉴权api key
 *  @param localLicencePath 本地鉴权文件路径
 */
- (void)initWithLicenseID:(NSString *)licenseId andLocalLicenceFile:(NSString *)localLicencePath;

- (void)initWithToken:(NSString *)token;

/**
 *  开启网络鉴权
 *  默认开启
 *
 *  @param remoteAuthorize 是否开启网络鉴权
 */
- (void)setRemoteAuthorize:(BOOL)remoteAuthorize;

/**
 *  SDK是否可用
 *  如果可用返回结果为0
 *
 *  @return SDK可用结果
 */
- (FaceLicenseErrorCode)canWork;

/**
 *  人脸识别
 *
 *  @param image            图像
 *  @param methodType       识别算法模型 CNN / NIR
 *  @param faceRects        output人脸框的位置
 *  @param faceNumber       output识别人脸的数目
 *  @param minSize          最小人脸size
 */
- (void)detectFaceInImage:(UIImage *)image
           withMethodType:(FaceSDKDetectMethodType)methodType
          andGetFaceRects:( NSArray *_Nullable*_Nullable)faceRects
            andFaceNumber:(NSInteger *)faceNumber
       andMinimumFaceSize:(NSInteger)minSize;

/**
 *  人脸对齐
 *
 *  @param image            图像
 *  @param methodType       识别算法模型 SDM / CDNN
 *  @param faceRect         人脸位置
 *  @param faceShape        output人脸特征点
 *  @param numberOfPoint    output人脸特征点个数
 *  @param score            output人脸置信度
 */
- (void)alignFaceInImage:(UIImage *)image
          withMethodType:(FaceSDKMethodType)methodType
             andFaceRect:(CGRect)faceRect
      andOutputFaceShape:( NSArray *_Nullable*_Nullable)faceShape
           andNumOfPoints:(NSInteger *)numberOfPoint
                andScore:(CGFloat *)score;

/**
 *  人脸精确对齐
 *
 *  @param image            图像
 *  @param methodType       识别算法模型 SDM / CDNN
 *  @param faceShape        input/output人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 */
- (void)fineAlignFaceInImage:(UIImage *)image
              withMethodType:(FaceSDKMethodType)methodType
                andFaceShape:(NSArray *_Nullable*_Nullable)faceShape
              andNumOfPoints:(NSInteger)numberOfPoints;

/**
 *  人脸追踪
 *
 *  @param image            图像
 *  @param methodType       识别算法模型 SDM / CDNN
 *  @param faceShape        input and output人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 *  @param score            output置信度
 */
- (void)trackFaceInImage:(UIImage *)image
          withMethodType:(FaceSDKMethodType)methodType
            andFaceShape:( NSArray *_Nullable*_Nullable)faceShape
          andNumOfPoints:(NSInteger )numberOfPoints
                andScore:(CGFloat *)score;

/**
 *  获取头部姿态(1)
 *
 *  @param faceShape        input and output 人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 *  @param rows             图像宽
 *  @param colums           图像高
 *
 *  @return                 头部姿态 pitch,yaw,roll
 */

- (NSDictionary *)headPostEstimationWithFaceShape:(NSArray *)faceShape
                                 andImgDataRowNum:(NSInteger)rows
                                   andImgColumNum:(NSInteger)colums
                                   andNumOfPoints:(NSInteger )numberOfPoints;

/**
 *  人脸解析
 *
 *  @param image            图像
 *  @param modelType        解析模式：outline / detail features
 *  @param faceShape        人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 *
 *  @return
 */
- (unsigned char *)parsingFaceInImage:(UIImage *)image
                 withParsingModelType:(FaceSDKParsingModelType)modelType
                         andFaceShape:(NSArray *)faceShape
                       andNumOfPoints:(NSInteger )numberOfPoints;

/**
 *  获取头部姿态(1)
 *
 *  @param image            图像
 *  @param faceShape        input and output 人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 *
 *  @return                 头部姿态 pitch,yaw,roll
 */
- (NSDictionary *)headPostEstimationWithImage:(UIImage *)image
                                 andFaceShape:(NSArray *)faceShape
                               andNumOfPoints:(NSInteger )numberOfPoints;



/**
 *  活体检测
 *
 *  @param image            图像
 *  @param faceShape        input and output 人脸特征点
 *  @param numberOfPoints   人脸特征点个数
 *  @param flag             如果track失败传入0 其他传入1
 *  @param state            output 活体
 */

- (void)livenessDetectInImage:(UIImage *)image
                 andFaceShape:( NSArray *_Nullable*_Nullable)faceShape
               andNumOfPoints:(NSInteger )numberOfPoints
                      andFlag:(NSInteger)flag
               andOutPutState:(LivenessState *)state;


/**
 *  图片网纹处理
 *
 *  @param image          图像
 *  @param width          图片处理后的宽
 *  @param height         图片处理后的高
 *  @param outImage       output处理后的图片
 */
- (void)removeTextureWith:(UIImage *)image
            outImageWidth:(NSInteger *_Nullable)width
           outImageHeight:(NSInteger *_Nullable)height
                 outImage:(UIImage *_Nullable*_Nullable)outImage;

/**
 *  图片超分辨率处理（芯片照预处理）
 *
 *  @param image          图像
 *  @param width          图片处理后的宽
 *  @param height         图片处理后的高
 *  @param outImage       output处理后的图片
 */
- (void)superResolutionWith:(UIImage *)image
              outImageWidth:(NSInteger *_Nullable)width
             outImageHeight:(NSInteger *_Nullable)height
                   outImage:(UIImage *_Nullable*_Nullable)outImage;

//~*~*~*~*~*~*~*~*~*~**~**~*~**~*~**~**~**~**~~*~*~*~*~*~*~*~*~*~**~**~*~**~*~**~**~**~**~
#pragma mark - Utils

/**
 *  根据特征点获取人脸各部位结构 (目前仅适用于72个特征点的模型)
 *
 *  @param faceShape 人脸特征点
 *
 *  @return 人脸结构

 *  {0,1,2,3,4,5,6,7,8,9,10,11,12};         //contour
 *  {13,14,15,16,17,18,19,20,13,21};        //left eye
 *  {22,23,24,25,26,27,28,29,22};           //left eye brow
 *  {30,31,32,33,34,35,36,37,30,38};        //right eye
 *  {39,40,41,42,43,44,45,46,39};           //right eye brow
 *  {47,48,49,50,51,52,53,54,55,56,47};     //nose
 *  {51,57,52}; //nose tip
 *  {58,59,60,61,62,63,64,65,58};           //lips outer contour
 *  {58,66,67,68,62,69,70,71,58};           //lips inner contour
 *
 */
- (NSDictionary *)faceDetailFromFaceShapeArray:(NSArray *)faceShape;



/**
 *  静默活体检测
 *
 *  @param image            图像
 *  @param shapePoints      人脸特征点
 */

- (CGFloat)livenessSilentDetectInImage:(UIImage *)image
                             faceShape:(NSArray *_Nullable*_Nullable)shapePoints;

@end

#pragma mark - FaceVerifier

typedef enum {
    FaceVerifierActionTypeDelete = 0,
    FaceVerifierActionTypeRegister,
    FaceVerifierActionTypeVerify,
    FaceVerifierActionTypeRecognition,
    FaceVerifierActionTypeCheckName,
} FaceVerifierActionType;

typedef enum {
    ErrorCodeOK,
    ErrorCodePitchOutofDownRange,    // 头部偏低
    ErrorCodePitchOutofUpRange,      // 头部偏高
    ErrorCodeYawOutofLeftRange,      // 头部偏左
    ErrorCodeYawOutofRightRange,     // 头部偏右
    ErrorCodePoorIllumination,       // 光照不足
    ErrorCodeNoFaceDetected,         // 没有检测到人脸
    ErrorCodeDataNotReady,           // 数据没有准备好
    ErrorCodeDataHitOne,             // 采集到一张图片
    ErrorCodeDataHitLast,            // 采集完最后一张
    ErrorCodeImgBlured,              // 图像模糊
    ErrorCodeOcclusionLeftEye,       // 左眼有遮挡
    ErrorCodeOcclusionRightEye,      // 右眼有遮挡
    ErrorCodeOcclusionNose,          // 鼻子有遮挡
    ErrorCodeOcclusionMouth,         // 嘴巴有遮挡
    ErrorCodeOcclusionLeftContour,   // 左脸颊有遮挡
    ErrorCodeOcclusionRightContour,  // 右脸颊有遮挡
    ErrorCodeOcclusionChinCoutour,   // 下颚有遮挡
    ErrorCodeFaceNotComplete,        // 没有完成
    ErrorCodeUnknowType              // 未知错误
} FaceVerifierErrorCode;

@interface FaceVerifier : NSObject
/**
 *  初始化方法
 *
 *  @return 实例
 */
+ (FaceVerifier * _Nullable)sharedInstance;

/**
 *  SDK鉴权方法
 *  SDK鉴权方法 必须在使用其他方法之前设置，否则会导致SDK不可用
 *
 *  @param licenseId 鉴权api key
 *  @param localLicencePath 本地鉴权文件路径
 */
- (void)setLicenseID:(NSString *)licenseId andLocalLicenceFile:(NSString *)localLicencePath;

- (void)initWithToken:(NSString *)token;

/**
 *  开启网络鉴权
 *  默认开启
 *
 *  @param remoteAuthorize 是否开启网络鉴权
 */
- (void)setRemoteAuthorize:(BOOL)remoteAuthorize;

/**
 *  SDK是否可用
 *  如果可用返回结果为0
 *
 *  @return SDK可用结果
 */
- (FaceLicenseErrorCode)canWork;

/**
 *  设置追踪最小人脸尺寸
 *  默认100
 *
 *  @param size 人脸尺寸
 */
- (void)setMinFaceSize:(NSInteger)size;

- (void)setIfNeedFineAlign:(BOOL)isFineAlign;

/**
 *  设置是否检测活体
 *  默认false
 *
 *  @param isVerifyLife 否检测活体
 */
- (void)setIsVerifyLive:(BOOL)isVerifyLife;

/**
 *  设置追踪目标时间间隔
 *  默认500ms
 *
 *  @param interval 时间间隔
 */
- (void)setTrackByDetectionInterval:(NSTimeInterval)interval;

/**
 *  设置未跟踪目标时的时间间隔
 *  默认1000ms
 *
 *  @param interval 时间间隔
 */
- (void)setDetectInVideoInterval:(NSTimeInterval)interval;

/**
 *  设置抠图大小
 *  默认256像素
 *
 *  @param width 抠图大小
 */
- (void)setCropFaceSizeWithWidth:(CGFloat)width;

/**
 *  设置非人脸的置信度阈值
 *  默认0.5 取值范围0 ~ 1
 *
 *  @param thr 置信度阈值
 */
- (void)setNotFaceThr:(CGFloat)thr;

/**
 *  设置遮挡阈值
 *  默认0.5 取值范围0 ~ 1
 *
 *  @param thr 遮挡阈值
 */
- (void)setOccluThr:(CGFloat)thr;

/**
 *  设置光照阈值
 *  默认40 取值范围0 ~ 255
 *
 *  @param thr 光照阈值
 */
- (void)setIllumThr:(NSInteger)thr;

/**
 *  设置模糊阈值
 *  默认0.7 取值范围0 ~ 1
 *  @param thr 模糊阈值
 */
- (void)setBlurThr:(CGFloat)thr;

/**
 *  设置Align模型类型
 *  默认CDNN
 *
 *  @param type 模型类型
 */
- (void)setAlignMethodType:(NSInteger)type;

/**
 *  设置Detect模型类型
 *  默认CNN
 *
 *  @param type 模型类型
 */
- (void)setDetectMethodType:(NSInteger)type;

/**
 *  设置3D姿态角
 *  默认15°
 *
 *  @param degree pitch
 *  @param yawDegree yaw
 *  @param rollDegree roll
 */
- (void)setEulurAngleThrPitch:(NSInteger)degree
                          yaw:(NSInteger)yawDegree
                         roll:(NSInteger)rollDegree;

/**
 *  设置人脸抠图张数
 *  默认3张 最多三张
 *
 *  @param imageNum 抠图数量
 */
- (void)setMaxRegImgNum:(NSInteger)imageNum;

/**
 *  质量检测
 *  默认false
 *
 *  @param flag 是否执行质量检测
 */
- (void)setIsCheckQuality:(BOOL)flag;

/**
 *  设置抠图时截取的人脸大小倍数
 *  默认3.0
 *
 *  @param thr 人脸大小倍数
 */
- (void)setCropFaceEnlargeRatio:(CGFloat)thr;

/**
 *  获取人脸抠图大小 以像素为单位
 *
 *  @return 抠图大小
 */
- (NSInteger)getCropFaceSize;

/**
 *  获取追踪到的人脸
 *
 *  @return 追踪到的人脸数组
 */
- (NSArray *)getTrackedFace;

/**
 *  获取人脸抠图
 *
 *  @return 人脸抠图数据集
 */
- (NSArray *)regImages;

/** 是否正在tracking */
- (BOOL)isUnderTrack;

/** 人脸特征点集合 */
- (NSArray *)faceShapes;

/** 清除追踪人脸数据*/
- (void)clearTrackedFaces;

/** 活体检测状态,仅对单人模式有效(maxfacecount = 1) */
- (LivenessState *)livenessState;

/** 注册用的HttpString仅对单人模式有效(maxfacecount = 1) */
- (NSString *)httpString;

/** errorcode for the process of preparing data */
- (NSInteger)errorCode;

/**
 *  多人脸追踪
 *
 *  @param image 图像
 *  @param count 追踪人脸数目
 */
- (void)trackWithImage:(UIImage *)image andMaxFaceCount:(NSUInteger)count;

/**
 *  人脸追踪识别
 *
 *  @param image          图像
 *  @param actionType     识别模式
 *  @return               人脸识别结果
 */
- (FaceVerifierErrorCode)prepareDataWithImage:(UIImage *)image
                                andActionType:(FaceVerifierActionType)actionType;

/**
 *  最大人脸追踪识别
 *
 *  @param image          图像
 *  @param actionType     识别模式
 *  @return               最大人脸识别结果
 */
- (FaceVerifierErrorCode)prepareDataForMaxFaceWithImage:(UIImage *)image
                                          andActionType:(FaceVerifierActionType)actionType;

/**
 *  人脸质量检测
 *
 * @param image           图像
 * @param faceShape       人脸特征点
 * @param numberOfPoints  人脸特征点个数
 * @param bluriness       output人脸模糊值
 * @param illum           output光照值
 * @param occlusion       output人脸部位
 * @param nOccluPart      output人脸部位数量
 */
- (void)imageQualityWith:(UIImage *)image
            andFaceShape:(NSArray *_Nullable*_Nullable)faceShape
          andNumOfPoints:(NSInteger)numberOfPoints
               bluriness:(CGFloat *)bluriness
                   illum:(NSInteger *)illum
               occlusion:(NSArray *_Nullable*_Nullable)occlusion
              nOccluPart:(NSInteger *)nOccluPart;

/**
 *  人脸抠图
 *
 *  @param image          图像
 *  @param faceShape      人脸特征点
 *  @param numberOfPoints 人脸特征点数目
 *  @param width          抠图宽
 *  @param height         抠图高
 *  @param cropImage      output抠图结果图片
 *  @param cropShaps      output抠图结果图片特征点
 */
- (void)cropFaceImageWith:(UIImage *)image
                FaceShape:(NSArray *_Nullable*_Nullable)faceShape
              numOfPoints:(NSInteger)numberOfPoints
           faceImageWidth:(NSInteger)width
          faceImageHeight:(NSInteger)height
                cropImage:(UIImage *_Nullable*_Nullable)cropImage
                cropShaps:(NSArray *_Nullable*_Nullable)cropShaps;

/**
 *  开启日志打印
 *  默认关闭
 *
 *  @param visible 是否开启日志打印
 */
- (void)setLogVisible:(BOOL)visible;

/**
 *  获取SDK版本
 *
 *  @return SDK当前版本
 */
- (NSString *)getVersion;

@end

@interface TrackedFaceInfoModel : NSObject
/** 人脸属性 */
@property(nonatomic, assign) CGRect faceRect;
@property(nonatomic, assign) NSInteger faceId;
@property(nonatomic, assign) CGFloat score;
@property(nonatomic, assign) BOOL isDetect;
@property(nonatomic, assign) NSInteger angle;
@property(nonatomic, assign) CGFloat conf;
@property(nonatomic, copy) NSArray *landMarks;
@property(nonatomic, copy) NSArray *headPose;
@property(nonatomic, copy) NSArray *isLive;
@property(nonatomic, copy) NSArray *regImage;
@property(nonatomic, copy) NSArray *regLandmarks;
@property(nonatomic, copy) NSArray *imgDigest;

@end

NS_ASSUME_NONNULL_END

