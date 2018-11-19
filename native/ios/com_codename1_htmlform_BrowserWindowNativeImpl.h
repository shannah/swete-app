#import <Foundation/Foundation.h>

@interface com_codename1_htmlform_BrowserWindowNativeImpl : NSObject {
}

-(void)execute:(NSString*)param;
-(void)addJSCallback:(NSString*)param param1:(int)param1;
-(void)removeWindowResizeListener:(int)param;
-(void)addWebEventListener:(NSString*)param param1:(int)param1;
-(void)addWindowResizeListener:(int)param;
-(int)getInternalId;
-(void)initBrowserWindowNative:(int)param;
-(void)removeJSCallback:(int)param;
-(void)setDebugMode:(BOOL)param;
-(void)setSize:(int)param param1:(int)param1;
-(int)getX;
-(int)getY;
-(void)putClientProperty:(NSString*)param param1:(int)param1;
-(void)setVisible:(BOOL)param;
-(void)setPosition:(int)param param1:(int)param1;
-(int)getWidth;
-(int)getHeight;
-(void)addWindowListener:(int)param;
-(void)removeWindowListener:(int)param;
-(NSString*)getTitle;
-(void)setTitle:(NSString*)param;
-(void)setURLHierarchy:(NSString*)param;
-(void)setFireCallbacksOnEdt:(BOOL)param;
-(NSString*)executeAndReturnString:(NSString*)param;
-(BOOL)isSupported;
@end
