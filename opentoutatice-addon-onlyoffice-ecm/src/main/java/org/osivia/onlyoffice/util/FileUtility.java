package org.osivia.onlyoffice.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.nuxeo.ecm.core.api.Blob;

public class FileUtility
{
    public static final String SPREADSHEET_EXTENSION = ".xlsx";
    public static final String PRESENTATION_EXTENSION = ".pptx";
    public static final String TEXT_EXTENSION = ".docx";
    public static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String PPTX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    private FileUtility()
    {
        super();
    }

    /**
     * FileType associé à un nom de fichier.
     * <p>basé sur l'extension du nom de fichier</p>
     * @param fileName
     * @return
     */
    public static FileType getFileType(String fileName)
    {
        String ext = getFileExtension(fileName).toLowerCase();

        if (ExtsDocument.contains(ext)) {
            return FileType.TEXT;
        }

        if (ExtsSpreadsheet.contains(ext)) {
            return FileType.SPREADSHEET;
        }

        if (ExtsPresentation.contains(ext)) {
            return FileType.PRESENTATION;
        }

        return FileType.TEXT;
    }

    /**
     * Teste si un fichier est pris en charge par l'édition OnlyOffice
     * <p>basé sur l'extension du nom de fichier</p>
     * @param fileName
     * @return
     */
    public static boolean isManaged(String fileName)
    {
        String ext = getFileExtension(fileName).toLowerCase();

        return ExtsDocument.contains(ext) || ExtsSpreadsheet.contains(ext) || ExtsPresentation.contains(ext);

    }

    /**
     * Teste si un Blob possède un mimeType valide au sens OnlyOffice
     * <p>les mimeType valides sont ceux des fichiers .docx, .xlx et .pptx</p>
     * <p>un fichier .doc pourra donc être édité mais son mimeType sera considéré comme invalide</p>
     * @param blob
     * @return
     */
    public static boolean isValidMimetype(Blob blob)
    {
        return isValidMimetype(blob.getFilename(), blob.getMimeType());
    }

    /**
     * Teste si la cohérence entre un fichier et son mimeType sens OnlyOffice
     * <p>les mimeType valides sont ceux des fichiers .docx, .xlx et .pptx</p>
     * <p>un fichier .doc pourra donc être édité mais son mimeType sera considéré comme invalide</p>
     * @param filename
     * @param mimetype
     * @return
     */
    public static boolean isValidMimetype(String filename, String mimetype)
    {
        return getOnlyofficeMimeType(filename).equalsIgnoreCase(mimetype);
    }

    /**
     * MimeType OnlyOffice associé à un fichier
     * <p>OnlyOffice ne gère que les mimeTypes des fichiers de type .docx, .xlsx ou .pptx,
     * un fichier .doc obtiendra donc un mimetype application/vnd.openxmlformats-officedocument.wordprocessingml.document
     * via cette méthode</p>
     * @param filename
     * @return
     */
    public static String getOnlyofficeMimeType(String filename)
    {
        FileType fileType = getFileType(filename);

        switch (fileType)
        {
            case TEXT:
                return DOCX_MIME_TYPE;
            case PRESENTATION:
                return PPTX_MIME_TYPE;
            case SPREADSHEET:
                return XLSX_MIME_TYPE;
        }

        return DOCX_MIME_TYPE;
    }

    /**
     * Extension OnlyOffice associée à un fichier.
     * <p>OnlyOffice ne gère que les mimeTypes des fichiers de type .docx, .xlsx ou .pptx,
     * un fichier .doc obtiendra donc une extension .docx via cette méthode</p>
     * @param filename
     * @return
     */
    public static String getOnlyOfficeExtension(String filename)
    {
        FileType fileType = getFileType(filename);

        switch (fileType)
        {
            case TEXT:
                return TEXT_EXTENSION;
            case PRESENTATION:
                return PRESENTATION_EXTENSION;
            case SPREADSHEET:
                return SPREADSHEET_EXTENSION;
        }

        return TEXT_EXTENSION;
    }

    /**
     * extension des documents compatibles Word
     */
    protected static final List<String> ExtsDocument = Arrays.asList(TEXT_EXTENSION, ".doc", ".odt", ".rtf", ".txt", ".html", ".htm", ".mht", ".djvu",
            ".fb2", ".epub", ".xps");

    /**
     * extension des documents compatibles Excel
     */
    protected static final List<String> ExtsSpreadsheet = Arrays.asList(".xls", SPREADSHEET_EXTENSION, ".ods", ".csv");

    /**
     * extension des documents compatibles PowerPoint
     */
    protected static final List<String> ExtsPresentation = Arrays.asList(".pps", ".ppsx", ".ppt", PRESENTATION_EXTENSION, ".odp");

    /**
     * Nom du fichier associé à une URL
     * <p>il s'agit de la chaîne située à droite du dernier /</p>
     * @param url
     * @return
     */
    public static String getFileName(String url)
    {
        if (url == null)
        {
            return "";
        }

        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    /**
     * Nom du fichier (sans extension) associé à une URL
     * @param url
     * @return
     */
    public static String getFileNameWithoutExtension(String url)
    {
        String fileName = getFileName(url);
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    /**
     * Extension du fichier associé à une URL
     * @param url
     * @return
     */
    public static String getFileExtension(String url)
    {
        String fileName = getFileName(url);
        String fileExt = fileName.substring(fileName.lastIndexOf('.'));
        return fileExt.toLowerCase();
    }

    /**
     * Encodage Base69 d'une liste de chaînes
     * <p>il s'agit d'un encodage compatible JWT : les chaînes seront préalablement
     * concaténées avec .</p>
     * @param strings
     * @return
     */
    public static String getEncoded(String... strings)
    {
        StringBuilder sb = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++)
        {
            sb.append('.').append(strings[i].replace('.', ' '));
        }

        return Base64.encodeBase64String(sb.toString().getBytes());
    }

    /**
     * Décodage d'une chaîne encodée sur le modèle de {@link FileUtility#getEncoded(String...)}
     * @param string
     * @return
     */
    public static String[] getDecoded(String string)
    {
        return new String(Base64.decodeBase64(string)).split("\\.");
    }
}
