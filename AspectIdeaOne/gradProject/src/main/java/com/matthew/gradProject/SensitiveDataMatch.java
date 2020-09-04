package com.matthew.gradProject;

import java.util.ArrayList;
import java.util.List;


// Class to hold details of the data leak, specifically the method name, file name, line number and info about the arguments that were found to contain sensitive data.
public final class SensitiveDataMatch {

    private String methodName;
    private String fileName;
    private int lineNumber;
    private List<OffendingArgument> offendingArgumentList;


    public SensitiveDataMatch(String methodName,String fileName,int lineNumber){
        this.methodName = methodName;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        offendingArgumentList = new ArrayList<OffendingArgument>();
    }

    public void addOffendingArgument(OffendingArgument offendingArgument){
        offendingArgumentList.add(offendingArgument);
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public boolean isOffendingArgumentListEmpty(){
        return  offendingArgumentList.isEmpty();
    }

    public List<OffendingArgument> getOffendingArgumentList(){
        return  offendingArgumentList;
    }
}
