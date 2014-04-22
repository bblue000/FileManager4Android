package org.ixming.android.file;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * SD卡下的文件管理
 * 
 * @author Yin Yong
 */
class SDcardFileManager extends FileManager {

	private File mRootDirPath;
	public SDcardFileManager(Context context) {
		super(context);
	}
	
	@Override
	public synchronized File getRootDirPath() {
		if (null == mRootDirPath) {
			mRootDirPath = new File(Environment.getExternalStorageDirectory(), 
					getContext().getPackageName());
		}
		if (mRootDirPath.isFile()) {
			try {
				mRootDirPath.delete();
			} catch (Exception ignore) { }
		}
		if (!mRootDirPath.exists()) {
			mRootDirPath.mkdirs();
		}
		return mRootDirPath;
	}

}
