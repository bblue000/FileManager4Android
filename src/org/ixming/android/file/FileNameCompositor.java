package org.ixming.android.file;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.ixming.android.coding.MD5;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * FileManager机制中，组合File Name/Path的工具类
 * 
 * @author Yin Yong
 */
public class FileNameCompositor implements Parcelable {

	private static final int MAX_POOL_SIZE = 3;
	private static final LinkedList<FileNameCompositor> sPoolList
		= new LinkedList<FileNameCompositor>();
//	private static final SimpleInstancePool<FileNameCompositor> sPool
//		= new SimpleInstancePool<FileNameCompositor>(MAX_POOL_SIZE,
//				new SimplePoolControl<FileNameCompositor>() {
//			public FileNameCompositor createInstance(Object...args) {
//				return new FileNameCompositor();
//			};
//			
//			public void recycleInstance(FileNameCompositor e) {
//				if (null != e) {
//					e.empty();
//				}
//			}
//	});
	
	private static FileNameCompositor obtainFileNameCompositor() {
		synchronized (sPoolList) {
			if (!sPoolList.isEmpty()) {
				return sPoolList.remove();
			}
			return new FileNameCompositor();
		}
//		return sPool.obtain();
	}
	
	/**
	 * 获得根目录的FileNameCompositor对象
	 */
	public static FileNameCompositor obtainRootDir() {
		FileNameCompositor instance = obtainFileNameCompositor();
		setParams(instance, null, null);
		return instance;
	}
	
	/**
	 * @param dir 单纯的文件夹名称（两端不需要“/”）
	 */
	public static FileNameCompositor obtainByDir(String dir) {
		FileNameCompositor instance = obtainFileNameCompositor();
		checkDirAccuracy(dir);
		setParams(instance, dir, null);
		return instance;
	}
	
	/**
	 * @param fileName 单纯的文件名称
	 */
	public static FileNameCompositor obtainByFileName(String fileName) {
		FileNameCompositor instance = obtainFileNameCompositor();
		checkFileNameAccuracy(fileName);
		setParams(instance, null, fileName);
		return instance;
	}
	
	/**
	 * @param dir 指定文件的父文件夹（两端不需要“/”）
	 * @param fileName 单纯的文件名称
	 */
	public static FileNameCompositor obtainByDirAndFile(
			String dir, String fileName) {
		FileNameCompositor instance = obtainFileNameCompositor();
		checkDirAccuracy(dir);
		checkFileNameAccuracy(fileName);
		setParams(instance, dir, fileName);
		return instance;
	}
	
	/**
	 * @param url 网络图片地址
	 * @param suffix 文件后缀，可以为空
	 */
	public static FileNameCompositor obtainByUrl(String url, String suffix) {
		String fileName = transfer(url);
		FileNameCompositor instance = obtainByFileName(fileName);
		instance.setSuffix(suffix);
		return instance;
	}
	
	/**
	 * @param dir 指定文件的父文件夹（两端不需要“/”）
	 * @param url 网络图片地址
	 * @param suffix 文件后缀，可以为空
	 */
	public static FileNameCompositor obtainByUrlWithDir(String dir, String url,
			String suffix) {
		String fileName = transfer(url);
		FileNameCompositor instance = obtainByDirAndFile(dir, fileName);
		instance.setSuffix(suffix);
		return instance;
	}
	
	private static void setParams(FileNameCompositor instance,
			String dir, String fileName) {
		instance.empty();
		instance.setPath(dir);
		instance.setFileName(fileName);
	}
	
	/**
	 * TODO
	 * url 转换成文件名的方法
	 */
	private static final String transfer(String url) {
		return MD5.digest2Str(url);
	}
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private static void checkDirAccuracy(String dir) {
		if (!isEmptyString(dir)) {
			return ;
		}
		throw new IllegalArgumentException("invalid dir name = " + dir);
	}
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private static void checkFileNameAccuracy(String fileName) {
		if (!isEmptyString(fileName) && fileName.indexOf("/") < 0
				&& fileName.indexOf("\\") < 0) {
			return ;
		}
		throw new IllegalArgumentException(
				"invalid file name = " + fileName);
	}
	
	private static boolean isEmptyString(String name) {
		return null == name || "".equals(name);
	}
	
	// >>>>>>>>>>>>>>
	// internal implements
	private FileNameCompositor() {}
	
	private String mPath;
	private String mFileName;
	private String mSuffix;
	private String mCompositedPath;
	// cache size
	private final Map<FileManager, File> mCompositedFileMap
		= new HashMap<FileManager, File>(StorageType.values().length);
	protected void empty() {
		mPath = null;
		mFileName = null;
		mCompositedPath = null;
		mCompositedFileMap.clear();
	}
	
	protected void setPath(String path) {
		mPath = path;
	}
	
	protected void setFileName(String fileName) {
		mFileName = fileName;
	}
	
	// extra settings
	// suffix
	protected void setSuffix(String suffix) {
		mSuffix = suffix;
	}
	
	public File getCompositedFile(StorageType type) {
		return getCompositedFile(type.getFileManager());
	}
	
	public File getCompositedFile(FileManager fileManager) {
		File file = mCompositedFileMap.get(fileManager);
		if (null == file) {
			String compositedFileName = getCompositedFileName();
			if (isEmptyString(compositedFileName)) {
				file = fileManager.getRootDirPath();
			} else {
				file = new File(fileManager.getRootDirPath(),
						getCompositedFileName());
			}
			mCompositedFileMap.put(fileManager, file);
		}
		return file;
	}
	
	public String getCompositedFileName() {
		if (isEmptyString(mCompositedPath)
				&& !(isEmptyString(mPath) && isEmptyString(mFileName))) {
			StringBuilder sb = new StringBuilder();
			if (!isEmptyString(mPath)) {
				sb.append(mPath);
				sb.append(File.separator);
			}
			if (!isEmptyString(mFileName)) {
				sb.append(mFileName);
				if (!isEmptyString(mSuffix)) {
					sb.append(mSuffix);
				}
			}
			return mCompositedPath = sb.toString();
		}
		return mCompositedPath;
	}
	
	@Override
	public String toString() {
		return "{ path = " + mPath + ", file = " + mFileName + " }";
	}
	
	/**
	 * 回收对象
	 * <br/><br/>
	 * 使用对象后，调用该方法是个习惯^_^
	 */
	public void recycle() {
		empty();
		synchronized (sPoolList) {
			if (sPoolList.size() < MAX_POOL_SIZE) {
				sPoolList.add(this);	
			}
		}
//		sPool.recycle(this);
	}

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mPath);
		dest.writeString(mFileName);
		dest.writeString(mSuffix);
	}
	
	public static final Parcelable.Creator<FileNameCompositor> CREATOR
		= new Parcelable.Creator<FileNameCompositor>() {
		public FileNameCompositor createFromParcel(Parcel in) {
			return FileNameCompositor.obtainFileNameCompositor().readFromParcel(in);
		}

		public FileNameCompositor[] newArray(int size) {
			return new FileNameCompositor[size];
		}
	};

	protected FileNameCompositor readFromParcel(Parcel in) {
		mPath = in.readString();
		mFileName = in.readString();
		mSuffix = in.readString();
		return this;
	}
	
}
