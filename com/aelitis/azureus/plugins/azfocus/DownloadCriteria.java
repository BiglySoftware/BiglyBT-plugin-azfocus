/**
 * File: DownloadCriteria.java
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

import com.biglybt.pif.download.Download;
import java.util.*;

/**
 * @author Allan Crooks
 *
 */
public abstract class DownloadCriteria {
	public abstract boolean matches(Download download);
	
	public static final DownloadCriteria ALL = new DownloadCriteria() {
		@Override
		public boolean matches(Download download) {return true;}
	};
	
	public static final DownloadCriteria NONE = new DownloadCriteria() {
		@Override
		public boolean matches(Download download) {return false;}
	};
	
	public static DownloadCriteria create(Download download) {
		return new SelectedDownloadCriteria(download);
	};

	public static DownloadCriteria create(Download[] downloads) {
		return new SelectedDownloadCriteria(downloads);
	};
	
	private static class SelectedDownloadCriteria extends DownloadCriteria {
		private Set selected;
		private SelectedDownloadCriteria(Download download) {
			this.selected = Collections.singleton(download);
		}
		private SelectedDownloadCriteria(Download[] downloads) {
			this.selected = new HashSet(Arrays.asList(downloads));
		}
		@Override
		public boolean matches(Download download) {
			return this.selected.contains(download);
		}
	}
	
}
