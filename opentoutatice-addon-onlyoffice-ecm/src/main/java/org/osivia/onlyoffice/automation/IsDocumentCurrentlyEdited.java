/**
 * 
 */
package org.osivia.onlyoffice.automation;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import fr.toutatice.ecm.platform.core.edition.CurrentlyEditedCacheHelper;
import fr.toutatice.ecm.platform.core.edition.CurrentlyEditedInfosProvider;

/**
 * Return informations about current modifications in only office
 * @author Loïc Billon
 *
 */
@Operation(
		id = IsDocumentCurrentlyEdited.ID,
		category = Constants.CAT_DOCUMENT,
		label = "Is document currently edited in only office ?",
		description = "Return true if document is currently edited and by whom")
public class IsDocumentCurrentlyEdited {

	public final static String ID = "Document.IsDocumentCurrentlyEdited";
	
    private static final Log log = LogFactory.getLog(IsDocumentCurrentlyEdited.class);

	
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

    	Map<String, Object> infos = null;
        JSONObject cacheEntry = CurrentlyEditedCacheHelper.get(currentDocument);
        if (cacheEntry != null) {
            infos = new HashMap<>(2);
            infos.put(CurrentlyEditedInfosProvider.IS_CURRENTLY_EDITED_KEY, true);
            infos.put(CurrentlyEditedInfosProvider.CURRENTLY_EDITED_KEY, cacheEntry);
            
            json.add(infos);
        } else {
            infos = new HashMap<>(1);
            infos.put(CurrentlyEditedInfosProvider.IS_CURRENTLY_EDITED_KEY, false);
            
            json.add(infos);
        }
    	
    	return new StringBlob(json.toString(), "application/json");
    }
}
