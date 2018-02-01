package org.osivia.onlyoffice.rest;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;
import org.osivia.onlyoffice.listener.OnlyofficeSaveDocumentListener;

import fr.toutatice.ecm.platform.core.edition.CurrentlyEditedCacheHelper;
import fr.toutatice.ecm.platform.core.edition.TemporaryLockedCacheHelper;

@Path("/onlyoffice")
@Produces("application/json")
@Consumes("application/json")
@WebObject(type = "OnlyOffice")
public class OnlyofficeRestService extends ModuleRoot {

    private final EventProducer eventProducer;

    public OnlyofficeRestService() {
        eventProducer = Framework.getService(EventProducer.class);
    }

    @Path("callbackEdit/{docId}")
    @POST
    public Object callbackEdit(@PathParam("docId") final String docId, OnlyofficeCallback onlyofficeCallback) {

        CoreSession session = getSession();
        DocumentModel documentModel = session.getDocument(new IdRef(docId));

        Principal principal = session.getPrincipal();
        DocumentEventContext eventCtx = new DocumentEventContext(session, principal, documentModel);

        int status = onlyofficeCallback.getStatus();

        Map<String, Serializable> properties = new HashMap<>();

        properties.put(OnlyofficeSaveDocumentListener.ONLYOFFICE_CALLBACK_STATUS_PROPERTY, onlyofficeCallback.getStatus());
        properties.put(OnlyofficeSaveDocumentListener.ONLYOFFICE_CALLBACK_URL_PROPERTY, onlyofficeCallback.getUrl());
        eventCtx.setProperties(properties);

        switch (status) {
            case 0:
                // 0 - no document with the key identifier could be found
                CurrentlyEditedCacheHelper.invalidate(documentModel);
                
                removeLockIfExists(session, documentModel);
                break;
            case 1:
                // 1 - document is being edited,
                CurrentlyEditedCacheHelper.put(documentModel, onlyofficeCallback.getUsers());
                break;
            case 2:
                // 2 - document is ready for saving,
                eventProducer.fireEvent(eventCtx.newEvent(OnlyofficeSaveDocumentListener.ONLYOFFICE_SAVEDOCUMENT_EVENT_NAME));
                break;
            case 3:
                // 3 - document saving error has occurred
                CurrentlyEditedCacheHelper.invalidate(documentModel);
                removeLockIfExists(session, documentModel);
            case 4:
                // 4 - document is closed with no changes
                CurrentlyEditedCacheHelper.invalidate(documentModel);
                removeLockIfExists(session, documentModel);
            case 5:
            case 6:
                // 6 - document is being edited, but the current document state is saved,
                break;
            case 7:
                // 7 - error has occurred while force saving the document.
                // eventProducer.fireEvent(eventCtx.newEvent(OnlyofficeSaveDocumentListener.ONLYOFFICE_SAVEDOCUMENT_EVENT_NAME));
                break;
            default:
                break;
        }

        return "{\"error\":0}";
    }

	private void removeLockIfExists(CoreSession session, DocumentModel doc) {
		if(TemporaryLockedCacheHelper.get(doc) != null) {
			session.removeLock(doc.getRef());
			TemporaryLockedCacheHelper.invalidate(doc);
			
		}
	}

    @Path("callbackCoEdit/{docId}")
    @POST
    public Object callbackCoEdit(@PathParam("docId") final String docId) {
        return docId;
        // return getOnlyOfficeService().trackDocument(SessionFactory.getSession(), docId, getBody(), true, false);
    }

    private DocumentModel getDocument(String docId) {
        return getSession().getDocument(new IdRef(docId));
    }

    private CoreSession getSession() {
        return SessionFactory.getSession();
    }
}
