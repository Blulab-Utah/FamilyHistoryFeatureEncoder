package edu.utah.blulab.services;

import java.io.File;
import java.util.Map;

public interface IFeatureEncoder {

    String getEncodedFeatures(File input) throws Exception;
}
