//
//  CircleView.m
//  IDLFaceSDKDemoOC
//
//  Created by Tong,Shasha on 2017/8/31.
//  Copyright © 2017年 Baidu. All rights reserved.
//

#import "CircleView.h"

@implementation CircleView

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
    }
    return self;
}

- (void)setConditionStatusFit:(BOOL)conditionStatusFit
{
    if (_conditionStatusFit != conditionStatusFit) {
        _conditionStatusFit = conditionStatusFit;
        
        __weak typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf setNeedsDisplay];
        });
    }

}

// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetLineWidth(context, 4);
    if (!self.conditionStatusFit) {
        CGContextSetStrokeColorWithColor(context, BackgroundColor.CGColor);
        CGContextAddArc(context, self.circleRect.origin.x+self.circleRect.size.width/2.0, self.circleRect.origin.y+self.circleRect.size.height/2.0, self.circleRect.size.width/2.0-1, 0, 2*M_PI, 0);
        CGContextDrawPath(context, kCGPathStroke);
        
        CGContextSetStrokeColorWithColor(context, OutSideColor.CGColor);
        CGFloat lengths[] = {12,10};
        CGContextSetLineDash(context, 0, lengths, 2);
        CGContextAddArc(context, self.circleRect.origin.x+self.circleRect.size.width/2.0, self.circleRect.origin.y+self.circleRect.size.height/2.0, self.circleRect.size.width/2.0-1, 0, 2*M_PI, 0);
        CGContextDrawPath(context, kCGPathStroke);
    }else {
        CGContextSetStrokeColorWithColor(context, OutSideColor.CGColor);
        CGContextAddArc(context, self.circleRect.origin.x+self.circleRect.size.width/2.0, self.circleRect.origin.y+self.circleRect.size.height/2.0, self.circleRect.size.width/2.0-1, 0, 2*M_PI, 0);
        CGContextDrawPath(context, kCGPathStroke);
    }
}


@end
