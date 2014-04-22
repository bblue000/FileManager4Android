package org.ixming.android.file;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.ixming.android.coding.MD5;

/**
 * FileManager机制中，组合File Name/Path的工具类
 * 
 * @author Yin Yong
 */
public class FileNameCompositor {

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
	
	public static FileNameCompositor obtainFromFileName(String fileName) {
		FileNameCompositor instance = obtainFileNameCompositor();
		setParams(instance, null, fileName);
		return instance;
	}
	
	public static FileNameCompositor obtainFromDirAndFile(
			String dir, String fileName) {
		FileNameCompositor instance = obtainFileNameCompositor();
		setParams(instance, dir, fileName);
		return instance;
	}
	
	public static FileNameCompositor obtainFromUrl(String url) {
		FileNameCompositor instance = obtainFileNameCompositor();
		setParams(instance, null, transfer(url));
		return instance;
	}
	
	public static FileNameCompositor obtainFromUrl(String dir, String url) {
		FileNameCompositor instance = obtainFileNameCompositor();
		setParams(instance, dir, transfer(url));
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
	
	private FileNameCompositor() {}
	
	private String mPath;
	private String mFileName;
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
		checkDirAccuracy(path);
		mPath = path;
	}
	
	protected void setFileName(String fileName) {
		if (null == fileName || "".equals(fileName)) {
			throw new IllegalArgumentException(
					"fileName is null or empty string!");
		}
		checkFileNameAccuracy(fileName);
		mFileName = fileName;
	}
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private void checkDirAccuracy(String path) {
		if (null == path || "".equals(path)) {
			return ;
		}
//		throw new IllegalArgumentException(
//				"invalid dir name = " + path);
	}
	
	/**
	 * TODO 检测-文件夹，文件名-参数是否正确
	 */
	private void checkFileNameAccuracy(String fileName) {
		if (null == fileName || "".equals(fileName)) {
			return ;
		}
		if (fileName.indexOf("/") < 0 && fileName.indexOf("\\") < 0) {
			return ;
		}
		throw new IllegalArgumentException(
				"invalid path/filename = " + fileName);
	}
	
	public File getCompositedFile(StorageType type) {
		return getCompositedFile(type.getFileManager());
	}
	
	public File getCompositedFile(FileManager fileManager) {
		File file = mCompositedFileMap.get(fileManager);
		if (null == file) {
			file = new File(fileManager.getRootDirPath(),
					getCompositedFileName());
			mCompositedFileMap.put(fileManager, file);
		}
		return file;
	}
	
	public String getCompositedFileName() {
		if (null == mCompositedPath
				&& !(null == mPath && null == mFileName)) {
			StringBuilder sb = new StringBuilder();
			if (null != mPath && !"".equals(mPath)) {
				sb.append(mPath);
				sb.append(File.separator);
			}
			if (null != mFileName && !"".equals(mFileName)) {
				sb.append(mFileName);
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
}
