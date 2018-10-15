//
//  ImageUtils.m
//  IDLFaceSDKDemoOC
//
//  Created by 阿凡树 on 2017/5/24.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import "ImageUtils.h"
#import <CoreGraphics/CoreGraphics.h>

@implementation ImageUtils

+ (CGRect)convertRectFrom:(CGRect)imageRect imageSize:(CGSize)imageSize detectRect:(CGRect)detectRect {
    CGPoint imageTopLeft = imageRect.origin;
    CGPoint imageBottomRight = CGPointMake(CGRectGetMaxX(imageRect),CGRectGetMaxY(imageRect));
    
    CGPoint viewTopLeft = [self convertPointFrom:imageTopLeft imageSize:imageSize detectSize:detectRect.size];
    CGPoint viewBottomRight = [self convertPointFrom:imageBottomRight imageSize:imageSize detectSize:detectRect.size];
    
    return CGRectMake(viewTopLeft.x, viewTopLeft.y, viewBottomRight.x - viewTopLeft.x, viewBottomRight.y - viewTopLeft.y);
}

+ (CGPoint)convertPointFrom:(CGPoint)imagePoint imageSize:(CGSize)imageSize detectSize:(CGSize)detectSize {
    CGPoint viewPoint = imagePoint;
    CGFloat scale = MAX(detectSize.width / imageSize.width, detectSize.height / imageSize.height);
    viewPoint.x *= scale;
    viewPoint.y *= scale;
    viewPoint.x += (detectSize.width - imageSize.width * scale) / 2.0;
    viewPoint.y += (detectSize.height - imageSize.height * scale) / 2.0;
    return viewPoint;
}



+ (UIImage *)getImageResourceForName:(NSString *)name {
    NSString* bundlepath = [[NSBundle mainBundle] pathForResource:@"com.baidu.idl.face.faceSDK.bundle" ofType:nil];
    NSBundle* modelBundle = [NSBundle bundleWithPath:bundlepath];
    NSString* imagePath = [modelBundle pathForResource:name ofType:@"png"];
    return [[UIImage alloc] initWithContentsOfFile:imagePath];
}

@end
