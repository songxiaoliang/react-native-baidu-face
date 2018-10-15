//
//  FaceParameterConfig.h
//  FacePrint
//
//  Created by 阿凡树 on 2017/9/20.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#ifndef FaceParameterConfig_h
#define FaceParameterConfig_h

// 如果在后台选择自动配置授权信息，下面的三个LICENSE相关的参数已经配置好了
// 只需配置FACE_API_KEY和FACE_SECRET_KEY两个参数即可

// 人脸license文件名
#define FACE_LICENSE_NAME    @"idl-license"

// 人脸license后缀
#define FACE_LICENSE_SUFFIX  @"face-ios"

// （您申请的应用名称(appname)+「-face-ios」后缀，如申请的应用名称(appname)为test123，则此处填写test123-face-ios）
// 在后台 -> 产品服务 -> 人脸识别 -> 客户端SDK管理查看，如果没有的话就新建一个
#define FACE_LICENSE_ID @"com-face-faceTest-face-ios"

#endif /* FaceParameterConfig_h */
