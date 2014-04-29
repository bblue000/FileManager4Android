package org.ixming.android;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import org.ixming.android.file.FileManager;
import org.ixming.android.file.FileCompositor;
import org.ixming.android.file.R;
import org.ixming.android.file.StorageType;
import org.ixming.io.file.FileUtils;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FileManager.initAppConfig(getApplicationContext(), StorageType.SDCard);
		
		
		FileCompositor fileNameCompositor = FileCompositor
				.obtainFile("yytest.xml");
		Log.d("yytest", "" + fileNameCompositor.getCompositedFileName());
		Log.d("yytest", "before = " + cal(fileNameCompositor.getAbsoluteFile().getAbsolutePath()));
		OutputStream out;
		try {
			out = fileNameCompositor.openFileOutput(true);
			out.write("1234".getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("yytest", "after = " + cal(fileNameCompositor.getAbsoluteFile().getAbsolutePath()));
		
		
		
		fileNameCompositor.recycle();
//		Log.d("yytest", "" + fileNameCompositor.getCompositedFileName());
		FileCompositor fileNameCompositor1 = FileCompositor.obtainRootDir();
		Log.d("yytest", "" + (fileNameCompositor == fileNameCompositor1));
		Log.d("yytest", "" + fileNameCompositor1.getCompositedFileName());
		
		
		fileNameCompositor = FileCompositor.obtainRootDir();
		long size = FileManager.sizeOfFreeByAndroidStatFs(fileNameCompositor
				.getAbsoluteFile(FileManager.getDataFileManager()).getAbsolutePath());
		Log.d("yytest", "" + FileUtils.calFileSizeString(size));
		
		size = FileManager.sizeOfFreeByAndroidStatFs(fileNameCompositor
				.getAbsoluteFile(FileManager.getSDcardFileManager()).getAbsolutePath());
		Log.d("yytest", "" + FileUtils.calFileSizeString(size));
		
		
		
		
//		size = FileManager.sizeOfFreeByAndroidStatFs("/");
//		Log.d("yytest", "" + FileUtils.calFileSizeString(size));
		
//		Log.d("yytest", "" + FileNameCompositor.obtainFromDirAndFile(
//				"yytest", "yytest.xml").getCompositedFileName());
//		
//		try {
//			FileManager.getDataFileManager().openFileOutput(
//					FileNameCompositor.obtainFromFileName("yytest.xml"), true);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			FileManager.getDataFileManager().openFileOutput(
//					FileNameCompositor.obtainFromDirAndFile("yytest", "yytest.xml"), true);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		
//
//		try {
//			FileManager.getSDcardFileManager().openFileOutput(
//					FileNameCompositor.obtainFromFileName("yytest.xml"), true);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			FileManager.getSDcardFileManager().openFileOutput(
//					FileNameCompositor.obtainFromDirAndFile("yytest", "yytest.xml"), true);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	private String cal(String path) {
		StatFs statFs = new StatFs(path);
		statFs.getBlockSize();
		return FileUtils.calFileSizeString(statFs.getBlockCount()
				* statFs.getBlockSize());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
