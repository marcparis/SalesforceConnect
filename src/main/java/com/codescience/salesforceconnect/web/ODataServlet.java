package com.codescience.salesforceconnect.web;

import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Basic Servlet used to handle Odata requests
 */
public class ODataServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(ODataServlet.class);
    private static final String EDM_PROVIDER = "edmProvider";
    private static final String ENTITY_PROCESSOR = "entityProcessor";
    private static final String ENTITY_COLLECTION_PROCESSOR = "entityCollectionProcessor";
    private static final String APPLICATION_CONTEXT_NAME = "org.springframework.web.context.WebApplicationContext.ROOT";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // Extract the Spring Application Context from the servlet context
            ServletContext srvctx = getServletContext();
            ApplicationContext ctx = (ApplicationContext) srvctx.getAttribute(APPLICATION_CONTEXT_NAME);

            // Extract the EDM Provider from the Spring application context (see applicationContext.xml)
            CsdlEdmProvider provider = (CsdlEdmProvider) ctx.getBean(EDM_PROVIDER);

            // create odata handler and configure it with EdmProvider and Processor. Use Spring configuration to request processors
            OData odata = OData.newInstance();
            ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register((EntityCollectionProcessor) ctx.getBean(ENTITY_COLLECTION_PROCESSOR));
            handler.register((EntityProcessor) ctx.getBean(ENTITY_PROCESSOR));

            // let the handler do the work
            handler.process(req, resp);
        } catch (RuntimeException e) {
            LOG.error("Server Error occurred in ODataServlet", e);
            throw new ServletException(e);
        }
    }
}
