/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.weblite.swete.util;

import ca.weblite.swete.forms.PreviewForm;
import ca.weblite.swete.models.Snapshot;
import ca.weblite.swete.models.WebSite;

/**
 *
 * @author shannah
 */
public class Dispatcher {
    public static void previewPage(WebSite website, Snapshot snapshot, String url) {
        new PreviewForm(website, snapshot, website.getProxyUrlForPage(url)).show();
    }
}
