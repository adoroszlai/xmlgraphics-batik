/*

   Copyright 2001-2003,2005-2006  The Apache Software Foundation 

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package org.apache.batik.script.rhino;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.CustomEvent;
import org.apache.batik.script.ScriptEventWrapper;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextAction;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

/**
 * A class that wraps an <code>EventTarget</code> instance to expose
 * it in the Rhino engine. Then calling <code>addEventListener</code>
 * with a Rhino function as parameter should redirect the call to
 * <code>addEventListener</code> with a Java function object calling
 * the Rhino function.
 * This class also allows to pass an ECMAScript (Rhino) object as
 * a parameter instead of a function provided the fact that this object
 * has a <code>handleEvent</code> method.
 * @author <a href="mailto:cjolif@ilog.fr">Christophe Jolif</a>
 * @version $Id$
 */
class EventTargetWrapper extends NativeJavaObject {

    /**
     * The Java function object calling the Rhino function.
     */
    static class FunctionEventListener implements EventListener {
        protected Function function;
        protected RhinoInterpreter interpreter;
        FunctionEventListener(Function f, RhinoInterpreter i) {
            function = f;
            interpreter = i;
        }
        public void handleEvent(Event evt) {
            Object event;
            if (evt instanceof ScriptEventWrapper) {
                event = ((ScriptEventWrapper) evt).getEventObject();
            } else {
                event = evt;
            }
            interpreter.callHandler(function, event);
        }
    }

    static class HandleEventListener implements EventListener {
        public final static String HANDLE_EVENT = "handleEvent";

        public Scriptable scriptable;
        public Object[] array = new Object[1];
        public RhinoInterpreter interpreter;

        HandleEventListener(Scriptable s, RhinoInterpreter interpreter) {
            scriptable = s;
            this.interpreter = interpreter;
        }
        public void handleEvent(Event evt) {
            if (evt instanceof ScriptEventWrapper) {
                array[0] = ((ScriptEventWrapper) evt).getEventObject();
            } else {
                array[0] = evt;
            }
            ContextAction handleEventAction = new ContextAction() {
                public Object run(Context cx) {
                    ScriptableObject.callMethod
                        (scriptable, HANDLE_EVENT, array);
                    return null;
                }
            };
            interpreter.call(handleEventAction);
        }
    }

    /**
     * A Java object wrapper for a javascript custom event object.
     */
    static class ObjectCustomEvent implements CustomEvent, ScriptEventWrapper {

        public Scriptable scriptable;
        public RhinoInterpreter interpreter;
        protected Map eventMap;

        public ObjectCustomEvent(Scriptable s,
                                 RhinoInterpreter interpreter,
                                 Map eventMap) {
            scriptable = s;
            this.interpreter = interpreter;
            this.eventMap = eventMap;
        }

        public Object getEventObject() {
            return scriptable;
        }

        protected Object call(String fn) {
            return call(fn, null);
        }

        protected Object call(final String fn, final Object[] args) {
            ContextAction callMethodAction = new ContextAction() {
                public Object run(Context cx) {
                    return ScriptableObject.callMethod(scriptable, fn, args);
                }
            };
            return interpreter.call(callMethodAction);
        }

        public String getType() {
            return (String) Context.jsToJava
                (scriptable.get("type", scriptable), String.class);
        }

        public EventTarget getTarget() {
            Object target = scriptable.get("target", scriptable);
            if (target instanceof NativeJavaObject) {
                target = ((NativeJavaObject) target).unwrap();
            }
            return (EventTarget) target;
        }

        public void setTarget(EventTarget target) {
            scriptable.put("target", scriptable,
                           Context.toObject(target, scriptable));
        }

        public EventTarget getCurrentTarget() {
            Object target = scriptable.get("currentTarget", scriptable);
            if (target instanceof NativeJavaObject) {
                target = ((NativeJavaObject) target).unwrap();
            }
            return (EventTarget) target;
        }

        public short getEventPhase() {
            Object ep = scriptable.get("eventPhase", scriptable);
            Integer i = (Integer) Context.jsToJava(ep, Integer.class);
            return (short) i.intValue();
        }

        public boolean getBubbles() {
            Object ep = scriptable.get("bubbles", scriptable);
            Boolean i = (Boolean) Context.jsToJava(ep, Boolean.class);
            return i.booleanValue();
        }

        public boolean getCancelable() {
            Object ep = scriptable.get("cancelable", scriptable);
            Boolean i = (Boolean) Context.jsToJava(ep, Boolean.class);
            return i.booleanValue();
        }

        public long getTimeStamp() {
            Object ts = scriptable.get("timeStamp", scriptable);
            Double d = (Double) Context.jsToJava(ts, Double.class);
            return (long) d.doubleValue();
        }

        public void stopPropagation() {
            call("stopPropagation");
        }

        public void preventDefault() {
            call("preventDefault");
        }

        public void initEvent(String t, boolean b, boolean c) {
            call("initEvent", new Object[] { t, new Boolean(b), new Boolean(c) });
        }

        public String getNamespaceURI() {
            return (String) Context.jsToJava
                (scriptable.get("namespaceURI", scriptable), String.class);
        }

        public boolean isCustom() {
            Boolean b = (Boolean) Context.jsToJava
                (call("isCustom"), Boolean.class);
            return b.booleanValue();
        }

        public void stopImmediatePropagation() {
            call("stopImmediatePropagation");
        }

        public boolean isDefaultPrevented() {
            Boolean b = (Boolean) Context.jsToJava
                (call("isDefaultPrevented"), Boolean.class);
            return b.booleanValue();
        }

        public void initEventNS(String ns, String t, boolean b, boolean c) {
            call("initEventNS", new Object[] { ns, t, new Boolean(b),
                                               new Boolean(c) });
        }

        public void setDispatchState(EventTarget t, short phase) {
            call("setDispatchState", new Object[] { t, new Double(phase) });
        }

        public boolean isPropagationStopped() {
            Boolean b = (Boolean) Context.jsToJava
                (call("isPropagationStopped"), Boolean.class);
            return b.booleanValue();
        }

        public boolean isImmediatePropagationStopped() {
            Boolean b = (Boolean) Context.jsToJava
                (call("isImmediatePropagationStopped"), Boolean.class);
            return b.booleanValue();
        }

        public void resumePropagation() {
            call("resumePropagation");
        }

        public CustomEvent retarget(EventTarget target) {
            Object ret = call("cloneEventObject", new Object[] { target });
            if (ret instanceof ScriptableObject) {
                ScriptableObject e = (ScriptableObject) ret;
                e.put("originalEvent", e, scriptable);
                e.put("target", e, Context.toObject(target, scriptable));
                e.put("type", e,
                        scriptable.get("type", scriptable));
                e.put("currentTarget", e,
                        scriptable.get("currentTarget", scriptable));
                e.put("eventPhase", e,
                        scriptable.get("eventPhase", scriptable));
                e.put("bubbles", e,
                        scriptable.get("bubbles", scriptable));
                e.put("cancelable", e,
                        scriptable.get("cancelable", scriptable));
                e.put("timeStamp", e,
                        scriptable.get("timeStamp", scriptable));
                e.put("namespaceURI", e,
                        scriptable.get("namespaceURI", scriptable));
                CustomEvent evt = new ObjectCustomEvent((NativeObject) ret,
                                                        interpreter,
                                                        eventMap);
                eventMap.put(ret, new SoftReference(evt));
                return evt;
            }
            // XXX some error here
            return null;
        }

        public Event getOriginalEvent() {
            Object target = scriptable.get("target", scriptable);
            if (target instanceof NativeJavaObject) {
                target = ((NativeJavaObject) target).unwrap();
            }
            return (Event) target;
        }
    }

    static abstract class FunctionProxy implements Function {
        protected Function delegate;

        public FunctionProxy(Function delegate) {
            this.delegate = delegate;
        }

        public Scriptable construct(Context cx,
                                    Scriptable scope, Object[] args) {
            return this.delegate.construct(cx, scope, args);
        }

        public String getClassName() {
            return this.delegate.getClassName();
        }

        public Object get(String name, Scriptable start) {
            return this.delegate.get(name, start);
        }

        public Object get(int index, Scriptable start) {
            return this.delegate.get(index, start);
        }

        public boolean has(String name, Scriptable start) {
            return this.delegate.has(name, start);
        }

        public boolean has(int index, Scriptable start) {
            return this.delegate.has(index, start);
        }

        public void put(String name, Scriptable start, Object value) {
            this.delegate.put(name, start, value);
        }

        public void put(int index, Scriptable start, Object value) {
            this.delegate.put(index, start, value);
        }

        public void delete(String name) {
            this.delegate.delete(name);
        }

        public void delete(int index) {
            this.delegate.delete(index);
        }

        public Scriptable getPrototype() {
            return this.delegate.getPrototype();
        }

        public void setPrototype(Scriptable prototype) {
            this.delegate.setPrototype(prototype);
        }

        public Scriptable getParentScope() {
            return this.delegate.getParentScope();
        }

        public void setParentScope(Scriptable parent) {
            this.delegate.setParentScope(parent);
        }

        public Object[] getIds() {
            return this.delegate.getIds();
        }

        public Object getDefaultValue(Class hint) {
            return this.delegate.getDefaultValue(hint);
        }

        public boolean hasInstance(Scriptable instance) {
            return this.delegate.hasInstance(instance);
        }
    }

    /**
     * This function proxy is delegating most of the job
     * to the underlying NativeJavaMethod object through
     * the FunctionProxy. However to allow user to specify
     * "Function" or objects with an "handleEvent" method
     * as parameter of "addEventListener"
     * it redefines the call method to deal with these
     * cases.
     */
    static class FunctionAddProxy extends FunctionProxy {
        protected Map              listenerMap;
        protected RhinoInterpreter interpreter;
        FunctionAddProxy(RhinoInterpreter interpreter,
                         Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                EventListener evtListener = null;
                SoftReference sr = (SoftReference)listenerMap.get(args[1]);
                if (sr != null) 
                    evtListener = (EventListener)sr.get();
                if (evtListener == null) {
                    evtListener = new FunctionEventListener
                        ((Function)args[1], interpreter);
                    listenerMap.put(args[1], new SoftReference(evtListener));
                }
                // we need to marshall args
                Class[] paramTypes = { String.class, Function.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                ((EventTarget)njo.unwrap()).addEventListener
                    ((String)args[0], evtListener,
                     ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }
            if (args[1] instanceof NativeObject) {
                EventListener evtListener = null;
                SoftReference sr = (SoftReference)listenerMap.get(args[1]);
                if (sr != null) 
                    evtListener = (EventListener)sr.get();
                if (evtListener == null) {
                    evtListener = new HandleEventListener((Scriptable)args[1],
                                                          interpreter);
                    listenerMap.put(args[1], new SoftReference(evtListener));
                }

                // we need to marshall args
                Class[] paramTypes = { String.class, Scriptable.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                ((EventTarget)njo.unwrap()).addEventListener
                    ((String)args[0], evtListener,
                     ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }
            return delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionRemoveProxy extends FunctionProxy {
        public Map listenerMap;

        FunctionRemoveProxy(Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[1] instanceof Function) {
                SoftReference sr = (SoftReference)listenerMap.get(args[1]);
                if (sr == null)
                    return Undefined.instance;
                EventListener el = (EventListener)sr.get();
                if (el == null)
                    return Undefined.instance;

                // we need to marshall args
                Class[] paramTypes = { String.class, Function.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                ((EventTarget)njo.unwrap()).removeEventListener
                    ((String)args[0], el, ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }
            if (args[1] instanceof NativeObject) {
                SoftReference sr = (SoftReference)listenerMap.get(args[1]);
                if (sr == null)
                    return Undefined.instance;
                EventListener el = (EventListener)sr.get();
                if (el == null)
                    return Undefined.instance;
                // we need to marshall args
                Class[] paramTypes = { String.class, Scriptable.class,
                                       Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                ((EventTarget)njo.unwrap()).removeEventListener
                    ((String)args[0], el, ((Boolean)args[2]).booleanValue());
                return Undefined.instance;
            }
            return delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionAddNSProxy extends FunctionProxy {
        protected Map              listenerMap;
        protected RhinoInterpreter interpreter;

        FunctionAddNSProxy(RhinoInterpreter interpreter,
                           Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
            this.interpreter = interpreter;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                EventListener evtListener = new FunctionEventListener
                    ((Function)args[2], interpreter);
                listenerMap.put(args[2], new SoftReference(evtListener));
                // we need to marshall args
                Class[] paramTypes = { String.class, String.class,
                                       Function.class, Boolean.TYPE,
                                       Object.class };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                AbstractNode target = (AbstractNode) njo.unwrap();
                target.addEventListenerNS
                    ((String)args[0],
                     (String)args[1],
                     evtListener,
                     ((Boolean)args[3]).booleanValue(),
                     args[4]);
                return Undefined.instance;
            }
            if (args[2] instanceof NativeObject) {
                EventListener evtListener =
                    new HandleEventListener((Scriptable)args[2], interpreter);
                listenerMap.put(args[2], new SoftReference(evtListener));
                // we need to marshall args
                Class[] paramTypes = { String.class, String.class,
                                       Scriptable.class, Boolean.TYPE,
                                       Object.class };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                AbstractNode target = (AbstractNode) njo.unwrap();
                target.addEventListenerNS
                    ((String)args[0],
                     (String)args[1],
                     evtListener,
                     ((Boolean)args[3]).booleanValue(),
                     args[4]);
                return Undefined.instance;
            }
            return delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionRemoveNSProxy extends FunctionProxy {
        protected Map listenerMap;

        FunctionRemoveNSProxy(Function delegate, Map listenerMap) {
            super(delegate);
            this.listenerMap = listenerMap;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject)thisObj;
            if (args[2] instanceof Function) {
                SoftReference sr = (SoftReference)listenerMap.get(args[2]);
                if (sr == null)
                    return Undefined.instance;
                EventListener el = (EventListener)sr.get();
                if (el == null)
                    return Undefined.instance;
                // we need to marshall args
                Class[] paramTypes = { String.class, String.class,
                                       Function.class, Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);
                AbstractNode target = (AbstractNode) njo.unwrap();
                target.removeEventListenerNS
                    ((String)args[0],
                     (String)args[1],
                     el,
                     ((Boolean)args[3]).booleanValue());
                return Undefined.instance;
            }
            if (args[2] instanceof NativeObject) {
                SoftReference sr = (SoftReference)listenerMap.get(args[2]);
                if (sr == null)
                    return Undefined.instance;
                EventListener el = (EventListener)sr.get();
                if (el == null)
                    return Undefined.instance;
                // we need to marshall args
                Class[] paramTypes = { String.class, String.class,
                                       Scriptable.class, Boolean.TYPE };
                for (int i = 0; i < args.length; i++)
                    args[i] = Context.jsToJava(args[i], paramTypes[i]);

                AbstractNode target = (AbstractNode) njo.unwrap();
                target.removeEventListenerNS
                    ((String)args[0],
                     (String)args[1],
                     el,
                     ((Boolean)args[3]).booleanValue());
                return Undefined.instance;
            }
            return delegate.call(ctx, scope, thisObj, args);
        }
    }

    static class FunctionDispatchProxy extends FunctionProxy {
        protected Map              eventMap;
        protected RhinoInterpreter interpreter;
        FunctionDispatchProxy(RhinoInterpreter interpreter,
                              Function delegate, Map eventMap) {
            super(delegate);
            this.eventMap = eventMap;
            this.interpreter = interpreter;
        }

        public Object call(Context ctx, Scriptable scope,
                           Scriptable thisObj, Object[] args) {
            NativeJavaObject njo = (NativeJavaObject) thisObj;
            if (args[0] instanceof NativeObject) {
                Event evt = new ObjectCustomEvent((NativeObject) args[0],
                                                  interpreter,
                                                  eventMap);
                eventMap.put(args[0], new SoftReference(evt));
                args[0] = Context.jsToJava(args[0], Scriptable.class);
                ((EventTarget) njo.unwrap()).dispatchEvent(evt);
                return Undefined.instance;
            }
            return delegate.call(ctx, scope, thisObj, args);
        }
    }

    // the keys are the underlying Java object, in order
    // to remove potential memory leaks use a WeakHashMap to allow
    // to collect entries as soon as the underlying Java object is
    // not available anymore.
    protected static WeakHashMap mapOfListenerMap;

    public final static String ADD_NAME      = "addEventListener";
    public final static String ADDNS_NAME    = "addEventListenerNS";
    public final static String REMOVE_NAME   = "removeEventListener";
    public final static String REMOVENS_NAME = "removeEventListenerNS";
    public final static String DISPATCH_NAME = "dispatchEvent";

    protected RhinoInterpreter interpreter;
    EventTargetWrapper(Scriptable scope, EventTarget object,
                       RhinoInterpreter interpreter) {
        super(scope, object, null);
        this.interpreter = interpreter;
    }

    /**
     * Overriden Rhino method.
     */
    public Object get(String name, Scriptable start) {
        Object method = super.get(name, start);
        if (name.equals(ADD_NAME)) {
            // prevent creating a Map for all JavaScript objects
            // when we need it only from time to time...
            method = new FunctionAddProxy(interpreter, 
                                          (Function)method, initMap());
        } else if (name.equals(REMOVE_NAME)) {
            // prevent creating a Map for all JavaScript objects
            // when we need it only from time to time...
            method = new FunctionRemoveProxy
                ((Function)method, initMap());
        } else if (name.equals(ADDNS_NAME)) {
            method = new FunctionAddNSProxy(interpreter,
                                            (Function) method, initMap());
        } else if (name.equals(REMOVENS_NAME)) {
            method = new FunctionRemoveNSProxy((Function) method, initMap());
        } else if (name.equals(DISPATCH_NAME)) {
            method = new FunctionDispatchProxy(interpreter, (Function) method, initMap());
        }
        return method;
    }

    // we have to store the listenerMap in a Map because
    // several EventTargetWrapper may be created for the exact
    // same underlying Java object.
    public Map initMap() {
        Map map = null;
        if (mapOfListenerMap == null)
            mapOfListenerMap = new WeakHashMap(10);
        if ((map = (Map)mapOfListenerMap.get(unwrap())) == null) {
            mapOfListenerMap.put(unwrap(), map = new WeakHashMap(2));
        }
        return map;
    }
}
