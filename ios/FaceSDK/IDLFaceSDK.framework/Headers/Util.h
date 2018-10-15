//
//  Util.h
//  IDLFaceSDK
//
//  Created by Tong,Shasha on 2017/5/24.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <UIKit/UIKit.h>


@interface Util : NSObject

+ (CGRect)convertRectFrom:(CGRect)imageRect image:(UIImage *)image detectRect:(CGRect)detectRect;

+ (NSString *)iphoneType;

@end
