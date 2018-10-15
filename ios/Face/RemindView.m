//
//  RemindView.m
//  IDLFaceSDKDemoOC
//
//  Created by Tong,Shasha on 2017/9/5.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import "RemindView.h"
#import "ImageUtils.h"

@implementation RemindView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.layer.borderWidth = 1.0;
        self.layer.borderColor = OutSideColor.CGColor;
        self.layer.cornerRadius = 17;
        
        UIImageView * remindImage = [[UIImageView alloc] initWithFrame:CGRectMake(20, (frame.size.height-27)/2.0, 27, 27)];
        remindImage.image = [ImageUtils getImageResourceForName:@"warning"];
        [self addSubview:remindImage];
        
        UILabel * remindLabel = [[UILabel alloc] initWithFrame:CGRectMake(CGRectGetMaxX(remindImage.frame)+15, CGRectGetMinY(remindImage.frame), 120, CGRectGetHeight(remindImage.frame))];
        remindLabel.textColor = OutSideColor;
        remindLabel.font = [UIFont systemFontOfSize:22];
        remindLabel.text = @"请正对手机";
        [self addSubview:remindLabel];
    }
    return self;
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
