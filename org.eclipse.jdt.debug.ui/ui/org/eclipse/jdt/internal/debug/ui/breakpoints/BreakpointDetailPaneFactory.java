/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.debug.ui.breakpoints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.IDetailPane;
import org.eclipse.debug.ui.IDetailPaneFactory;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaExceptionBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaLineBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaMethodBreakpoint;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaWatchpoint;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Detail pane factory for Java breakpoints.
 * 
 * @since 3.6
 */
public class BreakpointDetailPaneFactory implements IDetailPaneFactory {
	
	/**
	 * Maps pane IDs to names
	 */
	private Map fNameMap;

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneTypes(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public Set getDetailPaneTypes(IStructuredSelection selection) {
		HashSet set = new HashSet();
		if (selection.size() == 1) {
			IBreakpoint b = (IBreakpoint) selection.getFirstElement();
			try {
				String type = b.getMarker().getType();
				if (JavaLineBreakpoint.JAVA_LINE_BREAKPOINT.equals(type)) {
					set.add(StandardBreakpointDetailPane.DETAIL_PANE_STANDARD);
				} else if (JavaWatchpoint.JAVA_WATCHPOINT.equals(type)) {
					set.add(WatchpointDetailPane.DETAIL_PANE_WATCHPOINT);
				} else if (JavaMethodBreakpoint.JAVA_METHOD_BREAKPOINT.equals(type)) {
					set.add(MethodBreakpointDetailPane.DETAIL_PANE_METHOD_BREAKPOINT);
				} else if (JavaExceptionBreakpoint.JAVA_EXCEPTION_BREAKPOINT.equals(type)) {
					set.add(ExceptionBreakpointDetailPane.DETAIL_PANE_EXCEPTION_BREAKPOINT);
				} else {
					set.add(StandardBreakpointDetailPane.DETAIL_PANE_STANDARD);
				}
				if (b instanceof IJavaLineBreakpoint) {
					IJavaLineBreakpoint jlb = (IJavaLineBreakpoint) b;
					if (jlb.supportsCondition()) {
						set.add(BreakpointConditionDetailPane.DETAIL_PANE_CONDITION);
					}
				}
			} catch (CoreException e) {}
		}
		return set;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDefaultDetailPane(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public String getDefaultDetailPane(IStructuredSelection selection) {
		if (selection.size() == 1) {
			IBreakpoint b = (IBreakpoint) selection.getFirstElement();
			try {
				String type = b.getMarker().getType();
				if (b instanceof IJavaLineBreakpoint) {
					IJavaLineBreakpoint jlb = (IJavaLineBreakpoint) b;
					if (jlb.supportsCondition()) {
						return BreakpointConditionDetailPane.DETAIL_PANE_CONDITION;
					}
				}
				if (JavaWatchpoint.JAVA_WATCHPOINT.equals(type)) {
					return WatchpointDetailPane.DETAIL_PANE_WATCHPOINT;
				} else if (JavaExceptionBreakpoint.JAVA_EXCEPTION_BREAKPOINT.equals(type)) {
					return ExceptionBreakpointDetailPane.DETAIL_PANE_EXCEPTION_BREAKPOINT;
				} else {
					return StandardBreakpointDetailPane.DETAIL_PANE_STANDARD;
				}
			} catch (CoreException e) {}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#createDetailPane(java.lang.String)
	 */
	public IDetailPane createDetailPane(String paneID) {
		if (BreakpointConditionDetailPane.DETAIL_PANE_CONDITION.equals(paneID)) {
			return new BreakpointConditionDetailPane();
		}
		if (StandardBreakpointDetailPane.DETAIL_PANE_STANDARD.equals(paneID)) {
			return new StandardBreakpointDetailPane();
		}
		if (WatchpointDetailPane.DETAIL_PANE_WATCHPOINT.equals(paneID)) {
			return new WatchpointDetailPane();
		}
		if (MethodBreakpointDetailPane.DETAIL_PANE_METHOD_BREAKPOINT.equals(paneID)) {
			return new MethodBreakpointDetailPane();
		}
		if (ExceptionBreakpointDetailPane.DETAIL_PANE_EXCEPTION_BREAKPOINT.equals(paneID)) {
			return new ExceptionBreakpointDetailPane();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneName(java.lang.String)
	 */
	public String getDetailPaneName(String paneID) {
		return (String) getNameMap().get(paneID);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDetailPaneFactory#getDetailPaneDescription(java.lang.String)
	 */
	public String getDetailPaneDescription(String paneID) {
		return (String) getNameMap().get(paneID);
	}
	
	private Map getNameMap() {
		if (fNameMap == null) {
			fNameMap = new HashMap();
			fNameMap.put(BreakpointConditionDetailPane.DETAIL_PANE_CONDITION, BreakpointMessages.BreakpointConditionDetailPane_0);
			fNameMap.put(WatchpointDetailPane.DETAIL_PANE_WATCHPOINT, BreakpointMessages.WatchpointDetailPane_0);
			fNameMap.put(MethodBreakpointDetailPane.DETAIL_PANE_METHOD_BREAKPOINT, BreakpointMessages.MethodBreakpointDetailPane_0);
			fNameMap.put(StandardBreakpointDetailPane.DETAIL_PANE_STANDARD, BreakpointMessages.StandardBreakpointDetailPane_0);
			fNameMap.put(ExceptionBreakpointDetailPane.DETAIL_PANE_EXCEPTION_BREAKPOINT, BreakpointMessages.ExceptionBreakpointDetailPane_0);
		}
		return fNameMap;
	}

}
