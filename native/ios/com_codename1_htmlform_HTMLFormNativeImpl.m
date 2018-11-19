#import "com_codename1_htmlform_HTMLFormNativeImpl.h"
#import "com_codename1_htmlform_HTMLForm.h"

@implementation com_codename1_htmlform_HTMLFormNativeImpl

-(BOOL)isMainThread{
    return [NSThread isMainThread];
}

-(void)notifyDispatchQueue{
   dispatch_async(dispatch_get_main_queue(), ^{
       com_codename1_htmlform_HTMLForm_runQueuedEvent__(CN1_THREAD_GET_STATE_PASS_SINGLE_ARG);
   });
}

-(BOOL)isSupported{
    return YES;
}

@end
