package org.ixming.android.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

/**
 * 基础模块之一：文件管理模块
 * 
 * @author Yin Yong
 */
public abstract class FileManager {

	private static final String TAG = FileManager.class.getSimpleName();
	
	public static final int FILE_BUFFER_SIZE = 512;
	
	private static Context sApplicationContext;
	private static FileManager sDataInstance;
	private static FileManager sSDcardInstance;
	// application
	private static StorageType sPreferredStorageType;
	private static StorageType sCurrentStorageType;
	private static FileManager sCurrentInstance;
	@SuppressWarnings("unused")
	private static Set<StorageListener> sStorageListeners;
	/**
	 * 初始化文件管理器
	 * 
	 * @param context 应用的Context对象
	 * @param defType 设置默认的存储类型
	 * @see {@link StorageType}
	 */
	public static void initAppConfig(Context context, StorageType defType) {
		sApplicationContext = context.getApplicationContext();
		getAppFileManager();
		if (null != defType) {
			sPreferredStorageType = defType;
		}
		obtainJITStorageState(true);
	}
	
	/**
	 * 获得应用当前使用的的文件管理类
	 */
	public synchronized static FileManager getAppFileManager() {
		if (null == sApplicationContext) {
			throw new UnsupportedOperationException("FileManager hasn't initialized!");
		}
		if (null == sDataInstance) {
			sDataInstance = new DataFileManager(sApplicationContext);
		}
		if (null == sSDcardInstance) {
			sSDcardInstance = new SDcardFileManager(sApplicationContext);
		}
		
		// default prefer SD card storage
		if (null == sPreferredStorageType) {
			sPreferredStorageType = StorageType.SDCard;
			obtainJITStorageState(true);
		}
		obtainJITStorageState(false);
		return sCurrentInstance;
	}
	
	/**
	 * 获得内存储的文件管理类
	 */
	public synchronized static FileManager getDataFileManager() {
		getAppFileManager();
		return sDataInstance;
	}
	
	/**
	 * 获得SD卡存储的文件管理类
	 */
	public synchronized static FileManager getSDcardFileManager() {
		getAppFileManager();
		return sSDcardInstance;
	}
	
	private Context mContext;
	protected FileManager(Context context) {
		mContext = context;
	}
	
	/**
	 * 从所有的FileManager中查找，一旦找到返回存储类型
	 * 
	 * @param context
	 * @param fileNameCompositor
	 * @return null if not exist in all file systems
	 */
	public static StorageType existsWithinAllManagers(
			FileNameCompositor fileNameCompositor) {
		StorageType types[] = StorageType.values();
		for (int i = 0; i < types.length; i++) {
			FileManager fileManager = types[i].getFileManager();
			if (fileManager.exists(fileNameCompositor)) {
				return types[i];
			}
		}
		return null;
	}
	
	/**
	 * 当前FileManager对应的文件中是否存在
	 */
	public boolean exists(FileNameCompositor fileNameCompositor) {
		File file = fileNameCompositor.getCompositedFile(this);
		return file.exists();
	}
	
	/**
	 * @param path 不需要根目录地址前缀
	 * @return path指定的FileInputStream
	 * 
	 * @throws FileNotFoundException 如果找不到文件，将抛出该异常/访问权限、异常等
	 * @throws IOException [其他情况] 如果对应地址为文件夹，将抛出该异常
	 */
	public FileInputStream openFileInput(FileNameCompositor file)
			throws FileNotFoundException, IOException {
		File src = file.getCompositedFile(this);
		if (src.exists()) {
			if (src.isFile()) {
				return new FileInputStream(src);
			} else {
				throw new IOException("file = { " 
						+ src.getAbsolutePath() + " } is not a file!");
			}
		} else {
			throw new FileNotFoundException("file = { " + src.getAbsolutePath()
					+ " } is not found!");
		}
	}
	
	/**
	 * {@link #openFileOutput(FileNameCompositor, boolean, false)}
	 * 
	 * @param file <b>注意：</b>不需要根目录地址前缀
	 * @param createIfUnExists 如果文件不存在，是否创建
	 * 
	 * @return path指定的FileOutputStream
	 */
	public FileOutputStream openFileOutput(FileNameCompositor file,
			boolean createIfUnExists)
					throws FileNotFoundException, IOException {
		return openFileOutput(file, createIfUnExists, false);
	}
	
	/**
	 * @param file <b>注意：</b>不需要根目录地址前缀
	 * @param createIfUnExists 如果文件不存在，是否创建
	 * @param append If append is true and the file already exists, 
	 * it will be appended to; otherwise it will be truncated
	 * 
	 * @return path指定的FileOutputStream
	 * 
	 * @throws FileNotFoundException 文件不存在且createIfUnExists == false
	 * @throws IOException 文件创建失败
	 */
	public FileOutputStream openFileOutput(FileNameCompositor file,
			boolean createIfUnExists, boolean append)
			throws FileNotFoundException, IOException {
		File src = file.getCompositedFile(this);
		boolean fileExists = false;
		if (src.exists()) {
			fileExists = src.isFile();
		} else {
			if (createIfUnExists) {
				if (!src.exists()) {
					if (null != src.getParentFile()) {
						src.getParentFile().mkdirs();
					}
					try {
						src.createNewFile();
					} catch (IOException e) {
						throw e;
					}
				}
				fileExists = src.exists();
			}
		}
		if (fileExists) {
			return new FileOutputStream(src, append);
		}
		throw new FileNotFoundException("file = { " + src.getAbsolutePath()
				+ " } not found!");
	}
	
	/**
	 * 相当于save(String path, InputStream ins, false)
	 */
	public boolean save(FileNameCompositor file, InputStream ins)
			throws Exception {
		return save(file, ins, false);
	}
	
	public boolean save(FileNameCompositor file, InputStream ins,
			boolean append) throws Exception {
		FileOutputStream out = null;
		try {
			out = openFileOutput(file, append);
			byte[] buf = new byte[FILE_BUFFER_SIZE];
			int len = -1;
			while (-1 != (len = ins.read(buf))) {
				out.write(buf, 0, len);
			}
			return true;
		} catch (Exception e) {
			LogUtils.e(TAG, "save Exception : " + e.getMessage());
			throw e;
		} finally {
			try {
				if (null != out)
					out.close();
			} catch (Exception ignore) { }
		}
	}
	
	private static long sLastObtainTime = 0;
	private static final long TIME_OFFSET = 1 * 60 * 1000;
	/**
	 * 即时检查当前存储状态
	 * @param force 强制执行一次当前存储的检查
	 */
	private synchronized static void obtainJITStorageState(boolean force) {
		long time = System.currentTimeMillis();
		if (!force) {
			if (time - sLastObtainTime < TIME_OFFSET) {
				return ;
			}
		} else {
			sLastObtainTime = time;
		}
		StorageType oldStorageType = sCurrentStorageType;
		switch (sPreferredStorageType) {
		case Data:
			if (sCurrentInstance != sDataInstance) {
				sCurrentInstance = sDataInstance;
			}
			sCurrentStorageType = StorageType.Data;
			break;
		case SDCard:
		default:
			if (existSDcard()) {
				sCurrentInstance = sSDcardInstance;
				sCurrentStorageType = StorageType.SDCard;
			} else {
				sCurrentInstance = sDataInstance;
				sCurrentStorageType = StorageType.Data;
			}
			break;
		}
		if (oldStorageType != sCurrentStorageType) {
			// has changed
			
		}
	}
	
	/**
	 * 获取相应FileManager的根目录地址
	 */
	public abstract File getRootDirPath() ;
	
	public Context getContext() {
		return mContext;
	}
	
	/**
	 * @return 如果存在SD卡，则返回TRUE
	 */
	public static boolean existSDcard() {
		return Environment.MEDIA_MOUNTED
				.equals(Environment.getExternalStorageState());
	}
	
	public static long sizeOfFreeByAndroidStatFs(String path) {
		StatFs statFs = new StatFs(path);
		long blockSize = statFs.getBlockSize();
		long aBlocks = statFs.getAvailableBlocks();
		long aBlockSum = blockSize * aBlocks;
		return aBlockSum;
	}
	
	/**
	 * @param path absolute path
	 * @param size given enough size
	 * @return 如果指定路径的可用存储空间大于size，返回TURE
	 */
	public static boolean hasEnoughSize(String path, long size) {
		return sizeOfFreeByAndroidStatFs(path) >= size;
	}
	
}