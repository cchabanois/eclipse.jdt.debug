package org.eclipse.jdt.internal.debug.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;

public class ClasspathTooLongStatusHandler implements IStatusHandler {

	/**
	 * @see IStatusHandler#handleStatus(IStatus, Object)
	 */
	@Override
	public Object handleStatus(IStatus status, Object source) {
		ILaunch launch = (ILaunch) source;
		boolean enableClasspathOnlyJar = askEnableClasspathOnlyJar();
		if (enableClasspathOnlyJar) {
			try {
				ILaunchConfigurationWorkingCopy configurationWorkingCopy = launch.getLaunchConfiguration().getWorkingCopy();
				configurationWorkingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_USE_CLASSPATH_ONLY_JAR, true);
				configurationWorkingCopy.doSave();
			} catch (CoreException e) {
				JDIDebugUIPlugin.log(e);
			}
		}
		return Boolean.valueOf(enableClasspathOnlyJar);
	}

	private boolean askEnableClasspathOnlyJar() {
		final boolean[] result = new boolean[1];
		JDIDebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				String title = LauncherMessages.ClasspathTooLongStatusHandler_0;
				String message = LauncherMessages.ClasspathTooLongStatusHandler_1;
				result[0] = (MessageDialog.openQuestion(JDIDebugUIPlugin.getActiveWorkbenchShell(), title, message));
			}
		});
		return result[0];
	}

}
