package com.amaze.filemanager.services.asynctasks;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.adarshr.raroscope.RARFile;
import com.amaze.filemanager.R;
import com.amaze.filemanager.activities.MainActivity;
import com.amaze.filemanager.adapters.ZipAdapter;
import com.amaze.filemanager.fragments.ZipViewer;
import com.amaze.filemanager.utils.Layoutelements;
import com.amaze.filemanager.utils.ZipObj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Vishal on 11/23/2014.
 */
public class ZipHelperTask extends AsyncTask<File, Void, ArrayList<ZipObj>> {

    ZipViewer zipViewer;
    String dir;

    public ZipHelperTask(ZipViewer zipViewer, String dir) {

        this.zipViewer = zipViewer;
        this.dir = dir;
    }

    public ZipHelperTask(ZipViewer zipViewer, int counter) {
        this.zipViewer = zipViewer;

    }

    @Override
    protected ArrayList<ZipObj> doInBackground(File... params) {
        ArrayList<ZipObj> elements = new ArrayList<ZipObj>();

        try {
            ZipFile zipfile = new ZipFile(params[0]);
            int i = 0;
            if (zipViewer.wholelist.size() == 0) {
                for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    zipViewer.wholelist.add(entry);
                }
            }ArrayList<String> strings=new ArrayList<String>();
            //  int fileCount = zipfile.size();

            for (ZipEntry entry : zipViewer.wholelist) {

                i++;
                String s = entry.getName().toString();
                //System.out.println(s);
                File file = new File(entry.getName());
                    if(dir==null || dir.trim().length()==0){
                        if (file.getParent() == null) {
                            elements.add(new ZipObj(entry, entry.isDirectory()));
                            zipViewer.results = false;
                            strings.add(entry.getName());
                        } else {
                            String path=entry.getName().substring(0, entry.getName().indexOf("/")+1);
                            if(!strings.contains(path)){
                                ZipObj zipObj = new ZipObj(new ZipEntry(entry.getName().substring(0, entry.getName().indexOf("/")+1)), true);
                                strings.add(path);
                                elements.add(zipObj);}

                        }
                    }
                    else{
                    //Log.d("Test", dir);
                    if (file.getParent()!=null && file.getParent().equals(dir)) {
                        elements.add(new ZipObj(entry,entry.isDirectory()));
                        zipViewer.results = true;
                        strings.add(entry.getName());
                    }else {
                        if(entry.getName().startsWith(dir+"/")){
                        String path1=entry.getName().substring(dir.length()+1,entry.getName().length());

                        int index=dir.length()+1+path1.indexOf("/");
                        String path=entry.getName().substring(0, index+1);
                        if(!strings.contains(path)){
                            ZipObj zipObj = new ZipObj(new ZipEntry(entry.getName().substring(0, index+1)), true);
                            strings.add(path);
                            //System.out.println(path);
                            elements.add(zipObj);}}}

                    } }/*else if (counter==2) {
                    if (file.getParent()!=null && file.getParent().equals(dir)) {

                        elements.add(entry);
                        zipViewer.results = true;
                    } else if (file.getParent()==null) {
                        if (dir==null) {

                            elements.add(entry);
                            zipViewer.results = false;
                        }
                    }
                }
            }
        if(counter ==0 && elements.size()==0 && i!=0){
            for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) e.nextElement();
                elements.add(entry);
            }
            }
        */}catch (Exception e){e.printStackTrace();}

                Collections.sort(elements, new FileListSorter());
                zipViewer.elements = elements;
                return elements;
            }

            @Override
            protected void onPostExecute (ArrayList < ZipObj > zipEntries) {
                super.onPostExecute(zipEntries);
                zipViewer.zipAdapter = new ZipAdapter(zipViewer.getActivity(), R.layout.simplerow, zipEntries, zipViewer);
                zipViewer.setListAdapter(zipViewer.zipAdapter);
                zipViewer.current = dir;
                ((TextView) zipViewer.getActivity().findViewById(R.id.fullpath)).setText(zipViewer.current);

            }
            class FileListSorter implements Comparator<ZipObj> {


                public FileListSorter() {

                }

                @Override
                public int compare(ZipObj file1, ZipObj file2) {
                    if (file1.isDirectory() && !file2.isDirectory()) {
                        return -1;


                    } else if (file2.isDirectory() && !(file1).isDirectory()) {
                        return 1;
                    }
                    return file1.getEntry().getName().compareToIgnoreCase(file2.getEntry().getName());
                }
            }
        }
