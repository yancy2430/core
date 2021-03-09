package com.tdeado.core.enums;

public enum FileType implements BaseEnum<String>{
    NOT(0, "不是文件"),
    IMAGE(1, "图片"),
    FILE(2, "文件"),
    DOC(3, "文档"),
    XLS(4, "表格")
    ;
    private final int code;
    private final String descp;
    FileType(int code, String descp) {
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
    public static FileType getValueByCode(Integer code){
        for(FileType platformFree: FileType.values()){
            if(code.equals(platformFree.getCode())){
                return platformFree;
            }
        }
        return  null;
    }
}
