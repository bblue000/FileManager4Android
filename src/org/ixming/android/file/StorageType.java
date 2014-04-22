package org.ixming.android.file;

public enum StorageType {

	Data {
		@Override
		public FileManager getFileManager() {
			return FileManager.getDataFileManager();
		}
	},
	
	SDCard {
		@Override
		public FileManager getFileManager() {
			return FileManager.getSDcardFileManager();
		}
	};
	
	public abstract FileManager getFileManager() ;
	
}
