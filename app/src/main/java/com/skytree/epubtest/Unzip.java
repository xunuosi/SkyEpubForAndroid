package com.skytree.epubtest;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.skytree.epubtest.IOUtils;


import android.os.AsyncTask;
import android.util.Log;

public class Unzip extends Observable {

    private static final String TAG = "UnZip";
    private String mFileName, mFilePath, mDestinationPath;

    public Unzip (String fileName, String filePath, String destinationPath) {
        mFileName = fileName;
        mFilePath = filePath;
        mDestinationPath = destinationPath;
    }

    public String getFileName () {
        return mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getDestinationPath () {
        return mDestinationPath;
    }

    public void unzip () {
        String fullPath = mFilePath + "/" + mFileName;
        Log.d(TAG, "unzipping " + mFileName + " to " + mDestinationPath);
        new UnZipTask().execute(fullPath, mDestinationPath);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {

        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params) {
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while extracting file " + archive, e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setChanged();
            notifyObservers(mFileName);
        }

        private void unzipEntry(ZipFile zipfile, ZipEntry entry,
                String outputDir) throws IOException {

            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            Log.v(TAG, "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            try {
                IOUtils.copy(inputStream, outputStream);
            } finally {
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            Log.v(TAG, "Creating dir " + dir.getName());
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }
} 