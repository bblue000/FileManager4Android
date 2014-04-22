package org.ixming.android.file;

import java.io.File;

import android.content.Context;

class DataFileManager extends FileManager {

	private File mRootDirPath;
	public DataFileManager(Context context) {
		super(context);
	}
	
	@Override
	public File getRootDirPath() {
		if (null == mRootDirPath) {
			mRootDirPath = getContext().getFilesDir();
		}
		return mRootDirPath;
	}

}
