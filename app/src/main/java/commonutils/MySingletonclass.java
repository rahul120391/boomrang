package commonutils;

import java.util.ArrayList;

import modelclasses.MyFilesDataModel;

/**
 * Created by rahul on 3/21/2015.
 */
public class MySingletonclass {
    private static  MySingletonclass object=null;
    private ArrayList<MyFilesDataModel> list;
    private String searchstring;

    private MySingletonclass(){

    }

    public static synchronized MySingletonclass getobject(){
        if(object==null){
            object=new MySingletonclass();
        }
        return object;
    }

    public String getSearchstring() {
        return searchstring;
    }

    public void setSearchstring(String searchstring) {
        this.searchstring = searchstring;
    }

    public ArrayList<MyFilesDataModel> getList() {
        return list;
    }

    public void setList(ArrayList<MyFilesDataModel> list) {
        this.list = list;
    }


}
