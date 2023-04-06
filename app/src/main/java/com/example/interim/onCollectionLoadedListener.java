package com.example.interim;

import java.util.List;
import java.util.Map;

public interface onCollectionLoadedListener {
    void onCollectionsLoaded(List<Map<String,Object>> usersList, List<Map<String,Object>> prosList );
}
