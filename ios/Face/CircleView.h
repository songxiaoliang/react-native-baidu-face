//
//  CircleView.h
//  IDLFaceSDKDemoOC
//
//  Created by Tong,Shasha on 2017/8/31.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <UIKit/UIKit.h>

//#define OutSideColor [UIColor colorWithRed:246/255.0 green:166/255.0 blue:35/255.0 alpha:1]
//#define BackgroundColor [UIColor colorWithRed:47/255.0 green:47/255.0 blue:51/255.0 alpha:1]

#define circleOutSideColorR [[NSUserDefaults standardUserDefaults] integerForKey:@"circleOutSideColorR"]
#define circleOutSideColorG [[NSUserDefaults standardUserDefaults] integerForKey:@"circleOutSideColorG"]
#define circleOutSideColorB [[NSUserDefaults standardUserDefaults] integerForKey:@"circleOutSideColorB"]
#define circleOutSideColorA [[NSUserDefaults standardUserDefaults] integerForKey:@"circleOutSideColorA"]
#define OutSideColor [UIColor colorWithRed: circleOutSideColorR /255.0 green:circleOutSideColorG/255.0 blue:circleOutSideColorB/255.0 alpha:circleOutSideColorA]

#define circleBackgroundColorR [[NSUserDefaults standardUserDefaults] integerForKey:@"circleBackgroundColorR"]
#define circleBackgroundColorG [[NSUserDefaults standardUserDefaults] integerForKey:@"circleBackgroundColorG"]
#define circleBackgroundColorB [[NSUserDefaults standardUserDefaults] integerForKey:@"circleBackgroundColorB"]
#define circleBackgroundColorA [[NSUserDefaults standardUserDefaults] integerForKey:@"circleBackgroundColorA"]
#define BackgroundColor [UIColor colorWithRed: circleBackgroundColorR /255.0 green:circleBackgroundColorG/255.0 blue:circleBackgroundColorB/255.0 alpha:circleBackgroundColorA]

@interface CircleView : UIView

@property (nonatomic, assign) CGRect circleRect;

@property (nonatomic, assign) BOOL conditionStatusFit;

@end
