package com.tdeado.core.enums;

public enum ContentType implements BaseEnum<String>{
    TEXT(0, "text"),
    NUMBER(1, "number"),
    PASS(2, "password"),
    FILE(3, "file"),
    IMAGE(4, "image"),
    ;
    private final int code;
    private final String descp;
    ContentType(int code, String descp) {
        this.code = code;
        this.descp = descp;
    }

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    @Override
    public String getValue() {
        return descp;
    }
    /**
     * 根据code获取去value
     * @param code
     * @return
     */
    public static ContentType getValueByCode(Integer code){
        for(ContentType platformFree: ContentType.values()){
            if(code.equals(platformFree.getCode())){
                return platformFree;
            }
        }
        return  null;
    }
}
