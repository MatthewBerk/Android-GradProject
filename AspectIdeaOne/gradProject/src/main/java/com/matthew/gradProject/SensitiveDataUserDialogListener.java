package com.matthew.gradProject;

import org.aspectj.lang.ProceedingJoinPoint;


//Interface Listener would implement if need to know about sensitive data found and have reference to proceedingJoinPoint so can have method
// execute.
// An example listener that would implement this interface is one that prompts an alert to user to make a decision.
// Due to Android's UI thread policy, would need proceedingJoinPoint reference so can allow user to execute method if they approve of the data leak.
public interface SensitiveDataUserDialogListener extends SensitiveDataListener {
    boolean onFoundData(SensitiveDataMatch sensitiveDataMatch,ProceedingJoinPoint proceedingJoinPoint);
}
