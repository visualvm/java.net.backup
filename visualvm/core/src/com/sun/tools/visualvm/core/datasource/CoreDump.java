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

package com.sun.tools.visualvm.core.datasource;

/**
 * DataSource representing a core dump.
 *
 * @author Tomas Hurka
 */
public interface CoreDump extends Snapshot {
    
    /**
     * Named property for display name of the coredump.
     */
    public static final String PROPERTY_DISPLAYNAME = "prop_displayname";
    
    /**
     * Returns display name of this core dump.
     * This name is used everywhere in VisualVM UI when referring to this core dump instance.
     * 
     * @return display name of this core dump.
     */
    public String getDisplayName();
    
    /**
     * Sets display name of this core dump.
     * This name is used everywhere in VisualVM UI when referring to this core dump instance.
     * 
     * @param string display name of this core dump.
     */
    public void setDisplayName(String string);
    
    /**
     * Returns java executable which will be used to retrieve data from this core dump.
     * This executable must be compatible with the executable which was running the application
     * when the core dump was created.
     * 
     * @return java executable which will be used to retrieve data from this core dump
     */
    public String getExecutable();
    
    public String getJDKHome();
    
}
