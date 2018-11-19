/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.services;

import ca.weblite.swete.models.WebSite;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;

/**
 *
 * @author shannah
 */
public class CapturePageCrawler extends AbstractPageCrawler {
    
    public CapturePageCrawler(WebSite site) {
        super(site);
    }

    @Override
    protected RequestResult sendRequest(String url) {
        Log.p("Sending request for url "+url);
        return new RequestResult(client.requestPageWithCapturing(url).getResponseCode());
    }

    
}
