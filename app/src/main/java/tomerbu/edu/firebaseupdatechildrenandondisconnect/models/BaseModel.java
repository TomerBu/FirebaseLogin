package tomerbu.edu.firebaseupdatechildrenandondisconnect.models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by tomerbuzaglo on 06/09/2016.
 * Copyright 2016 tomerbuzaglo. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0
 * you may not use this file except
 * in compliance with the License
 */

@IgnoreExtraProperties
public class BaseModel {

    @Exclude
    public HashMap<String, Object> toMap() {

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> cls =
                new TypeReference<HashMap<String, Object>>() {};

        return mapper.convertValue(this, cls);
    }
}


