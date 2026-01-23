package com.codescience.salesforceconnect.web;

import com.codescience.salesforceconnect.service.Constants;
import com.codescience.salesforceconnect.service.Messages;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Basic Servlet used to handle OData requests
 */
public class ODataServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ODataServlet.class);

    /**
     * Method handles the OData request and generates a response
     * @param req HttpServletRequest with input Data
     * @param resp HttpServletResponse contains output
     * @throws ServletException Exception thrown if a Servlet Error occurs
     * @throws IOException Exception thrown if a IOException occurs
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Extract the Spring Application Context from the servlet context
            ServletContext servletContext = getServletContext();
            ApplicationContext ctx = (ApplicationContext) servletContext.getAttribute(Constants.APPLICATION_CONTEXT_NAME);

            // Extract the EDM Provider from the Spring application context (see applicationContext.xml)
            CsdlEdmProvider provider = (CsdlEdmProvider) ctx.getBean(Constants.EDM_PROVIDER);

            // create odata handler and configure it with EdmProvider and Processor. Use Spring configuration to request processors
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<>());
            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register((EntityCollectionProcessor) ctx.getBean(Constants.ENTITY_COLLECTION_PROCESSOR));
            handler.register((EntityProcessor) ctx.getBean(Constants.ENTITY_PROCESSOR));

            // let the handler do the work
            handler.process(req, resp);
        } catch (Exception e) {
            LOG.error(Messages.SERVER_ERROR + "{}", e.getMessage(), e);
            throw new ServletException(Messages.SERVER_ERROR + e.getMessage());
        }
    }
}
