/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.tools.visualvm.tools.jmx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.swing.Timer;
import org.openide.util.RequestProcessor;

/**
 * <p>The {@code CachedMBeanServerConnectionFactory} class is a factory class that
 * allows to get instances of {@link CachedMBeanServerConnection} for a given
 * {@link MBeanServerConnection} or {@link JmxModel}.</p>
 * 
 * <p>The factory methods allow to supply an interval value at which the cache will
 * be automatically flushed and interested {@link MBeanCacheListener}s notified.</p>
 * 
 * <p>If the factory methods which do not take an interval value are used then
 * no automatic flush is performed and the user will be in charge of flushing
 * the cache by calling {@link CachedMBeanServerConnection#flush()}.</p>
 *
 * @author Eamonn McManus
 * @author Luis-Miguel Alventosa
 */
public final class CachedMBeanServerConnectionFactory {

    private static Map<Integer, Map<MBeanServerConnection, CachedMBeanServerConnection>> snapshots =
            new HashMap<Integer, Map<MBeanServerConnection, CachedMBeanServerConnection>>();

    private CachedMBeanServerConnectionFactory() {
    }

    /**
     * <p>Factory method for obtaining the {@link CachedMBeanServerConnection} for
     * the given {@link MBeanServerConnection}.</p>
     * 
     * @param mbsc an MBeanServerConnection.
     * 
     * @return a {@link CachedMBeanServerConnection} instance which caches the
     * attribute values of the supplied {@link MBeanServerConnection}.
     */
    public static CachedMBeanServerConnection getCachedMBeanServerConnection(MBeanServerConnection mbsc) {
        return getCachedMBeanServerConnection(mbsc, 0);
    }

    /**
     * <p>Factory method for obtaining the {@link CachedMBeanServerConnection} for
     * the given {@link MBeanServerConnection}. The cache will be flushed at the
     * given interval and the interested {@link MBeanCacheListener}s will be notified.</p>
     * 
     * @param mbsc an MBeanServerConnection.
     * @param interval the interval (in milliseconds) at which the cache is flushed.
     * An interval equal to zero means no automatic flush of the MBean cache.
     * 
     * @return a {@link CachedMBeanServerConnection} instance which caches the
     * attribute values of the supplied {@link MBeanServerConnection} and is
     * flushed at the end of every interval period.
     * 
     * @throws IllegalArgumentException if the supplied interval is negative.
     */
    public static CachedMBeanServerConnection
            getCachedMBeanServerConnection(MBeanServerConnection mbsc, int interval)
            throws IllegalArgumentException {
        if (interval < 0) {
            throw new IllegalArgumentException("interval cannot be negative"); // NOI18N
        }
        return retrieveCachedMBeanServerConnection(mbsc, interval);
    }

    /**
     * <p>Factory method for obtaining the {@link CachedMBeanServerConnection} for
     * the given {@link JmxModel}.</p>
     * 
     * @param jmx a JmxModel.
     * 
     * @return a {@link CachedMBeanServerConnection} instance which caches the
     * attribute values of the supplied {@link JmxModel}.
     */
    public static CachedMBeanServerConnection getCachedMBeanServerConnection(JmxModel jmx) {
        return getCachedMBeanServerConnection(jmx.getMBeanServerConnection(), 0);
    }

    /**
     * <p>Factory method for obtaining the {@link CachedMBeanServerConnection} for
     * the given {@link JmxModel}. The cache will be flushed at the given interval
     * and the interested {@link MBeanCacheListener}s will be notified.</p>
     * 
     * @param jmx a JmxModel.
     * @param interval the interval (in milliseconds) at which the cache is flushed.
     * An interval equal to zero means no automatic flush of the MBean cache.
     * 
     * @return a {@link CachedMBeanServerConnection} instance which caches the
     * attribute values of the supplied {@link JmxModel} and is flushed at the
     * end of every interval period.
     *
     * @throws IllegalArgumentException if the supplied interval is negative.
     */
    public static CachedMBeanServerConnection
            getCachedMBeanServerConnection(JmxModel jmx, int interval)
            throws IllegalArgumentException {
        return getCachedMBeanServerConnection(jmx.getMBeanServerConnection(), interval);
    }

    private static synchronized CachedMBeanServerConnection
            retrieveCachedMBeanServerConnection(MBeanServerConnection mbsc, int interval) {
        Map<MBeanServerConnection, CachedMBeanServerConnection> mbscMap =
                snapshots.get(interval);
        if (mbscMap == null) {
            CachedMBeanServerConnection cmbsc = Snapshot.newSnapshot(mbsc, interval);
            Map<MBeanServerConnection, CachedMBeanServerConnection> mbscMapNew =
                    new HashMap<MBeanServerConnection, CachedMBeanServerConnection>();
            mbscMapNew.put(mbsc, cmbsc);
            snapshots.put(interval, mbscMapNew);
            return cmbsc;
        } else {
            CachedMBeanServerConnection cmbsc = mbscMap.get(mbsc);
            if (cmbsc == null) {
                cmbsc = Snapshot.newSnapshot(mbsc, interval);
                mbscMap.put(mbsc, cmbsc);
                return cmbsc;                
            } else {
                return cmbsc;
            }
        }
    }

    static class Snapshot {

        private Snapshot() {
        }

        public static CachedMBeanServerConnection newSnapshot(MBeanServerConnection mbsc, int interval) {
            final InvocationHandler ih = new SnapshotInvocationHandler(mbsc, interval);
            return (CachedMBeanServerConnection) Proxy.newProxyInstance(
                    Snapshot.class.getClassLoader(),
                    new Class[]{CachedMBeanServerConnection.class},
                    ih);
        }
    }

    static class SnapshotInvocationHandler implements InvocationHandler {

        private final MBeanServerConnection conn;
        private final int interval;
        private Timer timer = null;
        private Map<ObjectName, NameValueMap> cachedValues = newMap();
        private Map<ObjectName, Set<String>> cachedNames = newMap();
        private List<MBeanCacheListener> listenerList = new CopyOnWriteArrayList<MBeanCacheListener>();

        @SuppressWarnings("serial")
        private static final class NameValueMap
                extends HashMap<String, Object> {
        }

        SnapshotInvocationHandler(MBeanServerConnection conn, int interval) {
            this.conn = conn;
            this.interval = interval;
            if (interval > 0) {
                timer = new Timer(interval, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        intervalElapsed();
                    }
                });
                timer.setCoalesce(true);
                timer.start();
            }
        }

        synchronized void intervalElapsed() {
            RequestProcessor.getDefault().post(new Runnable() {
                public void run() {
                    flush();
                    notifyListeners();
                }
            });
        }

        synchronized void notifyListeners() {
            for (MBeanCacheListener listener : listenerList) {
                listener.flushed();
            }
        }

        synchronized void flush() {
            cachedValues = newMap();
        }

        int getInterval() {
            return interval;
        }

        void addMBeanCacheListener(MBeanCacheListener listener) {
            listenerList.add(listener);
        }

        void removeMBeanCacheListener(MBeanCacheListener listener) {
            listenerList.remove(listener);            
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            final String methodName = method.getName();
            if (methodName.equals("getAttribute")) { // NOI18N
                return getAttribute((ObjectName) args[0], (String) args[1]);
            } else if (methodName.equals("getAttributes")) { // NOI18N
                return getAttributes((ObjectName) args[0], (String[]) args[1]);
            } else if (methodName.equals("flush")) { // NOI18N
                flush();
                return null;
            } else if (methodName.equals("getInterval")) { // NOI18N
                return getInterval();
            } else if (methodName.equals("addMBeanCacheListener")) { // NOI18N
                addMBeanCacheListener((MBeanCacheListener) args[0]);
                return null;
            } else if (methodName.equals("removeMBeanCacheListener")) { // NOI18N
                removeMBeanCacheListener((MBeanCacheListener) args[0]);
                return null;
            } else {
                try {
                    return method.invoke(conn, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
        }

        private Object getAttribute(ObjectName objName, String attrName)
                throws MBeanException, InstanceNotFoundException,
                AttributeNotFoundException, ReflectionException, IOException {
            final NameValueMap values = getCachedAttributes(
                    objName, Collections.singleton(attrName));
            Object value = values.get(attrName);
            if (value != null || values.containsKey(attrName)) {
                return value;
            }
            // Not in cache, presumably because it was omitted from the
            // getAttributes result because of an exception.  Following
            // call will probably provoke the same exception.
            return conn.getAttribute(objName, attrName);
        }

        private AttributeList getAttributes(
                ObjectName objName, String[] attrNames) throws
                InstanceNotFoundException, ReflectionException, IOException {
            final NameValueMap values = getCachedAttributes(
                    objName,
                    new TreeSet<String>(Arrays.asList(attrNames)));
            final AttributeList list = new AttributeList();
            for (String attrName : attrNames) {
                final Object value = values.get(attrName);
                if (value != null || values.containsKey(attrName)) {
                    list.add(new Attribute(attrName, value));
                }
            }
            return list;
        }

        private synchronized NameValueMap getCachedAttributes(
                ObjectName objName, Set<String> attrNames) throws
                InstanceNotFoundException, ReflectionException, IOException {
            NameValueMap values = cachedValues.get(objName);
            if (values != null && values.keySet().containsAll(attrNames)) {
                return values;
            }
            attrNames = new TreeSet<String>(attrNames);
            Set<String> oldNames = cachedNames.get(objName);
            if (oldNames != null) {
                attrNames.addAll(oldNames);
            }
            values = new NameValueMap();
            final AttributeList attrs = conn.getAttributes(
                    objName,
                    attrNames.toArray(new String[attrNames.size()]));
            for (Attribute attr : attrs.asList()) {
                values.put(attr.getName(), attr.getValue());
            }
            cachedValues.put(objName, values);
            cachedNames.put(objName, attrNames);
            return values;
        }

        // See http://www.artima.com/weblogs/viewpost.jsp?thread=79394
        private static <K, V> Map<K, V> newMap() {
            return new HashMap<K, V>();
        }
    }
}
