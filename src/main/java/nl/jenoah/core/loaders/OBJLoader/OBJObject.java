package nl.jenoah.core.loaders.OBJLoader;

import java.util.ArrayList;
import java.util.List;

public class OBJObject {
    private ArrayList<OBJModel> objModels = new ArrayList<>();


    public ArrayList<OBJModel> getObjModels() {
        return objModels;
    }

    public void setObjModels(ArrayList<OBJModel> objModels) {
        this.objModels = objModels;
    }

    public void addObjModel(OBJModel objModel){
        this.objModels.add(objModel);
    }


    public void cleanUp(){
        List<Integer> indicesToRemove = new ArrayList<>();

        for (int i = 0; i < objModels.size(); i++) {
            if(objModels.get(i).getFaces().isEmpty()) indicesToRemove.add(i);
        }

        for (Integer integer : indicesToRemove) {
            objModels.remove((int) integer);
        }
    }
}
