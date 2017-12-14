package org.osivia.onlyoffice.rest;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement(name = "OnlyofficeCallback")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnlyofficeCallback {

    /** actions */
    private List<OnlyofficeAction> actions;

    /** key */
    private String key;

    /** status */
    private int status;

    /** users */
    private List<String> users;

    /** changesurl */
    private String changesurl;

    /** history */
    private OnlyofficeHistory history;

    /** url */
    private String url;

    /** forcesavetype */
    private int forcesavetype;

    /** userdata */
    private String userdata;

    public static class OnlyofficeAction {

        /** type */
        private int type;

        /** userid */
        private String userid;


        /**
         * @return the type
         */
        public int getType() {
            return type;
        }


        /**
         * @param type the type to set
         */
        public void setType(int type) {
            this.type = type;
        }


        /**
         * @return the userid
         */
        public String getUserid() {
            return userid;
        }


        /**
         * @param userid the userid to set
         */
        public void setUserid(String userid) {
            this.userid = userid;
        }
    }

    public static class OnlyofficeHistory {

        /** changes */
        private JsonNode changes;

        /** serverVersion */
        private String serverVersion;


        /**
         * @return the changes
         */
        public JsonNode getChanges() {
            return changes;
        }


        /**
         * @param changes the changes to set
         */
        public void setChanges(JsonNode changes) {
            this.changes = changes;
        }


        /**
         * @return the serverVersion
         */
        public String getServerVersion() {
            return serverVersion;
        }


        /**
         * @param serverVersion the serverVersion to set
         */
        public void setServerVersion(String serverVersion) {
            this.serverVersion = serverVersion;
        }
    }


    /**
     * @return the actions
     */
    public List<OnlyofficeAction> getActions() {
        return actions;
    }


    /**
     * @param actions the actions to set
     */
    public void setActions(List<OnlyofficeAction> actions) {
        this.actions = actions;
    }


    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }


    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }


    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }


    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }


    /**
     * @return the users
     */
    public List<String> getUsers() {
        return users;
    }


    /**
     * @param users the users to set
     */
    public void setUsers(List<String> users) {
        this.users = users;
    }


    /**
     * @return the changesurl
     */
    public String getChangesurl() {
        return changesurl;
    }


    /**
     * @param changesurl the changesurl to set
     */
    public void setChangesurl(String changesurl) {
        this.changesurl = changesurl;
    }


    /**
     * @return the history
     */
    public OnlyofficeHistory getHistory() {
        return history;
    }


    /**
     * @param history the history to set
     */
    public void setHistory(OnlyofficeHistory history) {
        this.history = history;
    }


    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }


    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }


    /**
     * @return the forcesavetype
     */
    public int getForcesavetype() {
        return forcesavetype;
    }


    /**
     * @param forcesavetype the forcesavetype to set
     */
    public void setForcesavetype(int forcesavetype) {
        this.forcesavetype = forcesavetype;
    }


    /**
     * @return the userdata
     */
    public String getUserdata() {
        return userdata;
    }


    /**
     * @param userdata the userdata to set
     */
    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }


}
