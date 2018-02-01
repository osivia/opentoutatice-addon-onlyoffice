/**
 * 
 */
package org.osivia.onlyoffice.automation;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import fr.toutatice.ecm.platform.core.edition.EditionCacheHelper;
import fr.toutatice.ecm.platform.core.edition.TemporaryLockedCacheHelper;

/**
 * Lock a document before editing a document.
 * The lock is removed when onlyoffice edition is done.
 * 
 * @author Loïc Billon
 *
 */
@Operation(
		id = LockTemporary.ID,
		category = Constants.CAT_DOCUMENT,
		label = "Lock a document before editing a document.",
		description = "Lock a document before editing a document, the lock is removed when onlyoffice edition is done.")
public class LockTemporary {

	public final static String ID = "Document.LockTemporary";
	
    private static final Log log = LogFactory.getLog(LockTemporary.class);
    /**
     * Session.
     */
    @Context
    protected CoreSession coreSession;
    
    /**
     * Identifiant uuid) du document en entrée.
     */
    @Param(name = "id")
    protected DocumentModel currentDocument;

    @OperationMethod
    public Object run() throws Exception {
    	
    	JSONArray json  = new JSONArray();
    	Map<String, Object> infos = new HashMap<String, Object>();
    	
    	try {
    		coreSession.setLock(currentDocument.getRef());
    		EditionCacheHelper.put(currentDocument, coreSession.getPrincipal().getName(), TemporaryLockedCacheHelper.TEMPORARY_LOCKED_CACHE_NAME);
    		
    		
            infos.put("error", "0");
    	}
    	catch (ClientException e) {
    		infos.put("error", "1");
    	}
    	
    	json.add(infos);
    	return new StringBlob(json.toString(), "application/json");
    }
}
