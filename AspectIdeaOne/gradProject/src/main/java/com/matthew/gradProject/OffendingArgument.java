package com.matthew.gradProject;

import java.util.ArrayList;
import java.util.List;

// Class that holds details of the data leak, specifically the argument value and a list of all the variable values (not names of the variables)
// that was found to have sensitive data, and the sensitive data found within them.
public final class OffendingArgument {
    private Object argumentValue;
    private List<OffendingData> dataMatches;

    public OffendingArgument(Object argumentValue){
        this.argumentValue = argumentValue;
        dataMatches = new ArrayList<OffendingData>();
    }

    //This would be called for every offending value in the arg
    public void addSensitiveData(OffendingData data){
        dataMatches.add(data);
    }

    public Object getArgumentValue() {
        return argumentValue;
    }

    public List<OffendingData> getDataMatches() {
        return dataMatches;
    }
}
