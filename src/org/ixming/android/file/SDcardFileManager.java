package org.ixming.android.file;

import java.io.File;

import org.ixming.io.file.FileUtils;

import android.content.Context;
import android.os.Environment;

class SDcardFileManager extends FileManager {

	private File mRootDirPath;
	public SDcardFileManager(Context context) {
		super(context);
	}
	
	@Override
	public File getRootDirPath() {
		if (null == mRootDirPath) {
			mRootDirPath = new File(Environment.getExternalStorageDirectory(), 
					getContext().getPackageName());
			if (!mRootDirPath.exists() || mRootDirPath.isFile()) {
				FileUtils.deleteFile(mRootDirPath, true);
				mRootDirPath.mkdirs();
			}
		}
		return mRootDirPath;
	}

}
