package org.kie.saga.model;

public class CommandResponse {

    private String type;
    private String commandType;
    private String commandId;

    public boolean isSuccess(){
        return Boolean.TRUE.equals(Boolean.parseBoolean(type));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
