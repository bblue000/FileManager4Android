package org.ixming.android;

import org.ixming.android.file.FileManager;
import org.ixming.android.file.FileCompositor;
import org.ixming.android.file.R;
import org.ixming.android.file.StorageType;
import org.ixming.io.file.FileUtils;

import android.os.Bundle;
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
				.obtainByFileName("yytest.xml");
		Log.d("yytest", "" + fileNameCompositor.getCompositedFileName());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
