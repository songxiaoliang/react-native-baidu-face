//
//  RemindView.h
//  IDLFaceSDKDemoOC
//
//  Created by Tong,Shasha on 2017/9/5.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import <UIKit/UIKit.h>

//#define OutSideColor [UIColor colorWithRed:246/255.0 green:166/255.0 blue:35/255.0 alpha:1]

#define faceTitleColorR [[NSUserDefaults standardUserDefaults] integerForKey:@"faceTitleColorR"]
#define faceTitleColorG [[NSUserDefaults standardUserDefaults] integerForKey:@"faceTitleColorG"]
#define faceTitleColorB [[NSUserDefaults standardUserDefaults] integerForKey:@"faceTitleColorB"]
#define faceTitleColorA [[NSUserDefaults standardUserDefaults] integerForKey:@"faceTitleColorA"]
#define OutSideColor [UIColor colorWithRed: faceTitleColorR /255.0 green:faceTitleColorG/255.0 blue:faceTitleColorB/255.0 alpha:faceTitleColorA]

@interface RemindView : UIView

@end
