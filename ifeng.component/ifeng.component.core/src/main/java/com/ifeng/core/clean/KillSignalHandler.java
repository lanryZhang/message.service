/*
* KillSingleHandler.java 
* Created on  202016/12/13 17:25 
* Copyright © 2012 Phoenix New Media Limited All Rights Reserved 
*/
package com.ifeng.core.clean;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Description Here
 *
 * @author zhanglr
 * @version 1.0.1
 */
public class KillSignalHandler implements SignalHandler {
    private List<CleanupAware> handlers = new ArrayList<>();
    public void handle(Signal signal) {
        if (handlers.size() > 0){
            handlers.forEach(h-> h.cleanup());
        }
    }

    public void regist(CleanupAware handle){
        if (null != handle) {
            handlers.add(handle);
        }
    }
}
