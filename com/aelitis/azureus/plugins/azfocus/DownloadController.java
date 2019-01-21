/**
 * File: DownloadControl.java
 * Library: Focus Plugin for Azureus
 * Date: 23 Jan 2007
 *
 * Author: Allan Crooks
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details ( see the COPYING file ).
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.aelitis.azureus.plugins.azfocus;

import java.util.Arrays;
import java.util.Collections;
import com.biglybt.pif.PluginInterface;
import com.biglybt.pif.download.*;
import com.biglybt.pif.utils.*;

/**
 * @author Allan Crooks
 *
 */
public class DownloadController {
	
	private PluginInterface plugin_interface;
	private UTTimer timer;
	
	public DownloadController(PluginInterface pi) {
		plugin_interface = pi;
		timer = pi.getUtilities().createTimer("FocusPlugin-DownloadController", true);
	}
	
	public void resumeAllDownloads() {
		DownloadControl dc = new DownloadControl(this.plugin_interface, DownloadCriteria.ALL, DownloadCriteria.NONE, true);
		timer.addEvent(System.currentTimeMillis() + 300, dc);
	}
	
	public void pauseAllDownloadsExcept(Download[] downloads) {
		DownloadControl dc = new DownloadControl(this.plugin_interface, DownloadCriteria.ALL, DownloadCriteria.create(downloads), false);
		timer.addEvent(System.currentTimeMillis() + 300, dc);
	}
	
	private class DownloadControl implements UTTimerEventPerformer {
	
		private PluginInterface plugin_interface;
		private DownloadCriteria selection;
		private DownloadCriteria exclusion;
		private boolean resume;
	
		private DownloadControl(PluginInterface plugin_interface, DownloadCriteria selection, DownloadCriteria exclusion, boolean resume) {
			this.plugin_interface = plugin_interface;
			this.selection = selection;
			this.exclusion = exclusion;
			this.resume = resume;
		}
	
		@Override
		public void perform(UTTimerEvent event) {
			
			/**
			 * If we are resuming downloads, then we will resume them in order.
			 * This should stop torrents being fired up and then being requeued, because
			 * an earlier placed torrent has just been resumed.
			 * 
			 * If we are pausing downloads, then we will pause them in reverse order.
			 * This is to stop torrents which were queued starting up when the earlier
			 * torrents are paused, only to be paused themselves moments later.
			 */  
			Download[] downloads = plugin_interface.getDownloadManager().getDownloads(true);
			if (!this.resume) {Collections.reverse(Arrays.asList(downloads));}
			for (int i=0; i<downloads.length; i++) {
				if (this.selection.matches(downloads[i])) {
					if (!this.exclusion.matches(downloads[i])) {
						if (this.resume) {downloads[i].resume();}
						else {downloads[i].pause();}
					}
				}
			}
		}
		
	}
	
}
