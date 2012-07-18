/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.fortlewis.portlet.ClassifiedsPortlet.service;

import edu.fortlewis.portlet.ClassifiedsPortlet.domain.Ad;

/**
 *
 * @author Eric Domazlicky
 */
public interface EmailNotification {

    public void SendAdExpireNotification(Ad expired_ad);
    public void SendAdSubmitNotification(Ad submitted_ad);
    public void SendAdminSubmitNotification(Ad submitted_ad);


}
