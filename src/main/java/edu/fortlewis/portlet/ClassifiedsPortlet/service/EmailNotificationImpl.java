package edu.fortlewis.portlet.ClassifiedsPortlet.service;

/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 *
 * @author Eric Domazlicky
 */
import edu.fortlewis.portlet.ClassifiedsPortlet.domain.Ad;
import java.text.DateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.properties.PropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;


public class EmailNotificationImpl implements EmailNotification {

    @Autowired
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    private final Log log = LogFactory.getLog(this.getClass());

    public void SendAdExpireNotification(Ad expired_ad) {
        String[] useremail = {expired_ad.getUserEmail()};
        SendNotification(expired_ad,useremail,"notifyUserOnAdExpire","AdExpireSubject","AdExpireTemplate");
    }

    public void SendAdSubmitNotification(Ad submitted_ad) {
        String[] useremail = {submitted_ad.getUserEmail()};
        SendNotification(submitted_ad,useremail,"notifyUserOnAdSubmit","AdSubmitSubject","AdSubmitTemplate");
    }

    public void SendAdminSubmitNotification(Ad submitted_ad) {
        SendNotification(submitted_ad,GetEmailsFromProperty("AdminEmail"),"notifyAdminOnAdSubmit","AdminSubmitSubject","AdminSubmitTemplate");
    }

    private void SendNotification(Ad ad,String[] toaddresses,String notifyOnProp,String notifySubjectProp,String notifyTemplateProp)
    {
        if(PropertiesManager.getPropertyAsBoolean(notifyOnProp, false))
        {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(PropertiesManager.getProperty("fromAddress"));                                
                message.setTo(toaddresses);
                message.setSubject(ReplaceTemplateVariables(PropertiesManager.getProperty(notifySubjectProp),ad));
                message.setText(ReplaceTemplateVariables(PropertiesManager.getProperty(notifyTemplateProp),ad));
                mailSender.send(message);

            }
            catch(Exception ex)
            {
                log.error("Error sending notification e-mail:"+ex.getMessage());
            }
        }
    }

    private String[] GetEmailsFromProperty(String propname)
    {
        String propvalue = PropertiesManager.getProperty(propname);
        if(propvalue.contains(","))
        {
            String[] propvalues = propvalue.split(",");
            return propvalues;
        }
        else {
            String retvalue[] = {propvalue};
            return retvalue;
        }
    }

    private String ReplaceTemplateVariables(String template,Ad ad)
    {
        String retvalue = template;
        retvalue = retvalue.replaceAll("%Username%", ad.getUserid());
        retvalue = retvalue.replaceAll("%AdTitle%", ad.getTitle());
        retvalue = retvalue.replaceAll("%AdPrice%", ad.getPrice());
        retvalue = retvalue.replaceAll("%EndDate%", DateFormat.getTimeInstance(DateFormat.MEDIUM).format(ad.getEndDate()));
        retvalue = retvalue.replaceAll("%CreateDate%", DateFormat.getTimeInstance(DateFormat.MEDIUM).format(ad.getCreateDate()));
        retvalue = retvalue.replaceAll("%AdDescription%", ad.getDescription());
        return retvalue;
    }



}
