//
//  ImageUtils.h
//  IDLFaceSDKDemoOC
//
//  Created by 阿凡树 on 2017/5/24.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface ImageUtils : NSObject

+ (CGRect)convertRectFrom:(CGRect)imageRect imageSize:(CGSize)imageSize detectRect:(CGRect)detectRect;

+ (UIImage *)getImageResourceForName:(NSString *)name;

@end
