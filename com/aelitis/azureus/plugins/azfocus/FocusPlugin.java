/**
 * File: FocusPlugin.java
 * Library: Focus download plugin for Azureus
 * Date: 19 Jan 2007
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
 * 
 */
package com.aelitis.azureus.plugins.azfocus;

import java.util.ArrayList;

import com.biglybt.pif.Plugin;
import com.biglybt.pif.PluginException;
import com.biglybt.pif.PluginInterface;
import com.biglybt.pif.torrent.TorrentAttribute;
import com.biglybt.pif.download.*;

/**
 * 
 */
public class FocusPlugin implements Plugin {
	
	TorrentAttribute FOCUS_ATTRIBUTE = null;
	TorrentAttribute FORCE_START_ATTRIBUTE = null;
	PluginInterface pi = null;
	
	private DownloadWatcher watcher = new DownloadWatcher();
	private ArrayList watched_dls = new ArrayList();
	private DownloadController dc = null;

	@Override
	public void initialize(PluginInterface plugin_interface) throws PluginException {
		dc = new DownloadController(plugin_interface);
		FOCUS_ATTRIBUTE = plugin_interface.getTorrentManager().getPluginAttribute("has_focus");
		FORCE_START_ATTRIBUTE = plugin_interface.getTorrentManager().getPluginAttribute("has_focus");
		pi = plugin_interface;
		new FocusPluginMenu(this).createMenu();
	}
	
	// Doing this will not affect the state of other downloads not
	// explicitly mentioned here.
	public synchronized void setFocus(Download[] downloads, boolean focus_on, boolean auto_pause) {
		
		boolean something_happened = false;

		// See which downloads need to have their state changed...
		for (int i=0; i<downloads.length; i++) {
			Download download = downloads[i];
			boolean is_focused = download.getBooleanAttribute(this.FOCUS_ATTRIBUTE); 
			if (focus_on) {
				if (is_focused) {continue;}
				something_happened = true;
				if (download.isPaused()) {download.resume();}
				if (!download.isForceStart()) {
					download.setBooleanAttribute(this.FORCE_START_ATTRIBUTE, true);
					download.setForceStart(true);
				}
				download.addListener(this.watcher);
				this.watched_dls.add(download);
			}
			else {
				if (!is_focused) {continue;}
				something_happened = true;
				if (download.getBooleanAttribute(this.FORCE_START_ATTRIBUTE)) {
					download.setForceStart(false);
					download.setBooleanAttribute(this.FORCE_START_ATTRIBUTE, false);
				}
				download.removeListener(this.watcher);
				this.watched_dls.remove(download);
			}
		}
		
		if (!something_happened) {return;}
		if (this.watched_dls.isEmpty()) {
			dc.resumeAllDownloads();
		}
		else if (auto_pause) {
			Download[] watched = new Download[this.watched_dls.size()];
			dc.pauseAllDownloadsExcept((Download[])this.watched_dls.toArray(watched));
		}
		
	}
		
	private class DownloadWatcher implements DownloadListener {
		@Override
		public void positionChanged(Download d, int old_pos, int new_pos) {}
		@Override
		public void stateChanged(Download d, int old_state, int new_state) {
			if (old_state == Download.ST_DOWNLOADING && new_state == Download.ST_SEEDING) {
				FocusPlugin.this.setFocus(new Download[]{d}, false, false);
			} 
		}
	}

	
}
