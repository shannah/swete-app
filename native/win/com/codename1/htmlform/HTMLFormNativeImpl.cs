namespace com.codename1.htmlform
{


    public class HTMLFormNativeImpl : IHTMLFormNativeImpl
    {
        public bool isMainThread()
        {
            //return impl.SilverlightImplementation.dispatcher.HasThreadAccess;
            return com.codename1.ui.Display.getInstance().isEdt();
        }

        public void notifyDispatchQueue()
        {
            //impl.SilverlightImplementation.dispatcher.RunAsync(Windows.UI.Core.CoreDispatcherPriority.Normal, () =>
           // {
           //     HTMLForm.runQueuedEvent();
            //});
            com.codename1.ui.Display.getInstance().callSerially(new RunQueuedEventClass());

        }

        public bool isSupported()
        {
            return true;
        }



    }

    class RunQueuedEventClass : object, java.lang.Runnable
    {
        public void run()
        {
            HTMLForm.runQueuedEvent();
        }
    }
   

   
}
