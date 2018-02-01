package org.osivia.onlyoffice.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.versioning.VersioningService;
import org.nuxeo.runtime.api.Framework;
import org.osivia.onlyoffice.util.FileUtility;

import fr.toutatice.ecm.platform.core.edition.CurrentlyEditedCacheHelper;
import fr.toutatice.ecm.platform.core.edition.TemporaryLockedCacheHelper;


/**
 * @author dorian
 */
public class OnlyofficeSaveDocumentListener implements EventListener {

    /**
	 * Reverse proxy path used in front, is deleted when url are transformed to private url
	 */
	private static final String OTTC_ONLYOFFICE_PROXY_PATH = "ottc.onlyoffice.proxy.path";

	/**
	 * Private hostname of onlyoffice
	 */
	private static final String OTTC_ONLYOFFICE_SERVER_URL = "ottc.onlyoffice.server.url";

	public static final String ONLYOFFICE_SAVEDOCUMENT_EVENT_NAME = "OnlyofficeSaveDocumentEvent";

    public static final String ONLYOFFICE_CALLBACK_STATUS_PROPERTY = "OnlyofficeCallbackStatus";

    public static final String ONLYOFFICE_CALLBACK_URL_PROPERTY = "OnlyofficeCallbackUrl";

    private static Log log = LogFactory.getLog(OnlyofficeSaveDocumentListener.class);

    @Override
    public void handleEvent(Event event) throws ClientException {
        EventContext ctx = event.getContext();
        if (ctx instanceof DocumentEventContext) {
            DocumentEventContext docCtx = (DocumentEventContext) ctx;

            CoreSession session = docCtx.getCoreSession();
            DocumentModel doc = docCtx.getSourceDocument();

            Integer status = (Integer) docCtx.getProperty(ONLYOFFICE_CALLBACK_STATUS_PROPERTY);

            String url = (String) docCtx.getProperty(ONLYOFFICE_CALLBACK_URL_PROPERTY);

            if (status != null && url != null && status == 2) {
                try {
                    String docTitle = doc.getTitle();

                    BlobHolder bh = doc.getAdapter(BlobHolder.class);
                    Blob originalBlob = bh.getBlob();
                    String originalFilename = originalBlob.getFilename();
                    String originalExt = FileUtility.getFileExtension(originalFilename);
                    String onlyofficeExt = FileUtility.getOnlyOfficeExtension(originalFilename);
                    String updatedFilename = FileUtility.getFileNameWithoutExtension(originalFilename) + onlyofficeExt;

                    // If the docTitle is similar to the filename (.i.e the file was imported and the title hasn't changed), update the title with the new
                    // filename
                    if (StringUtils.equalsIgnoreCase(docTitle, originalFilename)) {
                        doc.setProperty("dublincore", "title", updatedFilename);
                    }

                    Blob blob = getOnlyofficeBlob(url);
                    blob.setFilename(updatedFilename);
                    blob.setMimeType(FileUtility.getOnlyofficeMimeType(originalFilename));

                    bh.setBlob(blob);

                    // LBI #1769 - fix problem with first renaming
                    if (!originalExt.equalsIgnoreCase(onlyofficeExt) || session.isCheckedOut(doc.getRef()) ) {
                    	
                        if(!session.isCheckedOut(doc.getRef())) {
                        	session.checkOut(doc.getRef());
                        	
                        }
                    	
                        session.checkIn(doc.getRef(), VersioningOption.NONE, "historisation avant modification onlyoffice");
                    }

                    if (!originalExt.equalsIgnoreCase(onlyofficeExt)) {
                        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
                    } else {
                        doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
                    }

                    session.saveDocument(doc);

                } catch (Exception e) {
                    log.error("erreur",e.getCause());
                }
                CurrentlyEditedCacheHelper.invalidate(doc);
                
                if(TemporaryLockedCacheHelper.get(doc) != null) {
                	session.removeLock(doc.getRef());
                	TemporaryLockedCacheHelper.invalidate(doc);
                	
                }
            }
        }
    }

    private Blob getOnlyofficeBlob(String downloadUri) {
        HttpURLConnection connection = null;
        InputStream stream = null;
        try {
            URL url = new URL(downloadUri);

            // LBI #1733 transform url in private format (if needed by proxies)
            String onlyOfficeUrl = Framework.getProperty(OTTC_ONLYOFFICE_SERVER_URL);
			if(StringUtils.isNotBlank(onlyOfficeUrl)) {
				
				String path = url.getPath().replace(Framework.getProperty(OTTC_ONLYOFFICE_PROXY_PATH), "");
				url = new URL(onlyOfficeUrl + path + "?" + url.getQuery());
            }
            
            connection = (HttpURLConnection) url.openConnection();
            stream = connection.getInputStream();
            byte[] bytes = IOUtils.toByteArray(stream);
            return new ByteArrayBlob(bytes);
        } catch (IOException e) {
            throw new ClientException(e);
        } finally {
            IOUtils.closeQuietly(stream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
