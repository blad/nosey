package com.btellez.noseyexplorer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Inspector {
    public void inspect(Nosey nosey);

    public static class ModelNameInspector implements Inspector {

        private Nosey nosey;
        private List<String> modelNames;

        @Override
        public void inspect(Nosey nosey) {
            this.nosey = nosey;
        }

        public List<String> getModelNames() {
            if (modelNames == null) {
                modelNames = new ArrayList<String>();
                if (nosey != null) {
                    modelNames.addAll(nosey.objectTypes.keySet());
                    Collections.sort(modelNames);
                }
            }
            return modelNames;
        }
    }

    public static class ModelMapInspector implements Inspector {
        private Nosey nosey;
        private Map<String, Class> modelMap;

        @Override
        public void inspect(Nosey nosey) {
            this.nosey = nosey;
        }
        
        public Map<String, Class> getModelMap() {
            return nosey.objectTypes;
        }
    }
}
