package com.matthew.gradProject;


//Interface Listener would implement if just need to know about sensitive data found.
public interface SensitiveDataLoggingListener extends SensitiveDataListener {
    boolean onFoundData(SensitiveDataMatch sensitiveDataMatch);
}
