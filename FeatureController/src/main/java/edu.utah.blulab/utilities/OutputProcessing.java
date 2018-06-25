package edu.utah.blulab.utilities;

public class OutputProcessing {

    public static String processOutput(String content){
        String separator = "\t";

        String[] lines = content.split("\n");

        String columns[] = lines[0].split(separator);

        int id = 0;
        String idStr;
        int minLocation = 999999;
        int maxLocation = -1;

        int typeColumn = 0;
        int idColumn = 0;
        int docColumn = 0;
        int variableColumn = 0;
        int annotationColumn = 0;
        for (int i = 0; i < lines.length; i++) {
            String values[] = lines[i].split(separator);
            idStr = String.valueOf(id);

            // use header to identify columns

            if (i == 0){
                System.out.println("HERE!!!!");
                for (int j = 1; j < values.length; j++) {
                    System.out.println("HERE1!!!!    " + values[j]);
                    if (values[j].equals("Type")){
                        typeColumn = j;
                        System.out.println("HERE!!!!");
                    } else if (values[j].equals("Id")){
                        idColumn = j;
                    }  else if (values[j].equals("Document")){
                        docColumn = j;
                    } else if (values[j].equals("Annotation_Variable")){
                        variableColumn = j;
                    } else if (values[j].equals("Annotations")) {
                        annotationColumn = j;
                    }
                }
            }

            // skip the "Rejected annotations"
            if (values[typeColumn].equals("Rejected")){
                continue;
            }

            // iterate all lines with the same id
            String typeVal;
            String idVal;
            String docVal;
            String variableVal;
            String annotationVal;
            System.out.println(values[idColumn] + "  ---  " + idStr);
            if (values[idColumn].equals(idStr)){
                //TODO: create a single annotation line for each id
                annotationVal = values[annotationColumn];
                String[] vals = content.split("/");
                int startLoc = 99999;
                try {
                    startLoc = Integer.valueOf(vals[vals.length -1]);
                } catch (Exception ex ){
                    System.out.println("Could not convert " + vals[vals.length -1] + "  to an integer");
                }
                int endLoc = startLoc + vals[0].length(); // end location is the start location + length of the annotation
                if (startLoc < minLocation) {
                    minLocation = startLoc;
                    System.out.println("Min location set to : " + startLoc);
                }
                if (endLoc > maxLocation) {
                    maxLocation = endLoc;
                    System.out.println("Max location set to: " + endLoc);
                }

            } else { // if a new id is found
                id += 1;
                minLocation = 999999;
                maxLocation = -1;
//                StringBuilder sb = new StringBuilder();
//                for (int j = 1; j < values.length; j++) {
//                    sb.append(values[j]);
//                }
                typeVal = values[typeColumn];
                docVal = values[docColumn];
                variableVal = values[variableColumn];


            }

            for (int j = 1; j < values.length; j++) {
                System.out.println(values[j]);
            }
            //array.addObject(json);
        }
        return "";
    }
}
