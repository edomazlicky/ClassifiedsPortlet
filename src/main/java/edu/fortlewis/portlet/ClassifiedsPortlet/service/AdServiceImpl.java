package edu.fortlewis.portlet.ClassifiedsPortlet.service;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import edu.fortlewis.portlet.ClassifiedsPortlet.domain.Ad;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class AdServiceImpl extends HibernateDaoSupport implements AdService{
	private static Log log = LogFactory.getLog(AdServiceImpl.class);

        @Autowired
        private EmailNotification email_notification;

        public void setEmail_notification(EmailNotification email_notification) {
            this.email_notification = email_notification;
        }

	public void deleteExpiredAds() {
		List<Ad> result = null;
		
		try {
			result = getHibernateTemplate().find("from Ad where endDate < current_timestamp()");
			if (result == null || result.size() == 0) {
				return;
			}
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
		
		if (result.size() > 0) {
			log.info("Ad groomer Deleting "+result.size()+" expired ads.");
			
			try {
				for (Ad ad: result) {
                                        email_notification.SendAdExpireNotification(ad);
					getHibernateTemplate().delete(ad);
				}
				getHibernateTemplate().flush();
			} catch (HibernateException ex) {
				throw convertHibernateAccessException(ex);
			}
		}
		
	}

	public void delete(Long id)
	{
		try {
			Ad ad = (Ad)getHibernateTemplate().load(Ad.class, id);
			getHibernateTemplate().delete(ad);
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
		
	
	public List<Ad> getLatestAds(int nMaxResults) 
	{
		final java.util.Date today = new java.util.Date();
		final java.sql.Date sqlToday = new java.sql.Date(today.getTime());
		
	    final String query = "from Ad where STARTDATE <= ? and ENDDATE >= ? order by startDate desc  ";
	    final int maxResults = nMaxResults;
	    
	    HibernateCallback callback = new HibernateCallback() 
	    {
	        public Object doInHibernate(Session session) 
	        throws HibernateException,SQLException {
	        	return session.createQuery(query)
	        	.setDate(0, sqlToday)
	        	.setDate(1, sqlToday)
	        	.setMaxResults(maxResults)
	        	.list();
	        }
	    };
	    
	   return (List<Ad>) getHibernateTemplate().execute(callback);
	}
	

	public List<Ad> getAdsByUserId(String userid)
	{
		try {
			return (List<Ad>) getHibernateTemplate().find("from Ad where USERID = ? order by startDate desc ", userid);
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
	
	public List<Ad> getAd(Long id)
	{ 
		try {
			return (List<Ad>) getHibernateTemplate().find(" from Ad where ID = ? ", id);
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
		
	public void processAd(Ad ad) {
		try {
			getHibernateTemplate().saveOrUpdate(ad);
			getHibernateTemplate().flush();
                        email_notification.SendAdSubmitNotification(ad);
                        email_notification.SendAdminSubmitNotification(ad);
		} catch (HibernateException ex) {
			throw convertHibernateAccessException(ex);
		}
	}
}
