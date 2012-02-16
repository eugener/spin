package org.oxbow.spin.spring

import org.apache.commons.logging.LogFactory
import org.springframework.web.context.support.WebApplicationContextUtils
import org.springframework.web.context.WebApplicationContext

import com.vaadin.terminal.gwt.server.AbstractApplicationServlet
import com.vaadin.Application

import javax.servlet.http.HttpServletRequest
import javax.servlet.ServletConfig
import javax.servlet.ServletException

/**
 * The servlet assumes that Vaadin application is configured as a bean in the 
 * Spring application context. The application bean id has to be passed through  
 * with "applicationBean" init-parameter, configured in web.xml file
 */
class SpringVaadinAppServlet extends AbstractApplicationServlet {

    private lazy val logger = LogFactory.getLog( classOf[SpringVaadinAppServlet] );
    
    private var appBean: String = null
    private var appContext: WebApplicationContext = null
	private var appClass: Class[Application] = null
    
    @throws(classOf[ServletException])
	override def init( servletConfig: ServletConfig ): Unit = {
        
        if (logger.isDebugEnabled) logger.debug("init")
		
		super.init(servletConfig)
		appBean = servletConfig.getInitParameter("applicationBean")
		if (appBean == null) {
			throw new ServletException("Application bean not specified in servlet parameters")
		}
		appContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext)
		appClass = appContext.getType(appBean).asInstanceOf[Class[Application]]
        
    }
    
    @throws(classOf[ServletException])
    override protected def getNewApplication( request: HttpServletRequest ): Application = {
		if (logger.isTraceEnabled) logger.trace("getNewApplication")
		return appContext.getBean(appBean).asInstanceOf[Application]
	}

    @throws(classOf[ServletException])
	override protected def getApplicationClass: Class[Application] = {
		if (logger.isTraceEnabled) logger.trace("getApplicationClass")
		return appClass
	}
    
    
}