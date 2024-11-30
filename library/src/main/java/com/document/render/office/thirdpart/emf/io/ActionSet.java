
package com.document.render.office.thirdpart.emf.io;

import java.util.HashMap;
import java.util.Map;


public class ActionSet {


    protected Map actions;

    protected Action defaultAction;


    public ActionSet() {
        actions = new HashMap();
        defaultAction = new Action.Unknown();
    }


    public void addAction(Action action) {
        actions.put(new Integer(action.getCode()), action);
    }


    public Action get(int actionCode) {
        Action action = (Action) actions.get(new Integer(actionCode));
        if (action == null) {
            action = defaultAction;
        }
        return action;
    }


    public boolean exists(int actionCode) {
        return (actions.get(new Integer(actionCode)) != null);
    }
}
