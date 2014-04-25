package org.ixming.android.file;

/**
 * 预置的一个监听器。
 * 
 * @author Yin Yong
 */
public class StorageListener {
	// default 10KB
	private long mConsideredLowSize = 10L * 1024L * 1024L;
	
	/**
	 * @param size 设置监听者自身认为的“低存储”大小
	 */
	public void setLowStorageSize(long size) {
		if (size > mConsideredLowSize) {
			mConsideredLowSize = size;
		}
	}
	
	/**
	 * 相应的FileManager当前剩余的空间
	 * @param size 当前存储机制剩余的空间大小
	 */
	public void onLowStorageSize(long size) {};
	
	/**
	 * 当应用默认的Storage切换的时候调用。
	 * 
	 * <p>
	 * 	<ul>
	 * 		<li>该种情况多发生在SD卡在开机过程中失效；</li>
	 * 		<li>程序主动调用切换（<b><i>不推荐</i></b>）</li>
	 * 	</ul>
	 * </p>
	 * @param oldType 老的
	 * @param newType 新的
	 */
	public void onStorageTypeChanged(StorageType oldType, StorageType newType) {};
	
	/**
	 * 当没有找到SD卡时调用。
	 */
	public void onNoSDCardFound() {};
}
